package engine.tm2;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.Window;
import engine.graph.ICamera;
import engine.graph.Input;
import engine.tm.Camera;

public class ThinMatrixCamera implements ICamera {

	private static final float FOV = 60;
	public static final float NEAR_PLANE = 0.1f;
	public static final float FAR_PLANE = 1000;

	private Vector3f position;
	private Vector3f rotation;

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
	
	@Override
	public Matrix4f updateViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(this.rotation.x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(this.rotation.y), new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(this.rotation.z), new Vector3f(0, 0, 1));		
		Vector3f camPos = this.position;
		Vector3f negativeCameraPos = new Vector3f(-camPos.x, -camPos.y, -camPos.z);		
		viewMatrix.translate(negativeCameraPos);
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

	public Matrix4f createViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(this.rotation.x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(this.rotation.y), new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(this.rotation.z), new Vector3f(0, 0, 1));		
		Vector3f camPos = this.position;
		Vector3f negativeCameraPos = new Vector3f(-camPos.x, -camPos.y, -camPos.z);		
		viewMatrix.translate(negativeCameraPos);
		return viewMatrix;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void move(Input input) {
		if (input.isKeyDown(GLFW.GLFW_KEY_W)) {
			position.z += +0.02f;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S)) {
			position.z += -0.02f;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_A)) {
			position.x += +0.02f;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D)) {
			position.x += -0.02f;
		}
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
		this.position = pos;
	}

	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
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
