package engine.tm.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joml.Matrix4f;

import engine.IScene;
import engine.tm.entities.Camera;
import engine.tm.loaders.Loader;
import engine.tm.scene.Scene;

public class ParticleMaster {

	private static Map<ParticleTexture, List<Particle>> particles = new HashMap<ParticleTexture, List<Particle>>();
	private static ParticleRenderer renderer;

	public static void init(IScene scene, Matrix4f projectionMatrix) {
		Loader loader = ((Scene) scene).getLoader();
		renderer = new ParticleRenderer(loader, projectionMatrix);
	}

	public static void update(IScene scene) {
		Camera camera = (Camera) ((Scene) scene).getCamera();
		Iterator<Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Entry<ParticleTexture, List<Particle>> entry = mapIterator.next();
			List<Particle> list = entry.getValue();
			Iterator<Particle> iterator = list.iterator();
			while (iterator.hasNext()) {
				Particle p = iterator.next();
				boolean stillAlive = p.update(scene);
				if (!stillAlive) {
					iterator.remove();
					if (list.isEmpty()) {
						mapIterator.remove();
					}
				}
			}
			if (!entry.getKey().usesAdditiveBlending()) {
				InsertionSort.sortHighToLow(list);
			}
		}
	}

	public static void renderParticles(Camera camera) {
		renderer.render(particles, camera);
	}
	
	public static void addParticle(Particle particle) {
		List<Particle> list = particles.get(particle.getTexture());
		if (list == null) {
			list = new ArrayList<Particle>();
			particles.put(particle.getTexture(), list);
		}
		list.add(particle);
	}

	public static int getParticlesCount() {
		int totalCount = 0;
		for (ParticleTexture texture: particles.keySet()) {
			List<Particle> particleList = particles.get(texture);
			totalCount += particleList.size();
		}
		return totalCount;
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}
}
