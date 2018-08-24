package engine.tm.scene;

import org.joml.Vector3f;
import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.tm.Camera;
import engine.tm.entities.Entity;
import engine.tm.models.CubeMeshSimple;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.render.Loader;
import engine.tm.textures.ModelTexture;

public class Scene implements IScene {

	private Loader loader;
	private RawModel model;
	private ModelTexture texture;
	private TexturedModel texturedModel;
	private Entity entity;
	private ICamera camera;

	public void init(Window window) {
		loader = new Loader();
		model = loader.loadToVAO(CubeMeshSimple.vertices, CubeMeshSimple.textureCoords, CubeMeshSimple.indices);
		texture = new ModelTexture(loader.loadTexture("frame"));
		texturedModel = new TexturedModel(model, texture);
		entity = new Entity(texturedModel, new Vector3f(0, 0, -2), 0, 0, 0, 1);
		camera = new Camera();
	}

	public TexturedModel getTexturedModel() {
		return texturedModel;
	}

	public Entity getEntity() {
		return entity;
	}

	@Override
	public void update(float interval) {
		entity.increasePosition(0, 0, 0);
		entity.increaseRotation(1, 1, 0);
	}

	@Override
	public void resetScene(Window window, ICamera camera, IGameLogic game) {
		// TODO Auto-generated method stub
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
	}

	@Override
	public void cleanup() {
		loader.cleanUp();
	}

	@Override
	public ICamera getCamera() {
		return camera;
	}
}
