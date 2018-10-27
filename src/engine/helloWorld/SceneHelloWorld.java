package engine.helloWorld;

import java.util.ArrayList;
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
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.gui.GuiTexture;
import engine.tm.loaders.Loader;
import engine.tm.loaders.OBJLoader;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.settings.WorldSettings;
import engine.tm.textures.ModelTexture;

public class SceneHelloWorld implements IScene {

	private Loader loader;
	private ICamera camera;
	private IMasterRenderer masterRenderer;
	private Entity entity;
	private List<Light> lights = new ArrayList<Light>();

	public SceneHelloWorld() {
		masterRenderer = new MasterRendererHelloWorld();
	}

	@Override
	public void init() {

		camera = new Camera();
		((Camera) camera).setSpeed(0.1f);
		((Camera) camera).setPitch(0);

		loader = new Loader();
		masterRenderer.init(this);

		lights.add(new Light(new Vector3f(-100, 100, -100), new Vector3f(1, 1, 1)));

		ModelTexture modelTexture = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/dragon.png"));
		modelTexture.setShineDamper(10);
		modelTexture.setReflectivity(10);
		RawModel rawModel = OBJLoader.loadOBJModel("dragon", loader);
		TexturedModel texturedModel = new TexturedModel(rawModel, modelTexture);
		entity = new Entity(texturedModel, new Vector3f(0, -9.5f, -27), 0, 0, 0, 1);
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public void update(float interval, Input input) {
		entity.increasePosition(0, 0, 0);
		entity.increaseRotation(0, 1, 0);
		((Camera) camera).move(input);
	}

	@Override
	public void render(Window window) {
		masterRenderer.render(window, this);
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
		loader.cleanUp();
		masterRenderer.cleanUp();
	}

	@Override
	public Loader getLoader() {
		return loader;
	}

	@Override
	public ICamera getCamera() {
		return camera;
	}

	@Override
	public List<Light> getLights() {
		return lights;
	}

	@Override
	public Map<TexturedModel, List<Entity>> getEntityList() {
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
	public List<GuiTexture> getGuiElements() {
		return null;
	}

	@Override
	public void removeEntity(Entity entity) {
	}
}
