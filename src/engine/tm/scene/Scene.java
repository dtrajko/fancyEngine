package engine.tm.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.Input;
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.models.CubeMeshSimple;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.render.Loader;
import engine.tm.render.OBJLoader;
import engine.tm.terrains.Terrain;
import engine.tm.textures.ModelTexture;

public class Scene implements IScene {

	private Loader loader;
	private ICamera camera;
	private Light light;

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();

	public void init(Window window) {
		camera = new Camera();
		((Camera) camera).setPosition(new Vector3f(0, 10, 40));
		loader = new Loader();

		light = new Light(new Vector3f(-500, 500, 500), new Vector3f(1, 1, 1));

		Terrain terrain_1 = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("grass")).setShineDamper(20).setReflectivity(1));
		Terrain terrain_2 = new Terrain(-1, 0, loader, new ModelTexture(loader.loadTexture("mud")).setShineDamper(20).setReflectivity(1));
		Terrain terrain_3 = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("tiles")).setShineDamper(20).setReflectivity(1));
		Terrain terrain_4 = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("tiles_gold")).setShineDamper(20).setReflectivity(1));

		ModelTexture texture = new ModelTexture(loader.loadTexture("frame"));
		texture.setShineDamper(20).setReflectivity(1);
		// RawModel model = loader.loadToVAO(CubeMeshSimple.vertices, CubeMeshSimple.textureCoords, CubeMeshSimple.indices);
		RawModel model = OBJLoader.loadOBJModel("cube", loader);
		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity entity_1 = new Entity(texturedModel, new Vector3f(0, 5, 0f), 0, 0, 0, 4);
		Entity entity_2 = new Entity(texturedModel, new Vector3f(12, 5, 0f), 0, 0, 0, 4);
		Entity entity_3 = new Entity(texturedModel, new Vector3f(-12, 5, 0f), 0, 0, 0, 4);

		RawModel modelOBJ = OBJLoader.loadOBJModel("dragon", loader);
		TexturedModel texturedModelOBJ = new TexturedModel(modelOBJ, new ModelTexture(loader.loadTexture("gold")));
		ModelTexture modelTexture = texturedModelOBJ.getTexture();
		modelTexture.setShineDamper(10);
		modelTexture.setReflectivity(1);
		Entity entityOBJ = new Entity(texturedModelOBJ, new Vector3f(0, 10, 0f), 0, 0, 0, 1);

		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		Entity grass = new Entity(grassModel, new Vector3f(0, 0, -100f), 0, 0, 0, 4);
		grass.getTexturedModel().getTexture().setTransparent(true).setUseFakeLighting(true);

		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		Entity fern = new Entity(fernModel, new Vector3f(0, 0, -100f), 0, 0, 0, 4);
		fern.getTexturedModel().getTexture().setTransparent(true).setUseFakeLighting(true);

		processTerrain(terrain_1);
		processTerrain(terrain_2);
		processTerrain(terrain_3);
		processTerrain(terrain_4);

		processEntity(entity_1);
		processEntity(entity_2);
		processEntity(entity_3);
		processEntity(entityOBJ);
		processEntity(grass);
		processEntity(fern);
	}

	public Map<TexturedModel, List<Entity>> getEntityList() {
		return entities;
	}

	public List<Terrain> getTerrains() {
		return terrains;
	}
	
	public void clearLists() {
		terrains.clear();
		entities.clear();
	}

	public Light getLight() {
		return light;
	}

	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getTexturedModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	@Override
	public void update(float interval, Input input) {
		
		for(TexturedModel model: entities.keySet()) {
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch) {
				// entity.increaseRotation(0, 1, 0);
			}
		}
		
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
