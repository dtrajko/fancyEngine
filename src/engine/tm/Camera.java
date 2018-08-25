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
	
	private float speed;

	public Camera() {
		speed = 0.5f;
	}

	public void move(Input input) {
		
		// gravity
		position.y += -speed / 2;
		if (position.y < 0) position.y = 0;
		
		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			position.y += +speed;
		}
		
		if (input.isKeyDown(GLFW.GLFW_KEY_A)) {
			position.x += -speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D)) {
			position.x += +speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_W)) {
			position.z += -speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S)) {
			position.z += +speed;
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
