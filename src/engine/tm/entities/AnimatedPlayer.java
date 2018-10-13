package engine.tm.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.graph.Input;
import engine.interfaces.IPlayer;
import engine.interfaces.IScene;
import engine.interfaces.ITerrain;
import engine.tm.animation.animatedModel.AnimatedModel;
import engine.tm.animation.animatedModel.Joint;
import engine.tm.openglObjects.Vao;
import engine.tm.settings.WorldSettingsLowPoly;
import engine.tm.textures.Texture;

public class AnimatedPlayer extends AnimatedModel implements IPlayer {
	
	private static final float RUN_SPEED = 40;
	private static final float TURN_SPEED = 10;
	private static final float GRAVITY = -2.0f; // -2.0f;
	private static final float JUMP_POWER = 2.0f;
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed = 0;
	private boolean gravityEnabled = true;

	public AnimatedPlayer(Vao model, Texture texture, Joint rootJoint, int jointCount, 
			Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, texture, rootJoint, jointCount, position, rotX, rotY, rotZ, scale);		
	}

	public Vector3f getProjectedFuturePosition(float distance) {
		Vector3f currentPos = this.getPosition();
		Vector3f futurePosition = new Vector3f(currentPos.x, currentPos.y, currentPos.z);
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
		futurePosition.x += dx;
		futurePosition.z += dz;
		return futurePosition;
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

		Vector3f currentPos = this.getPosition();

		Entity entity = Entity.getEntityInCollisionWith(scene, currentPos.x, currentPos.y, currentPos.z, 0.0f);
		if (entity instanceof Entity && entity.isSolid()) {
			super.decreasePosition(dx, upwardsSpeed, dz);
		}

		ITerrain currentTerrain = scene.getCurrentTerrain((int) super.getPosition().x, (int) super.getPosition().z);
		float terrainHeight = WorldSettingsLowPoly.WATER_HEIGHT + scene.getWaterLevelOffset();
		if (currentTerrain != null) {
			terrainHeight = currentTerrain.getHeightOfTerrain((int) super.getPosition().x, (int) super.getPosition().z);
		}

		if (super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
		}
	}

	@Override
	public void increaseRotation(float dx, float dy, float dz) {
		super.increaseRotation(dx, dy, dz);
	}

	private void jump() {
		upwardsSpeed = JUMP_POWER;
	}

	private void checkInputs(Input input) {

		if (input.isKeyDown(GLFW.GLFW_KEY_Q)) {
			upwardsSpeed += GRAVITY / 5;
		}

		if (input.isKeyDown(GLFW.GLFW_KEY_P)) {
			gravityEnabled = !gravityEnabled;
		}

		float speedCoef = 4.0f;
		float turnSpeedCoef = 16.0f;
		if (input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			speedCoef = 1.0f;
			turnSpeedCoef = 1.0f;
		}

		if (input.isKeyDown(GLFW.GLFW_KEY_W) || input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			this.currentSpeed = RUN_SPEED * speedCoef;
		} else if (input.isKeyDown(GLFW.GLFW_KEY_S) || input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			this.currentSpeed = -RUN_SPEED * speedCoef;
		} else {
			this.currentSpeed = 0;
		}

		if (input.isKeyDown(GLFW.GLFW_KEY_D) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			this.currentTurnSpeed = -TURN_SPEED * turnSpeedCoef;
		} else if (input.isKeyDown(GLFW.GLFW_KEY_A) || input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			this.currentTurnSpeed = TURN_SPEED * turnSpeedCoef;
		} else {
			this.currentTurnSpeed = 0;
		}

		// Update player horizontal rotation based on mouse movement
		Vector2f rotVec = input.getDisplVec();
		this.currentTurnSpeed += -TURN_SPEED * rotVec.y;

		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			jump();
		}
	}

	public static float getGravity() {
		return GRAVITY;
	}
}
