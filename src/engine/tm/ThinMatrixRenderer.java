package engine.tm;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
// import animatedModelRenderer.AnimatedModelRenderer;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.graph.ShaderProgram;
import engine.graph.Transformation;
import engine.tm.skybox.SkyboxRenderer;
import engine.tm.sun.SunRenderer;
import engine.tm.terrains.TerrainRenderer;
// import entityRenderers.EntityRenderer;
// import fbos.Fbo;
// import water.WaterFrameBuffers;
// import water.WaterRenderer;
// import water.WaterRendererAux;
import engine.utils.Log;

public class ThinMatrixRenderer implements IRenderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private final Transformation transformation;
	private static final Vector4f NO_CLIP = new Vector4f(0, 0, 0, 1);
	private static final float REFLECT_OFFSET = 0.1f;
	private static final float REFRACT_OFFSET = 1f;

	private SkyboxRenderer skyRenderer;
	private SunRenderer sunRenderer;
	private TerrainRenderer terrainRenderer;
	// private AnimatedModelRenderer animModelRenderer;
	// private EntityRenderer entityRenderer;
	// private WaterRenderer waterRenderer;
	// private WaterFrameBuffers waterFbos;
	// private WaterRendererAux waterRendererAux;
	// private final Fbo reflectionFbo;
	// private final Fbo refractionFbo;

	public ThinMatrixRenderer() {
		transformation = new Transformation();
		// this.waterFbos = new WaterFrameBuffers();
		// this.refractionFbo = createWaterFbo(Display.getWidth() / 2, Display.getHeight() / 2, true);
		// this.reflectionFbo = createWaterFbo(Display.getWidth(), Display.getHeight(), false);
		// this.waterRenderer = new WaterRenderer(waterFbos);
		// this.waterRendererAux = new WaterRendererAux();
		// this.entityRenderer = new EntityRenderer();
		// this.animModelRenderer = new AnimatedModelRenderer();
	}

	@Override
	public void init(Window window, IScene scene) {
		this.skyRenderer = new SkyboxRenderer();
		this.sunRenderer = new SunRenderer();
		this.terrainRenderer = new TerrainRenderer(true);
	}

	@Override
	public void render(Window window, ICamera camera, IScene scene, boolean sceneChanged) {
		clear();
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

		renderScene(scene);

        // Update projection matrix once per render cycle
        window.updateProjectionMatrix();
        

        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);
	}

	protected void renderScene(IScene scene) {
		renderMainPass(scene);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		renderWaterRefractionPass(scene);
		renderWaterReflectionPass(scene);
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		renderMainPass(scene);
	}

	private void renderMainPass(IScene scene) {
		/*
		prepare();
		skyRenderer.render(scene.getSkyBox(), scene.getCamera());
		sunRenderer.render(scene.getSun(), scene.getCamera());
		if (scene.getLensFlare() != null) {
			scene.getLensFlare().render(scene.getCamera(), scene.getSun().getWorldPosition(scene.getCamera().getPosition()));			
		}
		*/
		// entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), NO_CLIP);
		// terrainRenderer.render(scene.getTerrain(), scene.getCamera(), scene.getLight(), new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		// waterRenderer.render(scene.getWater(), scene.getCamera(), scene.getLightDirection());
		// waterRendererAux.render(scene.getWaterAux(), scene.getCamera(), scene.getLight(), reflectionFbo.getColourBuffer(0), refractionFbo.getColourBuffer(0), refractionFbo.getDepthBuffer());
		// animModelRenderer.render(scene.getAnimatedPlayer(), scene.getCamera(), scene.getLightDirection());

		// ParticleMaster.update(scene.getCamera());
		// ParticleMaster.renderParticles(scene.getCamera());
	}

	/**
	 * Prepare to render the current frame by clearing the framebuffer.
	 */
	private void prepare() {
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private void renderWaterRefractionPass(IScene scene) {
		// waterFbos.bindRefractionFrameBuffer();
		// refractionFbo.bindForRender(1);
		prepare();
		// scene.getTerrain().render(scene.getCamera(), scene.getLight(), new Vector4f(0, 1, 0, -scene.getWaterHeight() + REFRACT_OFFSET));
		// entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), new Vector4f(0,-1,0, 0));
		// waterFbos.unbindCurrentFrameBuffer();
		// refractionFbo.unbindAfterRender();
	}

	private void renderWaterReflectionPass(IScene scene) {
		// waterFbos.bindReflectionFrameBuffer();
		// reflectionFbo.bindForRender(1);
		prepare();
		// scene.getCamera().reflect(scene.getWaterHeight());
		// scene.getTerrain().render(scene.getCamera(), scene.getLight(), new Vector4f(0, 1, 0, -scene.getWaterHeight() + REFLECT_OFFSET));
		// entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), new Vector4f(0,1,0,0.1f));
		// skyRenderer.render(scene.getSkyBox(), scene.getCamera());
		// waterFbos.unbindCurrentFrameBuffer();
		// reflectionFbo.unbindAfterRender();
		// scene.getCamera().reflect(scene.getWaterHeight());
	}

	@Override
	public void clear() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}

	@Override
	public void cleanup() {
		// this.animModelRenderer.cleanUp();
		// this.entityRenderer.cleanUp();
		this.sunRenderer.cleanUp();
		this.skyRenderer.cleanUp();
		// this.waterRendererAux.cleanUp();
		// this.waterRenderer.cleanUp();
		this.terrainRenderer.cleanUp();
		// this.waterFbos.cleanUp();
		// this.refractionFbo.delete();
		// this.reflectionFbo.delete();
	}

}
