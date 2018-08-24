package engine.tm;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.graph.ICamera;
import engine.graph.Input;

public class Camera implements ICamera {
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch;
	private float yaw;
	private float roll;

	public Camera() {
		
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

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}

	@Override
	public Matrix4f getViewMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector3f getRotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix4f updateViewMatrix() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
