package engine.helloWorld;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import engine.Window;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IScene;

public class MasterRendererHelloWorld implements IMasterRenderer {

	private static Matrix4f projectionMatrix;

	@Override
	public void init(IScene scene) {
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		if (projectionMatrix == null) {
			createProjectionMatrix();
		}
		return projectionMatrix;
	}

	@Override
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
	}

	@Override
	public void render(Window window, IScene scene) {
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

	@Override
	public void cleanUp() {
	}
}
