package engine.tm.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.IScene;
import engine.graph.Input;
import engine.tm.models.TexturedModel;
import engine.tm.scene.Scene;
import engine.tm.terrains.Terrain;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 80;
	private static final float TURN_SPEED = 160;
	private static final float GRAVITY = -2.0f; // -2.0f;
	private static final float JUMP_POWER = 2.0f;
	private static final float TERRAIN_HEIGHT = 0;

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;

	private boolean isInAir = false;
	private boolean gravityEnabled = true;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move(float interval, Input input, IScene scene) {
		checkInputs(input);
		float distance = currentSpeed * interval;
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));

		float turnCoef = 1.0f;
		if (gravityEnabled) {
			upwardsSpeed += GRAVITY * interval;
		} else {
			turnCoef = 0.1f;
		}

		super.increasePosition(dx, upwardsSpeed, dz);
		super.increaseRotation(0, currentTurnSpeed * interval * turnCoef, 0);
		

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

		if (input.isKeyDown(GLFW.GLFW_KEY_Q)) {
			upwardsSpeed += GRAVITY / 5;
		}

		if (input.isKeyDown(GLFW.GLFW_KEY_P)) {
			gravityEnabled = !gravityEnabled;
		}

		float speedCoef = 1.0f;
		if (input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			speedCoef = 2.0f;
		}

		if (input.isKeyDown(GLFW.GLFW_KEY_W) || input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			this.currentSpeed = RUN_SPEED * speedCoef;
		} else if (input.isKeyDown(GLFW.GLFW_KEY_S) || input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			this.currentSpeed = -RUN_SPEED * speedCoef;
		} else {
			this.currentSpeed = 0;
		}

		if (input.isKeyDown(GLFW.GLFW_KEY_D) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			this.currentTurnSpeed = -TURN_SPEED;
		} else if (input.isKeyDown(GLFW.GLFW_KEY_A) || input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			this.currentTurnSpeed = TURN_SPEED;
		} else {
			this.currentTurnSpeed = 0;
		}
		
		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			jump();
		}
	}
}
