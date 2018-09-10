package engine.tm.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import engine.GameEngine;
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
import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;
import engine.tm.gui.fonts.TextMaster;
import engine.tm.loaders.Loader;
import engine.tm.loaders.OBJLoader;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.normalMapping.NormalMappedObjLoader;
import engine.tm.particles.ParticleMaster;
import engine.tm.particles.ParticleSystemComplex;
import engine.tm.particles.ParticleSystemShoot;
import engine.tm.particles.ParticleTexture;
import engine.tm.render.MasterRenderer;
import engine.tm.settings.WorldSettings;
import engine.tm.skybox.Skybox;
import engine.tm.terrains.ITerrain;
import engine.tm.terrains.Terrain;
import engine.tm.terrains.TerrainProcedural;
import engine.tm.textures.ModelTexture;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;
import engine.tm.water.Water;
import engine.tm.water.WaterTile;
import engine.utils.Maths;

public class Scene implements IScene {

	private Loader loader;
	private ICamera camera;
	private Player player;
	private Skybox skybox;
	private Water water;

	private List<Light> lights = new ArrayList<Light>();
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<ITerrain> terrains = new ArrayList<ITerrain>();
	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();
	
	private FontType font_1;
	private FontType font_2;
	private GUIText[] text;
	
	private ParticleTexture particleTexture;
	private ParticleSystemComplex particleSystemComplex;
	private ParticleSystemComplex particleSystemFire;
	private ParticleSystemComplex particleSystemSmoke;
	private ParticleSystemShoot particleSystemShoot;

	private boolean fireMode = true;
	
	private MasterRenderer masterRenderer;

	public Scene() {
		camera = new Camera();
		loader = new Loader();
		skybox = new Skybox(loader);
		masterRenderer = new MasterRenderer();
		((Camera) camera).setPosition(new Vector3f(0, 20, 40));
	}

	public void init() {
		masterRenderer.init(this);
		setupTerrains(); // setupTerrainsProcedural();
		generateForestModels();
		generateNormalMapEntities();
		setupPlayer();
		setupWater();
		setupLights();
		setupParticles();
		setupGui();
		setupText();
	}

	public MasterRenderer getMasterRenderer() {
		return masterRenderer;
	}

	private void setupParticles() {
		fireMode = true;
		particleTexture = new ParticleTexture(loader.loadTexture("particles/particleAtlas"), 4, true);
		// particleSystemShoot = new ParticleSystemShoot(particleTexture, 20f, 20f, -2.0f, 5f); // magic circle around the player
		particleSystemShoot = new ParticleSystemShoot(particleTexture, 300f, 50f, -2.0f, 2f);
		// setupParticlesFire();
	}

	private void updateParticles(Input input) {

		if (input.isKeyReleased(GLFW.GLFW_KEY_F)) {
			fireMode = !fireMode;
			if (fireMode) {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 300f, 50f, -2.0f, 2f);
			} else {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 20f, 20f, -2.0f, 5f); // magic circle around the player
			}
		}

		if (input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_3)) {
			float coordX = player.getPosition().x;
			float coordY = player.getPosition().y + 10;
			float coordZ = player.getPosition().z;
			// particleSystemSimple.generateParticles(new Vector3f(coordX, coordY, coordZ));
			// particleSystemComplex.generateParticles(new Vector3f(coordX, coordY, coordZ));

			float playerDX = (float) (Math.sin(Math.toRadians(player.getRotY())));
			float playerDY = 0;
			float playerDZ = (float) (Math.cos(Math.toRadians(player.getRotY())));
			Vector3f playerDirection = new Vector3f(playerDX, playerDY, playerDZ);
			particleSystemShoot.generateParticles(new Vector3f(coordX, coordY, coordZ), playerDirection);
		}
		// updateParticlesFire();
	}

	private void setupParticlesFire() {
		ParticleTexture particleTextureFire = new ParticleTexture(loader.loadTexture("particles/fire"), 8, true);
		ParticleTexture particleTextureSmoke = new ParticleTexture(loader.loadTexture("particles/smoke"), 8, false);
		particleSystemFire = new ParticleSystemComplex(particleTextureFire, 100f, 1f, -1.0f, 2f, 20f);
		particleSystemFire.setLifeError(0.1f);
		particleSystemFire.setSpeedError(0.25f);
		particleSystemFire.setScaleError(0.5f);
		particleSystemFire.randomizeRotation();
		particleSystemSmoke = new ParticleSystemComplex(particleTextureSmoke, 100f, 100f, -100f, 20f, 20f);
		particleSystemSmoke.setLifeError(0.5f);
		particleSystemSmoke.setSpeedError(0.5f);
		particleSystemSmoke.setScaleError(0.5f);
		particleSystemSmoke.randomizeRotation();
		particleSystemSmoke.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f), 1f);
	}

	private void updateParticlesFire() {
		float coordX = -120;
		float coordZ = -800;
		float coordY = this.getCurrentTerrain().getHeightOfTerrain(coordX, coordZ);
		particleSystemFire.generateParticles(new Vector3f(coordX, coordY, coordZ));
		particleSystemSmoke.generateParticles(new Vector3f(coordX, coordY + 10, coordZ));
	}

	@Override
	public void update(float interval, Input input) {
		for(TexturedModel model: entities.keySet()) {
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch) {
				// entity.increaseRotation(0, 1, 0);
			}
		}
		updateParticles(input);
		updateText();
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
	
	private void setupTerrainsProcedural() {
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("terrain_1/bg"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("terrain_1/1"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("terrain_1/2"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("terrain_1/3"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("terrain_1/blendMap"));
		TerrainProcedural terrain_1 = new TerrainProcedural(-0.5f, -0.5f, loader, texturePack, blendMap, "terrain_1/heightmap");
		processTerrain(terrain_1);
		TerrainTexture blendMap_2 = new TerrainTexture(loader.loadTexture("terrain_2/blendMap"));
		TerrainProcedural terrain_2 = new TerrainProcedural(-0.5f, -1.5f, loader, texturePack, blendMap_2, "terrain_2/heightmap");
		processTerrain(terrain_2);
	}

	private void setupLights() {
		Light light_sun = new Light(new Vector3f(5000, 10000, 5000), new Vector3f(1, 1, 1));
		lights.add(light_sun);
	}

	private void setupGui() {
		GuiTexture reflection = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getReflectionTexture(), new Vector2f(0.86f, 0.84f), new Vector2f(0.12f, 0.12f));
		GuiTexture refraction = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getRefractionTexture(), new Vector2f(0.86f, 0.56f), new Vector2f(0.12f, 0.12f));
		GuiTexture minimap    = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getMinimapTexture(),    new Vector2f(-0.78f, 0.76f), new Vector2f(-0.2f, 0.2f));
		GuiTexture shadowMap  = new GuiTexture(MasterRenderer.getShadowMapTexture(),                               new Vector2f(-0.78f, 0.32f), new Vector2f(0.2f, 0.2f)); // new Vector2f(0.86f, 0.28f), new Vector2f(0.12f, 0.12f));
		GuiTexture mmTarget   = new GuiTexture(loader.loadTexture("gui/bullseye"), new Vector2f(-0.78f, 0.76f), new Vector2f(0.02f, 0.036f));
		processGui(reflection);
		processGui(refraction);
		processGui(shadowMap);
		processGui(minimap);
		processGui(mmTarget);
	}

	private void setupText() {
		font_1 = new FontType(loader.loadTexture("arial"), WorldSettings.FONTS_DIR + "/arial.fnt");
		font_2 = new FontType(loader.loadTexture("segoe"), WorldSettings.FONTS_DIR + "/segoe.fnt");
		text = new GUIText[10];
	}

	private void updateText() {
		Camera camera = (Camera) this.camera;
		TextMaster.emptyTextMap();
		float offsetX = 0.01f;
		float offsetY = 0.77f;
		Vector3f color = new Vector3f(1, 1, 1);
		String line_0 = "FPS: " + GameEngine.getFPS() + " / " + GameEngine.TARGET_FPS;
		String line_1 = "Player role: " + (fireMode ? "Warrior" : "Wizard");
		String line_2 = "Player position:  " + (int) player.getPosition().x + "  " + (int) player.getPosition().y + "  " + (int) player.getPosition().z;
		String line_3 = "Player rotation:  " + (int) player.getRotX() + "  " + Maths.angleTo360Range((int) player.getRotY()) + "  " + (int) player.getRotZ();
		String line_4 = "Camera position:  " + (int) camera.getPosition().x + "  " + (int) camera.getPosition().y + "  " + (int) camera.getPosition().z;
		String line_5 = "Camera rotation  [ pitch: " + (int) camera.getRotation().x + "  yaw: " + Maths.angleTo360Range((int) camera.getRotation().y) + "  roll: " + (int) camera.getRotation().z + " ]";
		String line_6 = "Particles active:  " + ParticleMaster.getParticlesCount();
		text[0] = new GUIText(line_0, 1, font_1, new Vector2f(offsetX, offsetY), 1f, false).setColor(color);
		text[1] = new GUIText(line_1, 1, font_1, new Vector2f(offsetX, offsetY + 0.03f), 1f, false).setColor(color);
		text[2] = new GUIText(line_2, 1, font_1, new Vector2f(offsetX, offsetY + 0.06f), 1f, false).setColor(color);
		text[3] = new GUIText(line_3, 1, font_1, new Vector2f(offsetX, offsetY + 0.09f), 1f, false).setColor(color);
		text[4] = new GUIText(line_4, 1, font_1, new Vector2f(offsetX, offsetY + 0.12f), 1f, false).setColor(color);
		text[5] = new GUIText(line_5, 1, font_1, new Vector2f(offsetX, offsetY + 0.15f), 1f, false).setColor(color);
		text[6] = new GUIText(line_6, 1, font_1, new Vector2f(offsetX, offsetY + 0.18f), 1f, false).setColor(color);
		TextMaster.loadText(text[0]);
		TextMaster.loadText(text[1]);
		TextMaster.loadText(text[2]);
		TextMaster.loadText(text[3]);
		TextMaster.loadText(text[4]);
		TextMaster.loadText(text[5]);
		TextMaster.loadText(text[6]);

		// GUIText distFieldText = new GUIText("A sample string of text!", 7, font_2, new Vector2f(0.0f, 0.4f), 1f, true).setColor(color);
		// TextMaster.loadText(distFieldText);
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

	public List<ITerrain> getTerrains() {
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

	public void processTerrain(ITerrain terrain) {
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

	public ITerrain getCurrentTerrain() {
		ITerrain currentTerrain = null;
		try {
			currentTerrain = getCurrentTerrain(player.getPosition().x, player.getPosition().z);
		} catch (Exception e) {
			System.out.println("Failed to retrieve the current terrain object.");
			e.printStackTrace();
		}
		return currentTerrain;
	}

	public ITerrain getCurrentTerrain(float x, float z) {
		ITerrain currentTerrain = null;
		for (ITerrain terrain : terrains) {
			if (x >= terrain.getX() && x < (terrain.getX() + Terrain.SIZE) &&
				z >= terrain.getZ() && z < (terrain.getZ() + Terrain.SIZE)) {
				currentTerrain = terrain;
			}
		}
		return currentTerrain;
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
