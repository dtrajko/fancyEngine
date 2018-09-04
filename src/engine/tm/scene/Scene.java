package engine.tm.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.joml.Vector2f;
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
import engine.tm.gui.GuiTexture;
import engine.tm.loaders.Loader;
import engine.tm.loaders.OBJLoader;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.normalMapping.NormalMappedObjLoader;
import engine.tm.render.MasterRenderer;
import engine.tm.skybox.Skybox;
import engine.tm.terrains.Terrain;
import engine.tm.textures.ModelTexture;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;
import engine.tm.water.Water;
import engine.tm.water.WaterTile;

public class Scene implements IScene {

	private Loader loader;
	private ICamera camera;
	private Player player;
	private Skybox skybox;
	private Water water;

	private List<Light> lights = new ArrayList<Light>();
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();

	public void init(Window window) {
		camera = new Camera();
		((Camera) camera).setPosition(new Vector3f(0, 20, 40));
		loader = new Loader();
		skybox = new Skybox(loader);
		setupTerrains();
		generateForestModels();
		generateNormalMapEntities();
		setupPlayer();
		setupWater();
		setupLights();
		setupGui();
	}

	private void setupWater() {
		water = new Water(loader);
		WaterTile waterTile = new WaterTile(0, Water.HEIGHT, 0);
		WaterTile waterTile_2 = new WaterTile(0, Water.HEIGHT, -WaterTile.TILE_SIZE * 2);
		processWaterTile(waterTile);
		processWaterTile(waterTile_2);
	}

	private void setupPlayer() {
		// player
		RawModel steveModelRaw = OBJLoader.loadOBJModel("steve", loader);
		TexturedModel steveModel = new TexturedModel(steveModelRaw, new ModelTexture(loader.loadTexture("steve")));
		player = new Player(steveModel, new Vector3f(0, 0, 0), 0, 180, 0, 4);
		processEntity(player);
	}

	private void setupTerrains() {
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain_1/bg"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain_1/1"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain_1/2"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain_1/3"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain_1/blendMap"));
		Terrain terrain_1 = new Terrain(-0.5f, -0.5f, loader, texturePack, blendMap, "terrain_1/heightmap");
		processTerrain(terrain_1);
		TerrainTexture blendMap_2 = new TerrainTexture(loader.loadTexture("terrain_2/blendMap"));
		Terrain terrain_2 = new Terrain(-0.5f, -1.5f, loader, texturePack, blendMap_2, "terrain_2/heightmap");
		processTerrain(terrain_2);
	}

	private void setupLights() {
		Light light_sun = new Light(new Vector3f(-500, 2000, -500), new Vector3f(1, 1, 1));
		lights.add(light_sun);
	}

	private void setupGui() {
		GuiTexture refraction = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getRefractionTexture(), new Vector2f(0.86f, 0.56f), new Vector2f(0.12f, 0.12f));
		GuiTexture reflection = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getReflectionTexture(), new Vector2f(0.86f, 0.84f), new Vector2f(0.12f, 0.12f));
		GuiTexture minimap    = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getMinimapTexture(),    new Vector2f(-0.84f, 0.72f), new Vector2f(0.14f, 0.24f));
		GuiTexture mmTarget   = new GuiTexture(loader.loadTexture("gui/bullseye"), new Vector2f(-0.84f, 0.72f), new Vector2f(0.02f, 0.036f));
		processGui(refraction);
		processGui(reflection);
		processGui(minimap);
		processGui(mmTarget);
	}

	private void generateForestModels() {
		Random rand = new Random();
		Entity entity = null;
		ModelTexture grassTexture = new ModelTexture(loader.loadTexture("grassTexture"));
		grassTexture.setTransparent(true).setUseFakeLighting(true);
		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), grassTexture);		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern_atlas"));
		fernTextureAtlas.setNumberOfRows(2);
		fernTextureAtlas.setTransparent(true).setUseFakeLighting(true);
		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);
		TexturedModel pineModel = new TexturedModel(OBJLoader.loadOBJModel("pine", loader), new ModelTexture(loader.loadTexture("pine")));

		int modelsSpawned = 0;
		while (modelsSpawned < 200) {
			entity = null;

			float coordX = rand.nextInt((int) Terrain.SIZE) - Terrain.SIZE * 1/2;
			float coordZ = rand.nextInt((int) Terrain.SIZE * 2) - Terrain.SIZE * 3/2;
			float coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ);
			
			float clearance = 50;
			if (coordX < -Terrain.SIZE * 1/2 + clearance || coordX > Terrain.SIZE * 1/2 - clearance ||
				coordZ < -Terrain.SIZE * 3/2 + clearance || coordZ > Terrain.SIZE * 1/2 - clearance ||
				(coordZ < -175 && coordZ > -625) || 
				coordY < Water.HEIGHT + 5.0f) {
				continue;
			}

			int modelIndex = rand.nextInt(3);
			int modelSize = rand.nextInt(3) + 2;
			int fernTxIndex = rand.nextInt(4);

			switch (modelIndex) {
			case 0:
				entity = new Entity(grassModel, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, modelSize);
				break;
			case 1:				
				entity = new Entity(fernModel, fernTxIndex, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, modelSize);
				break;
			case 2:
				entity = new Entity(pineModel, 0, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, modelSize);
				break;
			}
			if (entity != null) {
				processEntity(entity);
			}
			modelsSpawned++;
		}
	}
	
	private void generateNormalMapEntities() {
		// normal map entities
		ModelTexture crateTexture = new ModelTexture(loader.loadTexture("normalMaps/crate"));
		crateTexture.setNormalMap(loader.loadTexture("normalMaps/crateNormal"));
		crateTexture.setShineDamper(10);
		crateTexture.setReflectivity(0.5f);
		TexturedModel crateTexturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader), crateTexture);
		float coordX = -100;
		float coordZ = -280;
		float coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ) + 4;
		Entity crateModel = new Entity(crateTexturedModel, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, 0.05f);		
		processNormalMapEntity(crateModel);

		ModelTexture barrelTexture = new ModelTexture(loader.loadTexture("normalMaps/barrel"));
		barrelTexture.setNormalMap(loader.loadTexture("normalMaps/barrelNormal"));
		barrelTexture.setShineDamper(10);
		barrelTexture.setReflectivity(0.5f);
		TexturedModel barrelTexturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), barrelTexture);
		coordX = -200;
		coordZ = -305;
		coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ) + 10;
		Entity barrelModel = new Entity(barrelTexturedModel, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, 2f);		
		processNormalMapEntity(barrelModel);

		ModelTexture boulderTexture = new ModelTexture(loader.loadTexture("normalMaps/boulder"));
		boulderTexture.setNormalMap(loader.loadTexture("normalMaps/boulderNormal"));
		boulderTexture.setShineDamper(10);
		boulderTexture.setReflectivity(0.5f);
		TexturedModel boulderTexturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), boulderTexture);
		coordX = 260;
		coordZ = -330;
		coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ);
		Entity boulderModel = new Entity(boulderTexturedModel, new Vector3f(coordX, coordY, coordZ), 0, 90, 45, 2f);
		Entity boulderModel2 = new Entity(boulderTexturedModel, new Vector3f(coordX + 20, coordY, coordZ), -90, 0, 0, 2f);
		processNormalMapEntity(boulderModel);
		processNormalMapEntity(boulderModel2);
	}

	public Map<TexturedModel, List<Entity>> getEntityList() {
		return entities;
	}

	public Map<TexturedModel, List<Entity>> getNormalMapEntityList() {
		return normalMapEntities;
	}

	public List<Terrain> getTerrains() {
		return terrains;
	}

	public List<GuiTexture> getGuiElements() {
		return guis;
	}

	public List<Light> getLights() {
		return lights;
	}

	public List<WaterTile> getWaterTiles() {
		return waterTiles;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public ICamera getCamera() {
		return camera;
	}

	public Loader getLoader() {
		return loader;
	}

	public Skybox getSkybox() {
		return skybox;
	}

	public Water getWater() {
		return water;
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

	public void processNormalMapEntity(Entity entity) {
		TexturedModel entityModel = entity.getTexturedModel();
		List<Entity> batch = normalMapEntities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMapEntities.put(entityModel, newBatch);
		}
	}

	public void processWaterTile(WaterTile waterTile) {
		waterTiles.add(waterTile);
	}

	public void processGui(GuiTexture gui) {
		guis.add(gui);
	}

	public Terrain getCurrentTerrain() {
		Terrain currentTerrain = null;
		try {
			currentTerrain = getCurrentTerrain(player.getPosition().x, player.getPosition().z);
		} catch (Exception e) {
			System.out.println("Failed to retrieve the current terrain object.");
			e.printStackTrace();
		}
		return currentTerrain;
	}

	public Terrain getCurrentTerrain(float x, float z) {
		Terrain currentTerrain = null;
		for (Terrain terrain : terrains) {
			if (x >= terrain.getX() && x < (terrain.getX() + Terrain.SIZE) &&
				z >= terrain.getZ() && z < (terrain.getZ() + Terrain.SIZE)) {
				currentTerrain = terrain;
			}
		}
		return currentTerrain;
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

	public void clearLists() {
		terrains.clear();
		entities.clear();
		normalMapEntities.clear();
		lights.clear();
		waterTiles.clear();
		guis.clear();
	}

	@Override
	public void cleanUp() {		
		clearLists();
		loader.cleanUp();
	}
}
