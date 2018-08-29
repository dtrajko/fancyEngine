package engine.tm.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.IScene;
import engine.graph.Input;
import engine.tm.models.TexturedModel;
import engine.tm.scene.Scene;
import engine.tm.terrains.Terrain;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -2.0f;
	private static final float JUMP_POWER = 2.0f;
	private static final float TERRAIN_HEIGHT = 0;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	
	private boolean isInAir = false;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(float interval, Input input, IScene scene) {
		checkInputs(input);
		super.increaseRotation(0, currentTurnSpeed * interval, 0);
		float distance = currentSpeed * interval;
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		super.increasePosition(dx, 0, dz);
		upwardsSpeed += GRAVITY * interval;
		super.increasePosition(dx, upwardsSpeed, dz);

		Terrain currentTerrain = ((Scene) scene).getCurrentTerrain(super.getPosition().x, super.getPosition().z);
		float terrainHeight = TERRAIN_HEIGHT;
		if (currentTerrain != null) {
			terrainHeight = currentTerrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
		}

		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			isInAir = false;
			super.getPosition().y = terrainHeight;
		}		
	}

	private void jump() {
		if (true || !isInAir) {
			upwardsSpeed = JUMP_POWER;
			isInAir = true;
		}
	}

	private void checkInputs(Input input) {

		if (input.isKeyDown(GLFW.GLFW_KEY_W)) {
			this.currentSpeed = RUN_SPEED;
		} else if (input.isKeyDown(GLFW.GLFW_KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}

		if (input.isKeyDown(GLFW.GLFW_KEY_D)) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else if (input.isKeyDown(GLFW.GLFW_KEY_A)) {
			this.currentTurnSpeed = TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}
		
		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			jump();
		}
	}
}