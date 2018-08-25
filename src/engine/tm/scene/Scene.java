package engine.tm.scene;

import org.joml.Vector3f;
import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.Input;
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.models.CubeMeshSimple;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.render.Loader;
import engine.tm.render.OBJLoader;
import engine.tm.textures.ModelTexture;

public class Scene implements IScene {

	private Loader loader;
	private ICamera camera;

	TexturedModel texturedModel;
	private Entity entity;
	private Entity entityOBJ;

	public void init(Window window) {
		camera = new Camera();
		loader = new Loader();

		ModelTexture texture = new ModelTexture(loader.loadTexture("frame"));
		RawModel model = loader.loadToVAO(CubeMeshSimple.vertices, CubeMeshSimple.textureCoords, CubeMeshSimple.indices);
		texturedModel = new TexturedModel(model, texture);
		entity = new Entity(texturedModel, new Vector3f(0, 0, -4), 0, 0, 0, 1);

		RawModel modelOBJ = OBJLoader.loadOBJModel("pine", loader);
		TexturedModel texturedModelOBJ = new TexturedModel(modelOBJ, new ModelTexture(loader.loadTexture("pine")));
		entityOBJ = new Entity(texturedModelOBJ, new Vector3f(0, -5f, -50), 0, 0, 0, 1);
		
	}

	public TexturedModel getTexturedModel() {
		return texturedModel;
	}

	public Entity getEntity() {
		return entityOBJ;
	}

	@Override
	public void update(float interval, Input input) {
		entity.increasePosition(0, 0, 0);
		entity.increaseRotation(1, 1, 0);
		
		entityOBJ.increaseRotation(0, 1, 0);
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
