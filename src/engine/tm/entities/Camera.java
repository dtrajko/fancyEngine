package engine.tm.entities;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.graph.ICamera;
import engine.graph.Input;

public class Camera implements ICamera {
	
	private final float OFFSET_Y = 10; // point to player's head, not feet
	
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 10;
	private float yaw = 0;
	private float roll;
	
	private float distanceFromPlayer = 100;
	private float angleAroundPlayer = 0;

	private float speed;
	private float gravity;
	private float y_min;	
	private Vector3f cameraInc;
	private Vector2f displVec;

	public Camera() {
		speed = 2.0f;
		gravity = 0; // -speed / 2;
		y_min = 0;
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
	}

	public void moveWithPlayer(Player player, Input input) {
		displVec = input.getDisplVec();
		calculateZoom(input);
		calculatePitch(input);
		calculateAngleAroundPlayer(input);
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(player, horizontalDistance, verticalDistance);
		float theta = player.getRotY() + angleAroundPlayer;
		this.yaw = 180 - theta;
	}

	private void calculateCameraPosition(Player player, float horizontalDistance, float verticalDistance) {
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));		
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y + verticalDistance + OFFSET_Y;
		position.z = player.getPosition().z - offsetZ;
	}

	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom(Input input) {
		float zoomLevel = input.getMouseWheelDelta() * 10f;
		distanceFromPlayer += zoomLevel;
	}

	/*
	 * #define GLFW_MOUSE_BUTTON_LEFT    GLFW_MOUSE_BUTTON_1
     * #define GLFW_MOUSE_BUTTON_MIDDLE  GLFW_MOUSE_BUTTON_3
     * #define GLFW_MOUSE_BUTTON_RIGHT   GLFW_MOUSE_BUTTON_2
	 */
	private void calculatePitch(Input input) {
		if (input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_2)) { // right button pressed
			float pitchChange = displVec.y * 0.5f;
			pitch -= pitchChange;
		}
	}

	private void calculateAngleAroundPlayer(Input input) {
		if (input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_1)) { // left button pressed
			float angleChange = displVec.x * 1f;
			angleAroundPlayer -= angleChange;
		}
	}

	/**
	 * Stand-alone camera movement, not depending on player position
	 * 
	 * @param input
	 */
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
