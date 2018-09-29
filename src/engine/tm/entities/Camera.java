package engine.tm.entities;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.Input;
import engine.tm.render.IMasterRenderer;
import engine.tm.settings.WorldSettings;
import engine.tm.terrains.ITerrain;
import engine.tm.toolbox.Maths;

public class Camera implements ICamera {

	private float offsetY = 32; // point to player's head, not feet
	private Vector3f position = new Vector3f(0, 0, 0);
	private float pitch = 10;
	private float yaw = 0;
	private float roll = 0;
	private float distanceFromPlayer = 66;
	private float angleAroundPlayer = 0;
	private float speed;
	private float gravity;
	private float y_min;	
	private Vector3f cameraInc;
	private Vector2f displVec;
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();

	public Camera() {
		speed = 2.0f;
		gravity = 0; // -speed / 2;
		y_min = 0;
		cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
		projectionMatrix = createProjectionMatrix();
	}

	public Camera setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public Camera setDistanceFromPlayer(float distance) {
		this.distanceFromPlayer = distance;
		return this;
	}

	private static Matrix4f createProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) Window.width / (float) Window.height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(IMasterRenderer.FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = IMasterRenderer.FAR_PLANE - IMasterRenderer.NEAR_PLANE;

		projectionMatrix.m00(x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((IMasterRenderer.FAR_PLANE + IMasterRenderer.NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * IMasterRenderer.NEAR_PLANE * IMasterRenderer.FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);

		return projectionMatrix;
	}

	@Override
	public Matrix4f updateViewMatrix() {
		viewMatrix = Maths.createViewMatrix(this);
		return viewMatrix;
	}

	public void reflect(float height){
		invertPitch();
		this.position.y = position.y - 2 * (position.y - height);
		updateViewMatrix();
	}

	@Override
	public Matrix4f getProjectionViewMatrix() {
		Matrix4f projectionViewMatrix = new Matrix4f();		
		projectionMatrix.mul(viewMatrix, projectionViewMatrix);
		return projectionViewMatrix;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void moveWithPlayer(IScene scene, Input input) {
		IPlayer player = scene.getPlayer();
		displVec = input.getDisplVec();
		calculateZoom(input);
		calculatePitch(input);
		calculateRoll(input);
		calculateAngleAroundPlayer(input);
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance, scene);
		float theta = player.getRotY() + angleAroundPlayer;
		this.yaw = 180 - theta;
		updateViewMatrix();
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance, IScene scene) {
		IPlayer player = scene.getPlayer();
		float theta = player.getRotY() + angleAroundPlayer;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));		
		position.x = player.getPosition().x - offsetX;
		position.y = player.getPosition().y + verticalDistance + offsetY;
		position.z = player.getPosition().z - offsetZ;
		
		ITerrain terrain = scene.getCurrentTerrain(position.x, position.z);
		if (terrain instanceof ITerrain && terrain.getHeightOfTerrain(position.x, position.z) > position.y) {
			position.y = terrain.getHeightOfTerrain(position.x, position.z) + 5;
		}
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

	private void calculateRoll(Input input) {
		if (input.isKeyDown(GLFW.GLFW_KEY_1)) {
			// this.roll += speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_2)) {
			// this.roll += -speed;
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
		if (input.isKeyDown(GLFW.GLFW_KEY_A) || input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			cameraInc.x += -speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			cameraInc.x += +speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_W) || input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			cameraInc.z += -speed;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S) || input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			cameraInc.z += +speed;
		}

		Vector2f rotVec = input.getDisplVec();
		pitch += rotVec.x;
		yaw += rotVec.y;
		
		Vector3f newPos = calculateNewPosition(cameraInc.x, cameraInc.y, cameraInc.z);
		position.x = newPos.x;
		position.y = newPos.y;
		position.z = newPos.z;

		updateViewMatrix();
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

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getPitch() {
		return pitch;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getYaw() {
		return yaw;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public float getRoll() {
		return roll;
	}

	public void invertPitch() {
		this.pitch = -pitch;
	}

	public void invertRoll() {
		this.roll = -roll;
	}

	@Override
	public Matrix4f getViewMatrix() {
		return Maths.createViewMatrix(this);
	}

	@Override
	public Vector3f getRotation() {
		return new Vector3f(pitch, yaw, roll);
	}
}
