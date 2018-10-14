package engine.tm.particles;

import org.joml.Vector3f;
import engine.tm.loaders.Loader;
import engine.tm.settings.WorldSettings;
import game2D.io.Timer;

public class Fire {

	private Vector3f position;
	private double startTime;

	private ParticleTexture particleTextureFire;
	private ParticleTexture particleTextureSmoke;

	private ParticleSystemComplex particleSystemFire;
	private ParticleSystemComplex particleSystemSmoke;

	public Fire(Vector3f position, Loader loader, FireMaster fireMaster) {
		this.position = position;
		this.startTime = Timer.getTime();
		particleTextureFire = new ParticleTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/particles/fire.png"), 8, true);
		particleTextureSmoke = new ParticleTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/particles/smoke.png"), 8, false);
		particleSystemFire = new ParticleSystemComplex(particleTextureFire, 30f, 20f, 0.0f, 6f, 20f, false, fireMaster);
		particleSystemSmoke = new ParticleSystemComplex(particleTextureSmoke, 30f, 20f, -10f, 10f, 20f, false, fireMaster);
	}

	public void update() {
		particleSystemFire.generateParticles(new Vector3f(position.x, position.y + 4, position.z));
		particleSystemSmoke.generateParticles(new Vector3f(position.x, position.y + 10, position.z));
	}

	public double getStartTime() {
		return startTime;
	}
}
