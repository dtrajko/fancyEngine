package engine.tm.particles;

import org.joml.Vector3f;
import engine.GameEngine;
import engine.interfaces.ICamera;

public class ParticleSystemShoot {

	private float pps;
	private float speed;
	private float gravityComplient;
	private float lifeLength;
	private Vector3f direction;
	private Vector3f systemCenter;
	private ParticleTexture texture;
	private FireMaster fireMaster;

	public ParticleSystemShoot(ParticleTexture texture, float pps, float speed,
			float gravityComplient, float lifeLength, FireMaster fireMaster) {
		this.texture = texture;
		this.pps = pps;
		this.speed = speed;
		this.gravityComplient = gravityComplient;
		this.lifeLength = lifeLength;
		this.fireMaster = fireMaster;
	}

	public void generateParticles(ICamera camera, Vector3f systemCenter, Vector3f direction) {
		this.systemCenter = systemCenter;
		this.direction = new Vector3f(direction.x, direction.y * gravityComplient, direction.z);
		this.direction.normalize(speed);
		float delta = 1.0f / GameEngine.TARGET_UPS;
		float particlesToCreate = pps * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1;
		float scale = 5;
		for(int i = 0; i < count; i++) {
			this.systemCenter.add(this.direction);
			this.systemCenter.y += -camera.getPitch() / 3.1;
			scale *= 0.9f;
			emitParticle(this.systemCenter, scale);
		}
		if(Math.random() < partialParticle) {
			scale *= 0.9f;
			this.systemCenter.add(this.direction);
			emitParticle(this.systemCenter, scale);
		}
	}

	private void emitParticle(Vector3f center, float scale) {
		new Particle(fireMaster).setActive(this.texture, new Vector3f(center), center, gravityComplient, lifeLength, 10, scale, true);
	}
}
