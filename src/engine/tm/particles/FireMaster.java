package engine.tm.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector3f;
import engine.tm.loaders.Loader;
import game2D.io.Timer;

public class FireMaster {

	private static List<Fire> fires = new ArrayList<Fire>();
	private static List<Vector3f> locations = new ArrayList<Vector3f>();
	private Loader loader;
	private int MAX_FIRES = 3;
	private int MAX_LIFETIME = 3;

	public FireMaster(Loader loader) {
		this.loader = loader;
	}

	public void startFire(Vector3f position) {
		if (!locations.contains(position) && fires.size() < MAX_FIRES) {
			Fire fire = new Fire(position, loader, this);
			fires.add(fire);
			locations.add(position);
		}
	}

	public void update() {
		for (Iterator<Fire> iterator = fires.iterator(); iterator.hasNext(); ) {
			Fire fire = iterator.next();
			if (Timer.getTime() - fire.getStartTime() <= MAX_LIFETIME) {
				fire.update();
			} else {
				iterator.remove();
			}
		}
	}

	public void cleanUp() {
		fires.clear();
		locations.clear();
	}
}
