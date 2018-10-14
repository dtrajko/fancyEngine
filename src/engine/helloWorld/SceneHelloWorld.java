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
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.gui.GuiTexture;
import engine.tm.loaders.Loader;
import engine.tm.models.CubeMeshSimple;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.settings.WorldSettings;
import engine.tm.textures.ModelTexture;

public class SceneHelloWorld implements IScene {

	private Loader loader;
	private ICamera camera;
	private IMasterRenderer masterRenderer;
	private Entity entity;

	public SceneHelloWorld() {
		masterRenderer = new MasterRendererHelloWorld();
	}

	@Override
	public void init() {
		camera = new Camera();
		loader = new Loader();
		masterRenderer.init(this);

		ModelTexture texture = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/tiles.png"));
		RawModel model = loader.loadToVAO(CubeMeshSimple.vertices, CubeMeshSimple.textureCoords, CubeMeshSimple.indices);
		TexturedModel texturedModel = new TexturedModel(model, texture);
		entity = new Entity(texturedModel, new Vector3f(0, -0.5f, -5), 0, 0, 0, 1);
	}
	
	public Entity getEntity() {
		return entity;
	}

	@Override
	public void update(float interval, Input input) {
		entity.increasePosition(0, 0, 0);
		entity.increaseRotation(1, 1, 0);
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

	@Override
	public List<Light> getLights() {
		return null;
	}
}
