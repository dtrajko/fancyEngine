package engine.tm.render;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import engine.IScene;
import engine.Window;
import engine.tm.scene.Scene;

public class MasterRenderer {

	private static final float FOV = 70; // field of view angle
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	private Matrix4f projectionMatrix;

	private EntityRenderer entityRenderer;
	private TerrainRenderer terrainRenderer;

	public MasterRenderer() {
		createProjectionMatrix();
		entityRenderer = new EntityRenderer(projectionMatrix);
		terrainRenderer = new TerrainRenderer(projectionMatrix);
	}

	public void init(Window window) {
	}

	public void render(Window window, IScene scene) {
		prepare();
		entityRenderer.render(scene);
		terrainRenderer.render(scene);

	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 0.5f);
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

	public void cleanUp(IScene scene) {
		((Scene) scene).clearLists();
		entityRenderer.cleanUp();
		terrainRenderer.cleanUp();
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
}
