package engine.tm;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Window;
import engine.graph.Camera;

public class ThinMatrixCamera extends Camera {

	private static final float FOV = 60;
	public static final float NEAR_PLANE = 0.5f;
	public static final float FAR_PLANE = 1000;

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
		// First do the rotation so camera rotates over its position
		viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
		// Then do the translation
		viewMatrix.translate(-position.x,-position.y,-position.z);
		return viewMatrix;
	}

	private Matrix4f createProjectionMatrix() {
		float aspectRatio = (float) window.getWidth() / window.getHeight();
		projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
		return projectionMatrix;
	}

	public Matrix4f getProjectionViewMatrix() {
		Matrix4f pvm = projectionMatrix.mul(viewMatrix);
		return pvm;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void move() {
		updateViewMatrix();
	}

	public void reflect(float height){
		invertPitch();
		this.position.y = position.y - 2 * (position.y - height);
		updateViewMatrix();
	}

	public void invertPitch(){
		this.pitch = -pitch;
	}

}
