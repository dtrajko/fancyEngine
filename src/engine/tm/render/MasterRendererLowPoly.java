package engine.tm.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import engine.Window;
import engine.interfaces.ICamera;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IPlayer;
import engine.interfaces.IScene;
import engine.tm.animation.animatedModel.AnimatedModel;
import engine.tm.animation.renderer.AnimatedModelRenderer;
import engine.tm.entities.Camera;
import engine.tm.entities.EntityRenderer;
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
import engine.tm.particles.ParticleMaster;
import engine.tm.scene.Scene;
import engine.tm.scene.SceneLowPoly;
import engine.tm.settings.WorldSettings;
import engine.tm.skybox.SkyboxRenderer;
import engine.tm.sunRenderer.SunRenderer;
import engine.tm.utils.OpenGlUtils;

public class MasterRendererLowPoly implements IMasterRenderer {

	private static final float REFLECT_OFFSET = 0.2f;
	private static final float REFRACT_OFFSET = 0.2f;

	private static Matrix4f projectionMatrix;

	private static TerrainRendererLowPoly terrainRendererLowPoly;
	private static WaterRendererLowPoly waterRendererLowPoly;
	public static Fbo reflectionFbo;
	public static Fbo refractionFbo;

	private static EntityRenderer entityRenderer;
	private static AnimatedModelRenderer animatedModelRenderer;
	private static SkyboxRenderer skyboxRenderer;
	private static SunRenderer sunRenderer;
	private static GuiRenderer guiRenderer;

	public MasterRendererLowPoly() {
		createProjectionMatrix();
		terrainRendererLowPoly = new TerrainRendererLowPoly(projectionMatrix, true);
		waterRendererLowPoly = new WaterRendererLowPoly();
		refractionFbo = createWaterFbo(Window.width / 2, Window.height / 2, true);
		reflectionFbo = createWaterFbo(Window.width, Window.height, false);
		entityRenderer = new EntityRenderer(projectionMatrix);
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
		entityRenderer.init(scene);
	}

	public static WaterRendererLowPoly getWaterRenderer() {
		return waterRendererLowPoly;
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#render(engine.Window, engine.IScene)
	 */
	@Override
	public void render(Window window, IScene scene) {

		ICamera camera = scene.getCamera();

		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		renderWaterReflectionPass(scene);
		renderWaterRefractionPass(scene);
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		renderMainPass(scene);

		// after the 3D stuff and before the 2D stuff
		ParticleMaster.renderParticles(camera);

		guiRenderer.render(scene);
		TextMaster.render();
	}

	private void renderWaterReflectionPass(IScene scene) {		
		Camera camera = (Camera) scene.getCamera();
		IPlayer player = scene.getPlayer();
		Vector3f lightDirection = scene.getLightDirection();
		TerrainLowPoly terrainLowPoly = ((SceneLowPoly) scene).getTerrainLowPoly();
		LightDirectional lightDirectional = ((SceneLowPoly) scene).getLightDirectional();
		reflectionFbo.bindForRender(0);

		float distance = 2 * (camera.getPosition().y - WorldSettings.WATER_HEIGHT + REFLECT_OFFSET);
		camera.getPosition().y -= distance;
		camera.invertPitch();
		camera.invertRoll();

		Vector4f clipPlane = new Vector4f(0, 1, 0, -WorldSettings.WATER_HEIGHT + REFLECT_OFFSET);
		prepare();
		terrainRendererLowPoly.render(terrainLowPoly, camera, lightDirectional, clipPlane);
		entityRenderer.render(scene, clipPlane);
		animatedModelRenderer.render((AnimatedModel) player, camera, lightDirection, clipPlane);

		camera.getPosition().y += distance;
		camera.invertPitch();
		camera.invertRoll();

		reflectionFbo.unbindAfterRender();
	}

	private void renderWaterRefractionPass(IScene scene) {
		Vector4f clipPlane = new Vector4f(0, -1, 0, -WorldSettings.WATER_HEIGHT + REFRACT_OFFSET);
		Camera camera = (Camera) scene.getCamera();
		TerrainLowPoly terrainLowPoly = ((SceneLowPoly) scene).getTerrainLowPoly();
		LightDirectional lightDirectional = ((SceneLowPoly) scene).getLightDirectional();		
		refractionFbo.bindForRender(0);
		prepare();
		terrainRendererLowPoly.render(terrainLowPoly, camera, lightDirectional, clipPlane);
		scene.getFlareManager().render(scene);
		refractionFbo.unbindAfterRender();
	}

	private void renderMainPass(IScene scene) {
		Vector4f clipPlane = new Vector4f(0, 0, 0, 1);
		Camera camera = (Camera) scene.getCamera();
		IPlayer player = scene.getPlayer();
		Vector3f lightDirection = scene.getLightDirection();
		LightDirectional lightDirectional = ((SceneLowPoly) scene).getLightDirectional();
		TerrainLowPoly terrainLowPoly = ((SceneLowPoly) scene).getTerrainLowPoly();
		WaterTileLowPoly waterLowPoly = ((SceneLowPoly) scene).getWaterLowPoly();
		prepare();
		skyboxRenderer.render(scene, clipPlane);
		sunRenderer.render(scene);
		terrainRendererLowPoly.render(terrainLowPoly, camera, lightDirectional, clipPlane);
		waterRendererLowPoly.render(waterLowPoly, camera, lightDirectional,
			reflectionFbo.getColorBuffer(0), refractionFbo.getColorBuffer(0), refractionFbo.getDepthBuffer());
		entityRenderer.render(scene, clipPlane);
		animatedModelRenderer.render((AnimatedModel) player, camera, lightDirection, clipPlane);
		scene.getFlareManager().render(scene);
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
		Attachment colorAttach = new TextureAttachment(GL11.GL_RGBA8);
		Attachment depthAttach;
		if (useTextureForDepth) {
			depthAttach = new TextureAttachment(GL14.GL_DEPTH_COMPONENT24);
		} else {
			depthAttach = new RenderBufferAttachment(GL14.GL_DEPTH_COMPONENT24);
		}
		return Fbo.newFbo(width, height).addColorAttachment(0, colorAttach).addDepthAttachment(depthAttach).init();
	}

	/* (non-Javadoc)
	 * @see engine.tm.render.IMasterRenderer#prepare()
	 */
	@Override
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
		GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableDepthTesting(true);
		OpenGlUtils.antialias(true);
	}

	public static Matrix4f createProjectionMatrix() {
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
		entityRenderer.cleanUp();
		animatedModelRenderer.cleanUp();
		skyboxRenderer.cleanUp();
		guiRenderer.cleanUp();
	}
}
