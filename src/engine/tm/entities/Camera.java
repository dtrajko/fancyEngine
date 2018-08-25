package engine.tm.entities;

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
	private float gravity;
	private float y_min;

	public Camera() {
		speed = 2.0f;
		gravity = 0; // -speed / 2;
		y_min = 0;
	}

	public void move(Input input) {

		// gravity
		position.y += gravity;
		if (position.y <= y_min) position.y = y_min;
		
		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			position.y += +speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			position.y += -speed;
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

	public void setPosition(Vector3f pos) {
		position = pos;
		y_min = -10;
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
