package engine.tm;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Window;
import engine.graph.Camera;

public class ThinMatrixCamera extends Camera {

	private static final float FOV = 60;
	private static final float NEAR_PLANE = 0.5f;
	private static final float FAR_PLANE = 1000;

	private float pitch = 0;
	private float yaw = 0;
	private float roll = 0;

	private Window window;
	private Matrix4f projectionMatrix;

	public ThinMatrixCamera(Window window) {
		super();
		this.window = window;
		this.projectionMatrix = createProjectionMatrix();
	}

	public Matrix4f updateViewMatrix() {
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(yaw),   new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(roll),  new Vector3f(0, 0, 1));		
		Vector3f negativeCameraPos = new Vector3f(-getPosition().x,-getPosition().y,-getPosition().z);
		viewMatrix.translate(negativeCameraPos);
		return viewMatrix;
	}

	private Matrix4f createProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix.identity();
		
		System.out.println("TMCamera createPM: " + projectionMatrix);
		/*
		float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);
		*/
		return projectionMatrix;
	}

	public Matrix4f getProjectionViewMatrix() {		
		return projectionMatrix.mul(viewMatrix);
	}

	public void move() {
		updateViewMatrix();
	}

}
