package engine.tm.entities;

import org.joml.Matrix4f;
import org.joml.Vector2f;
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
	
	private Vector3f cameraInc;

	public Camera() {
		speed = 2.0f;
		gravity = 0; // -speed / 2;
		y_min = 0;
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
	}

	public void move(Input input) {

		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);

		// gravity
		cameraInc.y += gravity;
		if (cameraInc.y <= y_min) position.y = y_min;
		
		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			cameraInc.y += +speed / 4;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			cameraInc.y += -speed / 4;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_A)) {
			cameraInc.x += -speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D)) {
			cameraInc.x += +speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_W)) {
			cameraInc.z += -speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S)) {
			cameraInc.z += +speed;
		}

		Vector2f rotVec = input.getDisplVec();
		pitch += rotVec.x;
		yaw += rotVec.y;
		
		Vector3f newPos = calculateNewPosition(cameraInc.x, cameraInc.y, cameraInc.z);
		position.x = newPos.x;
		position.y = newPos.y;
		position.z = newPos.z;		
	}

	public void moveOld(Input input) {

		// gravity
		position.y += gravity;
		if (position.y <= y_min) position.y = y_min;
		
		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			position.y += +speed / 4;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			position.y += -speed / 4;
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

		Vector2f rotVec = input.getDisplVec();
		pitch += rotVec.x;
		yaw += rotVec.y;
	}

	public Vector3f calculateNewPosition(float offsetX, float offsetY, float offsetZ) {
		Vector3f newPos = new Vector3f(position.x, position.y, position.z);
		if ( offsetZ != 0 ) {
			newPos.x += (float) Math.sin(Math.toRadians(yaw)) * -1.0f * offsetZ;
			newPos.z += (float) Math.cos(Math.toRadians(yaw)) * offsetZ;
		}
		if ( offsetX != 0) {
			newPos.x += (float) Math.sin(Math.toRadians(yaw - 90)) * -1.0f * offsetX;
			newPos.z += (float) Math.cos(Math.toRadians(yaw - 90)) * offsetX;
		}
		newPos.y += offsetY;		
		return newPos;
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
