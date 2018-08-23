package engine.tm2;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
// import animatedModelRenderer.AnimatedModelRenderer;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.graph.Transformation;
// import entityRenderers.EntityRenderer;
// import fbos.Fbo;
// import water.WaterFrameBuffers;
// import water.WaterRenderer;
// import water.WaterRendererAux;
import engine.tm2.fbos.Attachment;
import engine.tm2.fbos.Fbo;
import engine.tm2.fbos.RenderBufferAttachment;
import engine.tm2.fbos.TextureAttachment;
import engine.tm2.skybox.SkyboxRenderer;
import engine.tm2.sun.SunRenderer;
import engine.tm2.terrains.TerrainRenderer;
import engine.tm2.water.WaterMeshRenderer;

public class ThinMatrixRenderer implements IRenderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private final Transformation transformation;
	private static final float REFLECT_OFFSET = 0.1f;
	private static final float REFRACT_OFFSET = 1f;

	private SkyboxRenderer skyRenderer;
	private SunRenderer sunRenderer;
	private TerrainRenderer terrainRenderer;
	// private AnimatedModelRenderer animModelRenderer;
	// private EntityRenderer entityRenderer;
	// private WaterRenderer waterRenderer;
	// private WaterFrameBuffers waterFbos;
	private WaterMeshRenderer waterMeshRenderer;
	private Fbo reflectionFbo;
	private Fbo refractionFbo;
	private ThinMatrixScene scene;

	public ThinMatrixRenderer() {
		transformation = new Transformation();
		// this.waterFbos = new WaterFrameBuffers();
		// this.waterRenderer = new WaterRenderer(waterFbos);
		// this.entityRenderer = new EntityRenderer();
		// this.animModelRenderer = new AnimatedModelRenderer();
	}

	@Override
	public void init(Window window, IScene scene) {
		this.scene = (ThinMatrixScene) scene;
		this.skyRenderer = new SkyboxRenderer();
		this.sunRenderer = new SunRenderer();
		this.terrainRenderer = new TerrainRenderer(true);
		this.waterMeshRenderer = new WaterMeshRenderer();
		this.refractionFbo = createWaterFbo(window.getWidth() / 2, window.getHeight() / 2, true);
		this.reflectionFbo = createWaterFbo(window.getWidth(), window.getHeight(), false);
	}

	/**
	 * Sets up an FBO for one of the extra render passes. The FBO is initialized
	 * with a texture colour attachment, and can be initialized with either a
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

	@Override
	public void render(Window window, ICamera camera, IScene scene, boolean sceneChanged) {
		clear();
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

		renderScene(window, (ThinMatrixScene) scene);

        // Update projection matrix once per render cycle
        window.updateProjectionMatrix();

        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);
	}

	protected void renderScene(Window window, ThinMatrixScene scene) {
		renderMainPass(window, scene);
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		renderWaterRefractionPass(window, scene);
		renderWaterReflectionPass(window, scene);
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		renderMainPass(window, scene);
	}

	private void renderMainPass(Window window, ThinMatrixScene scene) {
		prepare();
		skyRenderer.render(scene.getSkyBox(), scene.getCamera());
		sunRenderer.render(scene.getSun(), scene.getCamera());
		if (scene.getLensFlare() != null) {
			Vector3f sunWorldPos = new Vector3f(); // scene.getSun().getWorldPosition(scene.getCamera().getPosition());
			scene.getLensFlare().render(window, scene.getCamera(), sunWorldPos);
		}
		//// entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), NO_CLIP);
		terrainRenderer.render(scene.getTerrain(), scene.getCamera(), scene.getLight(), new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
		//// waterRenderer.render(scene.getWater(), scene.getCamera(), scene.getLightDirection());
		waterMeshRenderer.render(scene.getWaterMesh(), scene.getCamera(), scene.getLight(), reflectionFbo.getColourBuffer(0), refractionFbo.getColourBuffer(0), refractionFbo.getDepthBuffer());
		//// animModelRenderer.render(scene.getAnimatedPlayer(), scene.getCamera(), scene.getLightDirection());

		//// ParticleMaster.update(scene.getCamera());
		//// ParticleMaster.renderParticles(scene.getCamera());
	}

	/**
	 * Prepare to render the current frame by clearing the framebuffer.
	 */
	@Override
	public void prepare() {
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	private void renderWaterRefractionPass(Window window, ThinMatrixScene scene) {
		// waterFbos.bindRefractionFrameBuffer();
		refractionFbo.bindForRender(1);
		prepare();
		scene.getTerrain().render(scene.getCamera(), scene.getLight(), new Vector4f(0, 1, 0, -scene.getWaterHeight() + REFRACT_OFFSET));
		// entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), new Vector4f(0,-1,0, 0));
		// waterFbos.unbindCurrentFrameBuffer();
		refractionFbo.unbindAfterRender(window);
	}

	private void renderWaterReflectionPass(Window window, ThinMatrixScene scene) {
		// waterFbos.bindReflectionFrameBuffer();
		reflectionFbo.bindForRender(1);
		prepare();
		scene.getCamera().reflect(scene.getWaterHeight());
		scene.getTerrain().render(scene.getCamera(), scene.getLight(), new Vector4f(0, 1, 0, -scene.getWaterHeight() + REFLECT_OFFSET));
		// entityRenderer.render(scene.getAllEntities(), scene.getAdditionalEntities(), scene.getCamera(), scene.getSun(), new Vector4f(0,1,0,0.1f));
		skyRenderer.render(scene.getSkyBox(), scene.getCamera());
		// waterFbos.unbindCurrentFrameBuffer();
		reflectionFbo.unbindAfterRender(window);
		scene.getCamera().reflect(scene.getWaterHeight());
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
		this.waterMeshRenderer.cleanUp();
		// this.waterRenderer.cleanUp();
		this.terrainRenderer.cleanUp();
		// this.waterFbos.cleanUp();
		this.refractionFbo.delete();
		this.reflectionFbo.delete();
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
}
