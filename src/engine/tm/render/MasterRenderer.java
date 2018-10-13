package engine.tm.render;

import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import engine.Window;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IPlayer;
import engine.interfaces.IScene;
import engine.tm.animation.animatedModel.AnimatedModel;
import engine.tm.animation.renderer.AnimatedModelRenderer;
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.EntityRenderer;
import engine.tm.entities.Light;
import engine.tm.fbos.Attachment;
import engine.tm.fbos.Fbo;
import engine.tm.fbos.RenderBufferAttachment;
import engine.tm.fbos.TextureAttachment;
import engine.tm.gui.GuiRenderer;
import engine.tm.gui.fonts.TextMaster;
import engine.tm.lowPoly.TerrainRendererLowPoly;
import engine.tm.lowPoly.WaterRendererLowPoly;
import engine.tm.models.TexturedModel;
import engine.tm.normalMapping.NormalMappingRenderer;
import engine.tm.particles.ParticleMaster;
import engine.tm.scene.Scene;
import engine.tm.scene.SceneLowPoly;
import engine.tm.shadows.ShadowMapMasterRenderer;
import engine.tm.skybox.SkyboxRenderer;
import engine.tm.sunRenderer.SunRenderer;
import engine.tm.terrains.TerrainRenderer;
import engine.tm.water.Water;
import engine.tm.water.WaterRenderer;

public class MasterRenderer implements IMasterRenderer {

	private static Matrix4f projectionMatrix;

	private static TerrainRenderer terrainRenderer;
	private static EntityRenderer entityRenderer;
	private static NormalMappingRenderer normalMappingRenderer;
	private static AnimatedModelRenderer animatedModelRenderer;
	private static SkyboxRenderer skyboxRenderer;
	private static WaterRenderer waterRenderer;
	private static ShadowMapMasterRenderer shadowMapRenderer;
	private static GuiRenderer guiRenderer;
	private static SunRenderer sunRenderer;
	private static Fbo reflectionFbo;
	private static Fbo refractionFbo;

	public MasterRenderer() {
		createProjectionMatrix();
		terrainRenderer = new TerrainRenderer(projectionMatrix);
		entityRenderer = new EntityRenderer(projectionMatrix);
		normalMappingRenderer = new NormalMappingRenderer(projectionMatrix);
		animatedModelRenderer = new AnimatedModelRenderer(projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(projectionMatrix);
		waterRenderer = new WaterRenderer(projectionMatrix);
		shadowMapRenderer = new ShadowMapMasterRenderer();
		sunRenderer = new SunRenderer();
		guiRenderer = new GuiRenderer();
		reflectionFbo = createWaterFbo(Window.width, Window.height, false);
		refractionFbo = createWaterFbo(Window.width / 2, Window.height / 2, true);
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#init(engine.IScene)
	 */
	@Override
	public void init(IScene scene) {
		entityRenderer.init(scene);
		normalMappingRenderer.init(scene);
		shadowMapRenderer.init(scene);
	}

	public static WaterRenderer getWaterRenderer() {
		return waterRenderer;
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#render(engine.Window, engine.IScene)
	 */
	@Override
	public void render(Window window, IScene scene) {

		Vector4f clipPlane;
		Camera camera = (Camera) ((Scene) scene).getCamera();
		TextMaster textMaster = ((Scene) scene).getTextMaster();
		IPlayer player = ((Scene) scene).getPlayer();
		Vector3f lightDirection = ((Scene) scene).getLightDirection();

		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		renderShadowMap(scene);

		// render reflection texture
		waterRenderer.getFBOs().bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y - Water.HEIGHT);
		camera.getPosition().y -= distance;
		camera.invertPitch();
		camera.invertRoll();

		clipPlane = new Vector4f(0, 1, 0, -Water.HEIGHT);
		prepare();
		skyboxRenderer.render(scene, clipPlane);
		terrainRenderer.render(scene, clipPlane, shadowMapRenderer.getToShadowMapSpaceMatrix());
		entityRenderer.render(scene, clipPlane);
		normalMappingRenderer.render(scene, clipPlane);
		if (player instanceof AnimatedModel) {
			animatedModelRenderer.render((AnimatedModel) player, camera, lightDirection, clipPlane);
		}

		camera.getPosition().y += distance;
		camera.invertPitch();
		camera.invertRoll();

		// render refraction texture
		waterRenderer.getFBOs().bindRefractionFrameBuffer();

		clipPlane = new Vector4f(0, -1, 0, Water.HEIGHT);
		prepare();
		skyboxRenderer.render(scene, clipPlane);
		terrainRenderer.render(scene, clipPlane, shadowMapRenderer.getToShadowMapSpaceMatrix());
		entityRenderer.render(scene, clipPlane);
		normalMappingRenderer.render(scene, clipPlane);
		if (player instanceof AnimatedModel) {
			animatedModelRenderer.render((AnimatedModel) player, camera, lightDirection, clipPlane);
		}
		((Scene) scene).getFlareManager().render(scene);

		renderMinimap(scene);

		// render to screen
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		waterRenderer.getFBOs().unbindCurrentFrameBuffer();

		clipPlane = new Vector4f(0, 0, 0, 0);
		prepare();
		skyboxRenderer.render(scene, clipPlane);
		sunRenderer.render(scene);
		terrainRenderer.render(scene, clipPlane, shadowMapRenderer.getToShadowMapSpaceMatrix());
		entityRenderer.render(scene, clipPlane);
		normalMappingRenderer.render(scene, clipPlane);
		if (player instanceof AnimatedModel) {
			animatedModelRenderer.render((AnimatedModel) player, camera, lightDirection, clipPlane);
		}
		((Scene) scene).getFlareManager().render(scene);

		waterRenderer.render(scene);

		// after the 3D stuff and before the 2D stuff
		ParticleMaster.renderParticles(camera);

		guiRenderer.render(scene);
		textMaster.render();

		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}

	/**
	 * Sets up an FBO for one of the extra render passes. The FBO is initialized
	 * with a texture color attachment, and can be initialized with either a
	 * render buffer or texture attachment for the depth buffer.
	 *
	 * @param width
	 *            - The width of the FBO in pixels.
	 * @param height
	 *            - The height of the FBO in pixels.
	 * @param useTextureForDepth
	 *            - Whether the depth buffer attachment should be a texture or a
	 *            render buffer.
	 * @return The completed FBO.
	 */
	private static Fbo createWaterFbo(int width, int height, boolean useTextureForDepth) {
		Attachment colourAttach = new TextureAttachment(GL11.GL_RGBA8);
		Attachment depthAttach;
		if (useTextureForDepth) {
			depthAttach = new TextureAttachment(GL14.GL_DEPTH_COMPONENT24);
		} else {
			depthAttach = new RenderBufferAttachment(GL14.GL_DEPTH_COMPONENT24);
		}
		return Fbo.newFbo(width, height).addColorAttachment(0, colourAttach).addDepthAttachment(depthAttach).init();
	}

	public void renderScene(IScene scene, Vector4f clipPlane) {
	}

	public void renderShadowMap(IScene scene) {
		Map<TexturedModel, List<Entity>> entities = ((Scene) scene).getEntityList();
		Light sun = ((Scene) scene).getLights().get(0);
		shadowMapRenderer.render(((Scene) scene).getPlayer(), entities, sun);
	}

	public static int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}

	public void renderMinimap(IScene scene) {

		Camera camera = (Camera) ((Scene) scene).getCamera();
		IPlayer player = ((Scene) scene).getPlayer();
		Vector4f clipPlane = new Vector4f(0, 1, 0, -Water.HEIGHT);

		// render minimap
		waterRenderer.getFBOs().bindMinimapFrameBuffer();
		Vector3f cameraPosition = camera.getPosition();
		float currentPitch = camera.getPitch();
		float currentYaw = camera.getYaw();
		camera.setPosition(new Vector3f(player.getPosition().x, 620, player.getPosition().z));
		camera.setPitch(90);
		camera.setYaw(currentYaw - 180);
		prepare();
		terrainRenderer.render(scene, clipPlane);
		entityRenderer.render(scene);
		normalMappingRenderer.render(scene);
		camera.setPosition(cameraPosition);
		camera.setPitch(currentPitch);
		camera.setYaw(currentYaw);
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#prepare()
	 */
	@Override
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
	}

	private static Matrix4f createProjectionMatrix() {
		projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Window.width / (float) Window.height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * FAR_PLANE * NEAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);

		return projectionMatrix;
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#prepare()
	 */
	public Matrix4f getProjectionMatrix() {
		if (projectionMatrix == null) {
			createProjectionMatrix();
		}
		return projectionMatrix;
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#cleanUp(engine.IScene)
	 */
	@Override
	public void cleanUp() {
		terrainRenderer.cleanUp();
		entityRenderer.cleanUp();
		normalMappingRenderer.cleanUp();
		animatedModelRenderer.cleanUp();
		skyboxRenderer.cleanUp();
		waterRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
		guiRenderer.cleanUp();
		refractionFbo.delete();
		reflectionFbo.delete();
	}
}
