package engine.tm.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;
import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.Input;
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.entities.Player;
import engine.tm.loaders.Loader;
import engine.tm.loaders.OBJLoader;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.terrains.Terrain;
import engine.tm.textures.ModelTexture;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;

public class Scene implements IScene {

	private Loader loader;
	private ICamera camera;
	private Player player;
	private Light light;

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();

	public void init(Window window) {
		
		camera = new Camera();
		((Camera) camera).setPosition(new Vector3f(0, 20, 40));

		loader = new Loader();
		light = new Light(new Vector3f(-500, 500, 500), new Vector3f(1, 1, 1));

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain terrain_1 = new Terrain(0, 0,   loader, texturePack, blendMap, "heightmap");
		Terrain terrain_2 = new Terrain(-1, 0,  loader, texturePack, blendMap, "heightmap");
		Terrain terrain_3 = new Terrain(0, -1,  loader, texturePack, blendMap, "heightmap");
		Terrain terrain_4 = new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap");

		ModelTexture texture = new ModelTexture(loader.loadTexture("frame"));
		texture.setShineDamper(20).setReflectivity(1);
		// RawModel model = loader.loadToVAO(CubeMeshSimple.vertices, CubeMeshSimple.textureCoords, CubeMeshSimple.indices);
		RawModel model = OBJLoader.loadOBJModel("cube", loader);
		TexturedModel texturedModel = new TexturedModel(model, texture);
		Entity entity_1 = new Entity(texturedModel, new Vector3f(0, 8, -30f), 0, 0, 0, 4);
		Entity entity_2 = new Entity(texturedModel, new Vector3f(12, 8, -30f), 0, 0, 0, 4);
		Entity entity_3 = new Entity(texturedModel, new Vector3f(-12, 8, -30f), 0, 0, 0, 4);

		RawModel modelOBJ = OBJLoader.loadOBJModel("dragon", loader);
		TexturedModel texturedModelOBJ = new TexturedModel(modelOBJ, new ModelTexture(loader.loadTexture("gold")));
		ModelTexture modelTexture = texturedModelOBJ.getTexture();
		modelTexture.setShineDamper(10);
		modelTexture.setReflectivity(1);
		Entity entityOBJ = new Entity(texturedModelOBJ, new Vector3f(0, 13, -30f), 0, 0, 0, 1);

		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		Entity grass = new Entity(grassModel, new Vector3f(0, 0, -100f), 0, 0, 0, 4);
		grass.getTexturedModel().getTexture().setTransparent(true).setUseFakeLighting(true);

		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		Entity fern = new Entity(fernModel, new Vector3f(0, 0, -100f), 0, 0, 0, 4);
		fern.getTexturedModel().getTexture().setTransparent(true).setUseFakeLighting(true);

		// player
		RawModel steveModelRaw = OBJLoader.loadOBJModel("steve", loader);
		TexturedModel steveModel = new TexturedModel(steveModelRaw, new ModelTexture(loader.loadTexture("steve")));
		player = new Player(steveModel, new Vector3f(0, 0, 0), 0, 180, 0, 4);

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
		processEntity(player);
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

	public Player getPlayer() {
		return player;
	}

	@Override
	public ICamera getCamera() {
		return camera;
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
}
