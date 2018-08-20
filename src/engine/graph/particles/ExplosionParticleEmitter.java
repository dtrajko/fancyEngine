package engine.graph.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector3f;
import engine.items.GameItem;

public class ExplosionParticleEmitter implements IParticleEmitter {

    private int maxParticles;
    private boolean active;
    private final List<GameItem> particles;
    private final Particle baseParticle;
    private long creationPeriodMillis;
    private float speedRndRange;
    private float positionRndRange;
    private float scaleRndRange;
    private long animRange;
    private int numParticlesCreated;
    private float gravity;
    private float transparencyCoef;

    public ExplosionParticleEmitter(Particle baseParticle, int maxParticles) {
        particles = new ArrayList<>();
        this.baseParticle = baseParticle;
        this.maxParticles = maxParticles;
        this.active = false;
        this.numParticlesCreated = 0;
        this.gravity = -1.0f;
        this.transparencyCoef = 0.001f;
    }

    @Override
    public Particle getBaseParticle() {
        return baseParticle;
    }

    public long getCreationPeriodMillis() {
        return creationPeriodMillis;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    @Override
    public List<GameItem> getParticles() {
        return particles;
    }

    public float getPositionRndRange() {
        return positionRndRange;
    }

    public float getScaleRndRange() {
        return scaleRndRange;
    }

    public float getSpeedRndRange() {
        return speedRndRange;
    }

    public void setAnimRange(long animRange) {
        this.animRange = animRange;
    }

    public void setTransparencyCoef(float tc) {
        this.transparencyCoef = tc;
    }
    
    public void setGravity(float g) {
        this.gravity = g;
    }

    public void setCreationPeriodMillis(long creationPeriodMillis) {
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    public void setPositionRndRange(float positionRndRange) {
        this.positionRndRange = positionRndRange;
    }

    public void setScaleRndRange(float scaleRndRange) {
        this.scaleRndRange = scaleRndRange;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSpeedRndRange(float speedRndRange) {
        this.speedRndRange = speedRndRange;
    }

    public void update(long elapsedTime) {
    	
        Iterator<? extends GameItem> it = particles.iterator();
        while (it.hasNext()) {
            Particle particle = (Particle) it.next();
            if (particle.updateTtl(elapsedTime) < 0) {
                it.remove();
            } else {
                updatePosition(particle, elapsedTime);
                updateTransparency(particle, elapsedTime);
            }
        }

        while (numParticlesCreated < maxParticles) {
        	createParticle();
        	numParticlesCreated++;
        }
    }

	private void createParticle() {
        Particle particle = new Particle(this.getBaseParticle());
        // Add a little bit of randomness of the particle
        float signX = Math.random() > 0.5d ? -1.0f : 1.0f;
        float signY = Math.random() > 0.5d ? -1.0f : 1.0f;
        float signZ = Math.random() > 0.5d ? -1.0f : 1.0f;
        float sign = Math.random() > 0.5d ? -1.0f : 1.0f;

        float speedIncX = signX * (float) Math.random() * this.speedRndRange;
        float speedIncY = signY * (float) Math.random() * this.speedRndRange;
        float speedIncZ = signZ * (float) Math.random() * this.speedRndRange;

        float posIncX = signX * (float) Math.random() * this.positionRndRange;
        float posIncY = signY * (float) Math.random() * this.positionRndRange;
        float posIncZ = signZ * (float) Math.random() * this.positionRndRange;
        float scaleInc = sign * (float) Math.random() * this.scaleRndRange;

        long updateAnimInc = (long) sign * (long)(Math.random() * (float) this.animRange);
        particle.getPosition().add(posIncX, posIncY, posIncZ);        
        particle.setSpeed(new Vector3f(speedIncX, speedIncY, speedIncZ));

        particle.setScale(particle.getScale() + scaleInc);
        particle.setUpdateTextureMills(particle.getUpdateTextureMillis() + updateAnimInc);
        particles.add(particle);
    }

    /**
     * Updates a particle position
     * @param particle The particle to update
     * @param elapsedTime Elapsed time in milliseconds
     */
    public void updatePosition(Particle particle, long elapsedTime) {
        Vector3f speed = particle.getSpeed();        
        float delta = (float) (Math.pow(elapsedTime, 2) / 1000.0f) * 0.1f;

        // make sure that gravity always works downwards
        if (speed.y * gravity > 0) {
        	gravity = -gravity;
        }
        float dx = speed.x * delta;
        float dy = speed.y * delta * gravity;
        float dz = speed.z * delta;
        Vector3f pos = particle.getPosition();
        particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
    }

    private void updateTransparency(Particle particle, long elapsedTime) {
    	float delta = (float) (Math.pow(elapsedTime, 2) / 1000.0f) * transparencyCoef;
    	float transparency = particle.getMesh().getMaterial().getTransparency();
    	transparency -= delta;
    	if (transparency < 0) transparency = 0;
		particle.getMesh().getMaterial().setTransparency(transparency);
	}

    @Override
    public void cleanup() {
        for (GameItem particle : getParticles()) {
            particle.cleanup();
        }
    }
}
