package engine.tm.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.GameEngine;
import engine.interfaces.IScene;
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.Player;

public class Particle {

	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	private ParticleTexture texture;
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;
	private float elapsedTime = 0;
	private float distance;
	private Vector3f reusableChange = new Vector3f();
	private boolean alive = false;
	private boolean checkCollision = false;
	private FireMaster fireMaster;

	public Particle(FireMaster fireMaster) {
		this.fireMaster = fireMaster;
	}

	public void setActive(ParticleTexture texture, Vector3f position, Vector3f velocity, 
			float gravityEffect, float lifeLength, float rotation, float scale, boolean checkCollision) {
		this.alive = true;
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.texture = texture;
		this.checkCollision = checkCollision;
		ParticleMaster.addParticle(this);
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}

	protected boolean update(IScene scene) {
		Camera camera = (Camera) scene.getCamera();
		boolean stillAlive = true;
		float frameTimeSeconds = 1.0f / GameEngine.TARGET_UPS;
		velocity.y += Player.getGravity() * this.gravityEffect * frameTimeSeconds;
		reusableChange.set(velocity);
		reusableChange.mul(frameTimeSeconds, reusableChange);
		reusableChange.add(this.position);
		updateTextureCoordInfo();
		Vector3f camPos = new Vector3f(camera.getPosition());
		distance = camPos.sub(position).lengthSquared();
		this.elapsedTime += frameTimeSeconds;		
		stillAlive = this.elapsedTime < this.lifeLength;
		if (stillAlive && checkCollision) {
			Entity entity = Entity.getEntityInCollisionWith(scene, position.x, position.y, position.z, 5.0f);
			if (entity instanceof Entity) {
				Vector3f entPos = entity.getPosition();
				fireMaster.startFire(new Vector3f(entPos.x, entPos.y, entPos.z));
				checkCollision = false;
				scene.removeEntity(entity);
				stillAlive = false;
			}
		}
		return stillAlive;
	}

	public float getDistance() {
		return distance;
	}

	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public float getBlend() {
		return blend;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	private void updateTextureCoordInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
	}

	private void setTextureOffset(Vector2f offset, int index) {
		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}
}
