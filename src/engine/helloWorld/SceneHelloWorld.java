package engine.helloWorld;

import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import engine.Window;
import engine.graph.Input;
import engine.interfaces.ICamera;
import engine.interfaces.IGameLogic;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IPlayer;
import engine.interfaces.IScene;
import engine.interfaces.ISkybox;
import engine.interfaces.ISun;
import engine.interfaces.ITerrain;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.gui.GuiTexture;
import engine.tm.lensFlare.FlareManager;
import engine.tm.loaders.Loader;
import engine.tm.models.TexturedModel;
import engine.tm.particles.FireMaster;

public class SceneHelloWorld implements IScene {

	private IMasterRenderer masterRenderer;

	public SceneHelloWorld() {
		masterRenderer = new MasterRendererHelloWorld();
	}

	@Override
	public void init() {
		masterRenderer.init(this);
	}

	@Override
	public void update(float interval, Input input) {
	}

	@Override
	public void resetScene(Window window, ICamera camera, IGameLogic game) {
	}

	@Override
	public void save() {
	}

	@Override
	public IMasterRenderer getMasterRenderer() {
		return masterRenderer;
	}

	@Override
	public void cleanUp() {
		masterRenderer.cleanUp();
	}

	@Override
	public ICamera getCamera() {
		return null;
	}

	@Override
	public Map<TexturedModel, List<Entity>> getEntityList() {
		return null;
	}

	@Override
	public Loader getLoader() {
		return null;
	}

	@Override
	public ITerrain getCurrentTerrain(float x, float z) {
		return null;
	}

	@Override
	public IPlayer getPlayer() {
		return null;
	}

	@Override
	public Vector3f getLightDirection() {
		return null;
	}

	@Override
	public ISkybox getSkybox() {
		return null;
	}

	@Override
	public ISun getSun() {
		return null;
	}

	@Override
	public FlareManager getFlareManager() {
		return null;
	}

	@Override
	public List<GuiTexture> getGuiElements() {
		return null;
	}

	@Override
	public FireMaster getFireMaster() {
		return null;
	}

	@Override
	public void removeEntity(Entity entity) {
	}

	@Override
	public List<Light> getLights() {
		return null;
	}

	@Override
	public float getWaterLevelOffset() {
		return 0;
	}
}
