package engine.tm.particles;

import org.joml.Vector3f;

import engine.GameEngine;

public class ParticleSystemSimple {
	
	private float pps;
	private float speed;
	private float gravityComplient;
	private float lifeLength;
	private float scale;
	private boolean checkCollision;
	private ParticleTexture texture;
	private FireMaster fireMaster;
	
	public ParticleSystemSimple(ParticleTexture texture, float pps, float speed, float gravityComplient,
			float lifeLength, float scale, boolean checkCollision, FireMaster fireMaster) {
		this.texture = texture;
		this.pps = pps;
		this.speed = speed;
		this.gravityComplient = gravityComplient;
		this.lifeLength = lifeLength;
		this.scale = scale;
		this.checkCollision = checkCollision;
	}
	
	public void generateParticles(Vector3f systemCenter){
		float delta = 1.0f / GameEngine.getFPS();
		float particlesToCreate = pps * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1;
		for(int i = 0; i < count; i++){
			emitParticle(systemCenter);
		}
		if(Math.random() < partialParticle){
			emitParticle(systemCenter);
		}
	}
	
	private void emitParticle(Vector3f center){
		float dirX = (float) Math.random() * 2f - 1f;
		float dirZ = (float) Math.random() * 2f - 1f;
		Vector3f velocity = new Vector3f(dirX, 1, dirZ);
		velocity.normalize();
		velocity.normalize(speed);
		new Particle(fireMaster).setActive(this.texture, new Vector3f(center), velocity, gravityComplient, lifeLength, 0, scale, checkCollision);
	}
}
