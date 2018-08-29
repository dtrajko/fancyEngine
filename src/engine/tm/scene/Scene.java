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
import engine.tm.terrains.Terrain;
import engine.tm.textures.ModelTexture;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;

public class Scene implements IScene {

	private Loader loader;
	private ICamera camera;
	private Player player;

	private List<Light> lights = new ArrayList<Light>();
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();

	public void init(Window window) {

		camera = new Camera();
		((Camera) camera).setPosition(new Vector3f(0, 20, 40));

		loader = new Loader();

		setupLights();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain_1/bg"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain_1/1"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain_1/2"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain_1/3"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		Terrain terrain_1 = new Terrain(0, 0,   loader, texturePack, blendMap, "heightmap");
		Terrain terrain_2 = new Terrain(-1, 0,  loader, texturePack, blendMap, "heightmap");
		Terrain terrain_3 = new Terrain(0, -1,  loader, texturePack, blendMap, "heightmap");
		Terrain terrain_4 = new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap");

		// player
		RawModel steveModelRaw = OBJLoader.loadOBJModel("steve", loader);
		TexturedModel steveModel = new TexturedModel(steveModelRaw, new ModelTexture(loader.loadTexture("steve")));
		player = new Player(steveModel, new Vector3f(0, 0, 0), 0, 180, 0, 4);

		processTerrain(terrain_1);
		processTerrain(terrain_2);
		processTerrain(terrain_3);
		processTerrain(terrain_4);

		generateForestModels();

		processEntity(player);
		
		setupGui();
	}

	private void setupLights() {
		Light light_sun = new Light(new Vector3f(0, 1000, 0), new Vector3f(1, 1, 1));
		lights.add(light_sun);

		/*
		Light light_2   = new Light(new Vector3f(200, 10, -200),  new Vector3f(10, 0, 0), new Vector3f(1.0f, 0.01f, 0.002f));
		Light light_3   = new Light(new Vector3f(-200, 10, -200), new Vector3f(0, 10, 0), new Vector3f(1.0f, 0.01f, 0.002f));
		Light light_4   = new Light(new Vector3f(0, 10, -400),    new Vector3f(0, 0, 10), new Vector3f(1.0f, 0.01f, 0.002f));
		lights.add(light_2);
		lights.add(light_3);
		lights.add(light_4);
		*/
	}

	private void setupGui() {
		GuiTexture button = new GuiTexture(loader.loadTexture("gui/button"), new Vector2f(-0.89f, -0.92f), new Vector2f(0.1f, 0.06f));
		processGui(button);
		// GuiTexture target = new GuiTexture(loader.loadTexture("gui/bullseye"), new Vector2f(0f, 0f), new Vector2f(0.026f, 0.04f));
		// processGui(target);		
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

		for (int i = 0; i < 500; i++) {
			entity = null;
			
			float coordX = rand.nextInt((int) Terrain.SIZE * 2) - Terrain.SIZE;
			float coordZ = rand.nextInt((int) Terrain.SIZE * 2) - Terrain.SIZE;
			float coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ);
			
			int clearance = 30;
			if (coordX < -Terrain.SIZE + clearance || coordX > Terrain.SIZE - clearance ||
				coordZ < -Terrain.SIZE + clearance || coordZ > Terrain.SIZE - clearance ||
				(coordX > 0 - clearance && coordX < 0 + clearance) ||
				(coordZ > 0 - clearance && coordZ < 0 + clearance)) {
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
		}
	}

	public Map<TexturedModel, List<Entity>> getEntityList() {
		return entities;
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

	public void clearLists() {
		terrains.clear();
		entities.clear();
		lights.clear();
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public ICamera getCamera() {
		return camera;
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

	private void processGui(GuiTexture button) {
		guis.add(button);
	}

	public Terrain getCurrentTerrain(float x, float z) {
		Terrain currentTerrain = null;
		// System.out.println("Scene getCurrentTerrain for X: " + x + " and Z: " + z);
		for (Terrain terrain : terrains) {
			if (x >= terrain.getX() && x < (terrain.getX() + terrain.SIZE) &&
				z >= terrain.getZ() && z < (terrain.getZ() + terrain.SIZE)) {
				currentTerrain = terrain;
				// System.out.println("Terrain boundaries X: " + terrain.getX() + ", Z: " + terrain.getZ() + " maxX: " + (terrain.getX() + terrain.SIZE) + " maxZ: " + (terrain.getZ() + terrain.SIZE));
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

	@Override
	public void cleanup() {
		loader.cleanUp();
	}
}
