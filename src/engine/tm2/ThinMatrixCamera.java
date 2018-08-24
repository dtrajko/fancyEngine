package engine.tm2;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.Window;
import engine.graph.ICamera;

public class ThinMatrixCamera implements ICamera {

	private static final float FOV = 60;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;

	private final Vector3f position;
	private final Vector3f rotation;

	private float pitch = 0;
	private float yaw = 0;
	private float roll = 0;

	private Window window;
	private Matrix4f viewMatrix;
	private Matrix4f projectionMatrix;

	public ThinMatrixCamera(Window window) {		
		super();
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		this.window = window;
		this.viewMatrix = new Matrix4f();
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

	public Matrix4f createProjectionMatrix() {
		float aspectRatio = Window.width / Window.height;
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

	public void move() {
		// System.out.println("TM Camera move position X " + position.x + " Y: " + position.y + " Z: " + position.z);
		if (position.x != 0.0f) {
			int x = 20 / 0;
		}		
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

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	public void setPosition(Vector3f pos) {
		// this.position = pos;
	}

	public void setPosition(float x, float y, float z) {
		// position.x = x;
		// position.y = y;
		// position.z = z;
	}

	public void setRotation(Vector3f rot) {
		// this.rotation = rot;
	}

	public Vector3f getPosition() {
		return this.position;
	}

	public Vector3f getRotation() {
		return this.rotation;
	}
}
