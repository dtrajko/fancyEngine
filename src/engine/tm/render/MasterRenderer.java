package engine.tm.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.Window;
import engine.tm.entities.Camera;
import engine.tm.entities.EntityRenderer;
import engine.tm.entities.Player;
import engine.tm.gui.GuiRenderer;
import engine.tm.normalMapping.NormalMappingRenderer;
import engine.tm.scene.Scene;
import engine.tm.skybox.SkyboxRenderer;
import engine.tm.terrains.TerrainRenderer;
import engine.tm.water.Water;
import engine.tm.water.WaterRenderer;

public class MasterRenderer {

	private static final float FOV = 70; // field of view angle
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 2000;

	public static final float RED   = 0.832f;
	public static final float GREEN = 0.961f;
	public static final float BLUE  = 0.996f;

	private static Matrix4f projectionMatrix;

	private static TerrainRenderer terrainRenderer;
	private static EntityRenderer entityRenderer;
	private static NormalMappingRenderer normalMapRenderer;
	private static SkyboxRenderer skyboxRenderer;
	private static WaterRenderer waterRenderer;
	private static GuiRenderer guiRenderer;

	public MasterRenderer() {
		createProjectionMatrix();
		terrainRenderer = new TerrainRenderer(projectionMatrix);
		entityRenderer = new EntityRenderer(projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(projectionMatrix);
		waterRenderer = new WaterRenderer(projectionMatrix);
		guiRenderer = new GuiRenderer();
	}

	public void init(Window window) {
	}

	public static WaterRenderer getWaterRenderer() {
		return waterRenderer;
	}

	public void render(Window window, IScene scene) {

		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

		Camera camera = (Camera) ((Scene) scene).getCamera();

		// render reflection texture
		waterRenderer.getFBOs().bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y - Water.HEIGHT);
		camera.getPosition().y -= distance;
		camera.invertPitch();
		renderScene(scene, new Vector4f(0, 1, 0, -Water.HEIGHT));
		camera.getPosition().y += distance;
		camera.invertPitch();

		// render refraction texture
		waterRenderer.getFBOs().bindRefractionFrameBuffer();
		renderScene(scene, new Vector4f(0, -1, 0, Water.HEIGHT));

		renderMinimap(scene);

		// render to screen
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		waterRenderer.getFBOs().unbindCurrentFrameBuffer();
		renderScene(scene, new Vector4f(0, 0, 0, 0));
		waterRenderer.render(scene);
		guiRenderer.render(scene);

		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}

	public void renderMinimap(IScene scene) {

		Camera camera = (Camera) ((Scene) scene).getCamera();
		Player player = ((Scene) scene).getPlayer();

		// render minimap
		waterRenderer.getFBOs().bindMinimapFrameBuffer();
		Vector3f cameraPosition = camera.getPosition();
		float currentPitch = camera.getPitch();
		float currentYaw = camera.getYaw();
		float currentRoll = camera.getRoll();
		camera.setPosition(new Vector3f(player.getPosition().x, 500, player.getPosition().z));
		camera.setPitch(90);
		camera.setYaw(currentYaw - 180);
		prepare();
		terrainRenderer.render(scene, new Vector4f(0, 1, 0, -Water.HEIGHT));
		entityRenderer.render(scene);
		normalMapRenderer.render(scene, new Vector4f(0, 0, 0, 0));
		camera.setPosition(cameraPosition);
		camera.setPitch(currentPitch);
		camera.setYaw(currentYaw);
	}

	public void renderScene(IScene scene, Vector4f clipPlane) {
		prepare();
		terrainRenderer.render(scene, clipPlane);
		entityRenderer.render(scene, clipPlane);
		normalMapRenderer.render(scene, clipPlane);
		skyboxRenderer.render(scene, clipPlane);
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
	}

	private Matrix4f createProjectionMatrix() {
		float aspectRatio = (float) Window.width / (float) Window.height;		
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * FAR_PLANE * NEAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);

		return projectionMatrix;
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void cleanUp(IScene scene) {
		scene.cleanUp();
		terrainRenderer.cleanUp();
		entityRenderer.cleanUp();
		normalMapRenderer.cleanUp();
		skyboxRenderer.cleanUp();
		waterRenderer.cleanUp();
		guiRenderer.cleanUp();
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
}
