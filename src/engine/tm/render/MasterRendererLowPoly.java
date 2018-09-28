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
import engine.IScene;
import engine.Window;
import engine.tm.animation.animatedModel.AnimatedModel;
import engine.tm.animation.renderer.AnimatedModelRenderer;
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.EntityRenderer;
import engine.tm.entities.IPlayer;
import engine.tm.entities.Light;
import engine.tm.entities.LightDirectional;
import engine.tm.fbos.Attachment;
import engine.tm.fbos.Fbo;
import engine.tm.fbos.RenderBufferAttachment;
import engine.tm.fbos.TextureAttachment;
import engine.tm.gui.GuiRenderer;
import engine.tm.gui.fonts.TextMaster;
import engine.tm.lowPoly.TerrainLowPoly;
import engine.tm.lowPoly.TerrainRendererLowPoly;
import engine.tm.lowPoly.WaterRendererLowPoly;
import engine.tm.lowPoly.WaterTileLowPoly;
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

public class MasterRendererLowPoly implements IMasterRenderer {

	private static Matrix4f projectionMatrix;

	private static TerrainRendererLowPoly terrainRendererLowPoly;
	private static WaterRendererLowPoly waterRendererLowPoly;
	private static Fbo reflectionFbo;
	private static Fbo refractionFbo;

	private static AnimatedModelRenderer animatedModelRenderer;
	private static SkyboxRenderer skyboxRenderer;
	private static SunRenderer sunRenderer;
	private static GuiRenderer guiRenderer;

	public MasterRendererLowPoly() {
		createProjectionMatrix();

		terrainRendererLowPoly = new TerrainRendererLowPoly(true);
		waterRendererLowPoly = new WaterRendererLowPoly();
		reflectionFbo = createWaterFbo(Window.width, Window.height, false);
		refractionFbo = createWaterFbo(Window.width / 2, Window.height / 2, true);

		animatedModelRenderer = new AnimatedModelRenderer(projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(projectionMatrix);
		sunRenderer = new SunRenderer();
		guiRenderer = new GuiRenderer();

	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#init(engine.IScene)
	 */
	@Override
	public void init(IScene scene) {
	}

	public static WaterRendererLowPoly getWaterRenderer() {
		return waterRendererLowPoly;
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#render(engine.Window, engine.IScene)
	 */
	@Override
	public void render(Window window, IScene scene) {

		Vector4f clipPlane;
		Camera camera = (Camera) scene.getCamera();
		IPlayer player = scene.getPlayer();
		Vector3f lightDirection = scene.getLightDirection();
		LightDirectional lightDirectional = ((SceneLowPoly) scene).getLightDirectional();
		TerrainLowPoly terrainLowPoly = ((SceneLowPoly) scene).getTerrainLowPoly();
		WaterTileLowPoly waterLowPoly = ((SceneLowPoly) scene).getWaterLowPoly();

		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		// render reflection texture
		prepare();
		clipPlane = new Vector4f(0, 1, 0, -Water.HEIGHT);
		float distance = 2 * (camera.getPosition().y - Water.HEIGHT);
		camera.getPosition().y -= distance;
		camera.invertPitch();
		camera.invertRoll();
		reflectionFbo.bindForRender(1);
		skyboxRenderer.render(scene, clipPlane);
		terrainRendererLowPoly.render(terrainLowPoly, camera, lightDirectional, clipPlane);
		reflectionFbo.unbindAfterRender();
		camera.getPosition().y += distance;
		camera.invertPitch();
		camera.invertRoll();

		// render refraction texture
		prepare();
		clipPlane = new Vector4f(0, -1, 0, Water.HEIGHT);
		refractionFbo.bindForRender(1);
		skyboxRenderer.render(scene, clipPlane);
		terrainRendererLowPoly.render(terrainLowPoly, camera, lightDirectional, clipPlane);
		refractionFbo.unbindAfterRender();

		// render to screen
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

		prepare();
		clipPlane = new Vector4f(0, 0, 0, 0);
		skyboxRenderer.render(scene, clipPlane);
		sunRenderer.render(scene);
		terrainRendererLowPoly.render(terrainLowPoly, camera, lightDirectional, clipPlane);
		waterRendererLowPoly.render(waterLowPoly, camera, lightDirectional,
			reflectionFbo.getColorBuffer(0), refractionFbo.getColorBuffer(0), refractionFbo.getDepthBuffer());
		if (player instanceof AnimatedModel) {
			animatedModelRenderer.render((AnimatedModel) player, camera, lightDirection, clipPlane);
		}

		scene.getFlareManager().render(scene);

		// after the 3D stuff and before the 2D stuff
		ParticleMaster.renderParticles(camera);

		guiRenderer.render(scene);
		TextMaster.render();

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
		return Fbo.newFbo(width, height).addColourAttachment(0, colourAttach).addDepthAttachment(depthAttach).init();
	}

	public void renderScene(IScene scene, Vector4f clipPlane) {
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
	public void cleanUp(IScene scene) {
		scene.cleanUp();
		terrainRendererLowPoly.cleanUp();
		waterRendererLowPoly.cleanUp();
		refractionFbo.delete();
		reflectionFbo.delete();
		animatedModelRenderer.cleanUp();
		skyboxRenderer.cleanUp();
		guiRenderer.cleanUp();
	}
}
