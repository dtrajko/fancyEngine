package engine.tm.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import engine.GameEngine;
import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.Input;
import engine.tm.animation.animatedModel.AnimatedModel;
import engine.tm.animation.animatedModel.Joint;
import engine.tm.animation.animation.Animation;
import engine.tm.animation.loaders.AnimatedModelLoader;
import engine.tm.animation.loaders.AnimationLoader;
import engine.tm.colladaParser.colladaLoader.ColladaLoader;
import engine.tm.colladaParser.dataStructures.AnimatedModelData;
import engine.tm.colladaParser.dataStructures.SkeletonData;
import engine.tm.entities.AnimatedPlayer;
import engine.tm.entities.Camera;
import engine.tm.entities.Entity;
import engine.tm.entities.IPlayer;
import engine.tm.entities.Light;
import engine.tm.entities.LightDirectional;
import engine.tm.gui.GuiTexture;
import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;
import engine.tm.gui.fonts.TextMaster;
import engine.tm.lensFlare.FlareManager;
import engine.tm.lensFlare.FlareTexture;
import engine.tm.loaders.Loader;
import engine.tm.loaders.OBJLoader;
import engine.tm.lowPoly.TerrainLowPoly;
import engine.tm.lowPoly.WaterTileLowPoly;
import engine.tm.models.TexturedModel;
import engine.tm.normalMapping.NormalMappedObjLoader;
import engine.tm.openglObjects.Vao;
import engine.tm.particles.FireMaster;
import engine.tm.particles.ParticleMaster;
import engine.tm.particles.ParticleSystemShoot;
import engine.tm.particles.ParticleTexture;
import engine.tm.render.IMasterRenderer;
import engine.tm.render.MasterRenderer;
import engine.tm.settings.WorldSettings;
import engine.tm.skybox.Skybox;
import engine.tm.sunRenderer.ISun;
import engine.tm.sunRenderer.Sun;
import engine.tm.terrains.ITerrain;
import engine.tm.terrains.Terrain;
import engine.tm.textures.ModelTexture;
import engine.tm.textures.TerrainTexture;
import engine.tm.textures.TerrainTexturePack;
import engine.tm.textures.Texture;
import engine.tm.water.Water;
import engine.tm.water.WaterTile;
import engine.utils.Maths;
import engine.utils.MyFile;

public class Scene implements IScene {

	private Loader loader;
	private IMasterRenderer masterRenderer;
	private ICamera camera;
	private IPlayer player;
	private Skybox skybox;
	private Water water;
	private Sun sun;

	private List<Light> lights = new ArrayList<Light>();
	// contains both regular and normal map entities
	private static Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<ITerrain> terrains = new ArrayList<ITerrain>();
	private List<WaterTile> waterTiles = new ArrayList<WaterTile>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();

	private FontType font_1;
	private GUIText[] text;

	private ParticleTexture particleTexture;
	private ParticleSystemShoot particleSystemShoot;
	private boolean fireMode = true;
	private FireMaster fireManager;
	private FlareManager flareManager;
	private Vector3f lightDirection = WorldSettings.LIGHT_DIR;

	private TerrainLowPoly terrainLowPoly;
	private WaterTileLowPoly waterLowPoly;
	private LightDirectional lightDirectional;
	private boolean wireframeEnabled;

	public Scene() {
		camera = new Camera();
		loader = new Loader();
		skybox = new Skybox(loader);
		masterRenderer = new MasterRenderer();
		fireManager = new FireMaster(loader);
		wireframeEnabled = false;
	}

	public void init() {
		setupTerrain();
		generateForestModels();
		generateNormalMapEntities();
		setupAnimatedPlayer();
		setupWater();
		setupLights();
		setupLensFlare();
		setupParticles();
		setupGui();
		setupText();
		masterRenderer.init(this); // should be called after entities list is populated
	}

	private void setupLensFlare() {
		// loading textures for lens flare
		Texture texture1 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex1.png")).normalMipMap().create();
		Texture texture2 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex2.png")).normalMipMap().create();
		Texture texture3 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex3.png")).normalMipMap().create();
		Texture texture4 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex4.png")).normalMipMap().create();
		Texture texture5 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex5.png")).normalMipMap().create();
		Texture texture6 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex6.png")).normalMipMap().create();
		Texture texture7 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex7.png")).normalMipMap().create();
		Texture texture8 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex8.png")).normalMipMap().create();
		Texture texture9 = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/tex9.png")).normalMipMap().create();
		Texture textureSun = Texture.newTexture(new MyFile(WorldSettings.LENS_FLARE_DIR + "/sun.png")).normalMipMap().create();

		// set up lens flare
		flareManager = new FlareManager(0.16f,
			new FlareTexture(texture6, 1f),
			new FlareTexture(texture4, 0.46f),
			new FlareTexture(texture2, 0.2f),
			new FlareTexture(texture7, 0.1f),
			new FlareTexture(texture1, 0.04f),
			new FlareTexture(texture3, 0.12f),
			new FlareTexture(texture9, 0.24f),
			new FlareTexture(texture5, 0.14f),
			new FlareTexture(texture1, 0.024f),
			new FlareTexture(texture7, 0.4f),
			new FlareTexture(texture9, 0.2f),
			new FlareTexture(texture3, 0.14f),
			new FlareTexture(texture5, 0.6f),
			new FlareTexture(texture4, 0.8f),
			new FlareTexture(texture8, 1.2f)
		);

		//init sun and set sun direction
		sun = new Sun(textureSun, 20);
		sun.setDirection(WorldSettings.LIGHT_DIR);
		lightDirectional = new LightDirectional(WorldSettings.LIGHT_DIR, WorldSettings.LIGHT_COL, WorldSettings.LIGHT_BIAS);
	}

	private void setupAnimatedPlayer() {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(new MyFile(WorldSettings.MODELS_DIR + "/cowboy.dae"), WorldSettings.MAX_WEIGHTS);
		Vao model = AnimatedModelLoader.createVao(entityData.getMeshData());
		Texture texture = AnimatedModelLoader.loadTexture(new MyFile(WorldSettings.TEXTURES_DIR + "/cowboy.png"));
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = AnimatedModelLoader.createJoints(skeletonData.headJoint);
		player = new AnimatedPlayer(model, texture, headJoint, skeletonData.jointCount, new Vector3f(0, 2, -50), 0, 180, 0, 1.5f);
		Animation animation = AnimationLoader.loadAnimation(new MyFile(WorldSettings.MODELS_DIR + "/cowboy.dae"));
		((AnimatedModel) player).doAnimation(animation);
	}

	public void processEntity(Entity entity) {

		// set the bounding box
		entity.setBoundingBox();

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

	public void removeEntity(Entity entity) {
		for (TexturedModel model : entities.keySet()) {
			entities.get(model).remove(entity);
		}
		masterRenderer.init(this);
	}

	private void setupLights() {
		Light light_sun = new Light(new Vector3f(-5000, 8000, -5000), new Vector3f(1, 1, 1));
		lights.add(light_sun);
	}

	private void setupGui() {
		GuiTexture reflection  = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getReflectionTexture(), new Vector2f(0.86f, 0.84f), new Vector2f(0.12f, 0.12f));
		GuiTexture refraction  = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getRefractionTexture(), new Vector2f(0.86f, 0.56f), new Vector2f(0.12f, 0.12f));
		GuiTexture minimap     = new GuiTexture(MasterRenderer.getWaterRenderer().getFBOs().getMinimapTexture(),    new Vector2f(-0.78f, 0.76f), new Vector2f(-0.2f, 0.2f));
		GuiTexture shadowMap   = new GuiTexture(MasterRenderer.getShadowMapTexture(),                               new Vector2f(-0.78f, 0.32f), new Vector2f(0.2f, 0.2f)); // new Vector2f(0.86f, 0.28f), new Vector2f(0.12f, 0.12f));
		GuiTexture mmTargetMap = new GuiTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/gui/bullseye.png"), new Vector2f(-0.78f, 0.76f), new Vector2f(0.02f, 0.036f));
		GuiTexture mmTarget    = new GuiTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/gui/bullseye.png"), new Vector2f(0.0f, 0.0f), new Vector2f(0.02f, 0.036f));
		processGui(reflection);
		processGui(refraction);
		processGui(shadowMap);
		processGui(minimap);
		processGui(mmTargetMap);
		processGui(mmTarget);
	}

	@Override
	public void update(float interval, Input input) {
		player.update();
		updateParticles(input);
		updateText();
		fireManager.update();
		toggleWireframeMode(input);
	}

	private void toggleWireframeMode(Input input) {
		if (input.isKeyReleased(GLFW.GLFW_KEY_M)) {
			wireframeEnabled  = !wireframeEnabled;
		}
		if (wireframeEnabled) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);			
		} else {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
	}

	public IMasterRenderer getMasterRenderer() {
		return masterRenderer;
	}

	public FireMaster getFireMaster() {
		return fireManager;
	}

	public FlareManager getFlareManager() {
		return flareManager;
	}

	public ISun getSun() {
		return sun;
	}

	public LightDirectional getLightDirectional() {
		return lightDirectional;
	}

	private void setupParticles() {
		fireMode = true;
		particleTexture = new ParticleTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/particles/particleAtlas.png"), 4, true);
		particleSystemShoot = new ParticleSystemShoot(particleTexture, 400f, 10f, 0.0f, 2f);
	}

	private void updateParticles(Input input) {
		if (input.isKeyReleased(GLFW.GLFW_KEY_F)) {
			fireMode = !fireMode;
			if (fireMode) {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 200f, 60f, 0.0f, 1f);
			} else {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 20f, 50f, -0.25f, 5f); // magic circle around the player
			}
		}
		if (input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_3)) {
			float coordX = player.getPosition().x;
			float coordY = player.getPosition().y + 6;
			float coordZ = player.getPosition().z;
			float playerDX = (float) (Math.sin(Math.toRadians(player.getRotY())));
			float playerDY = 0;
			float playerDZ = (float) (Math.cos(Math.toRadians(player.getRotY())));
			Vector3f playerDirection = new Vector3f(playerDX, playerDY, playerDZ);
			particleSystemShoot.generateParticles(new Vector3f(coordX, coordY, coordZ), playerDirection);
		}
	}

	private void setupWater() {
		water = new Water(loader);
		WaterTile waterTile = new WaterTile(0, Water.HEIGHT, 0);
		WaterTile waterTile_2 = new WaterTile(0, Water.HEIGHT, -WaterTile.TILE_SIZE * 2);
		processWaterTile(waterTile);
		processWaterTile(waterTile_2);
	}

	private void setupTerrain() {
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/terrain_1/bg.png"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/terrain_1/1.png"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/terrain_1/2.png"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/terrain_1/3.png"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/terrain_1/blendMap.png"));
		Terrain terrain_1 = new Terrain(-0.5f, -0.5f, loader, texturePack, blendMap, "terrain_1/heightmap");
		processTerrain(terrain_1);
		TerrainTexture blendMap_2 = new TerrainTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/terrain_2/blendMap.png"));
		Terrain terrain_2 = new Terrain(-0.5f, -1.5f, loader, texturePack, blendMap_2, "terrain_2/heightmap");
		processTerrain(terrain_2);
	}

	private void setupText() {
		font_1 = new FontType(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/arial.png"), WorldSettings.FONTS_DIR + "/arial.fnt");
		text = new GUIText[10];
	}

	private void updateText() {
		Camera camera = (Camera) this.camera;
		TextMaster.emptyTextMap();
		float offsetX = 0.01f;
		float offsetY = 0.74f;
		Vector3f color = new Vector3f(1, 1, 1);
		String line_0 = "FPS: " + GameEngine.getFPS() + " / " + GameEngine.TARGET_FPS;
		String line_1 = "Player role: " + (fireMode ? "Warrior" : "Wizard");
		String line_2 = "Player position:  " + (int) player.getPosition().x + "  " + (int) player.getPosition().y + "  " + (int) player.getPosition().z;
		String line_3 = "Player rotation:  " + (int) player.getRotX() + "  " + Maths.angleTo360Range((int) player.getRotY()) + "  " + (int) player.getRotZ();
		String line_4 = "Camera position:  " + (int) camera.getPosition().x + "  " + (int) camera.getPosition().y + "  " + (int) camera.getPosition().z;
		String line_5 = "Camera rotation  [ pitch: " + (int) camera.getRotation().x + "  yaw: " + Maths.angleTo360Range((int) camera.getRotation().y) + "  roll: " + (int) camera.getRotation().z + " ]";
		String line_6 = "Entities spawned:  " + getEntitiesCount();
		String line_7 = "Particles active:  " + ParticleMaster.getParticlesCount();
		text[0] = new GUIText(line_0, 1, font_1, new Vector2f(offsetX, offsetY), 1f, false).setColor(color);
		text[1] = new GUIText(line_1, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[2] = new GUIText(line_2, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[3] = new GUIText(line_3, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[4] = new GUIText(line_4, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[5] = new GUIText(line_5, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[6] = new GUIText(line_6, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[7] = new GUIText(line_7, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		TextMaster.loadText(text[0]);
		TextMaster.loadText(text[1]);
		TextMaster.loadText(text[2]);
		TextMaster.loadText(text[3]);
		TextMaster.loadText(text[4]);
		TextMaster.loadText(text[5]);
		TextMaster.loadText(text[6]);
		TextMaster.loadText(text[7]);
	}

	public int getEntitiesCount() {
		int count = 0;
		for (TexturedModel model : entities.keySet()) {
			count += entities.get(model).size();
		}
		return count;
	}

	private void generateForestModels() {
		Random rand = new Random();
		Entity entity = null;
		ModelTexture grassTexture = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/grassTexture.png"));
		grassTexture.setTransparent(true).setUseFakeLighting(true);
		TexturedModel grassModel = new TexturedModel(OBJLoader.loadOBJModel("grassModel", loader), grassTexture);		
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/fern_atlas.png"));
		fernTextureAtlas.setNumberOfRows(2);
		fernTextureAtlas.setTransparent(true).setUseFakeLighting(true);
		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);
		TexturedModel pineModel = new TexturedModel(OBJLoader.loadOBJModel("pine", loader), new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/pine.png")));

		int modelsSpawned = 0;
		while (modelsSpawned < 100) {
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
				entity.setSolid(false);
				break;
			case 1:				
				entity = new Entity(fernModel, fernTxIndex, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, modelSize);
				entity.setSolid(false);
				break;
			case 2:
				entity = new Entity(pineModel, 0, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, modelSize);
				entity.setSolid(true);
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
		ModelTexture crateTexture = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/normalMaps/crate.png"));
		crateTexture.setNormalMap(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/normalMaps/crateNormal.png"));
		crateTexture.setShineDamper(10);
		crateTexture.setReflectivity(0.5f);
		TexturedModel crateTexturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("crate", loader), crateTexture);
		float coordX = -100;
		float coordZ = -280;
		float coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ);
		Entity crateModel = new Entity(crateTexturedModel, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, 0.05f);
		crateModel.setSolid(true);
		processEntity(crateModel);

		ModelTexture barrelTexture = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/normalMaps/barrel.png"));
		barrelTexture.setNormalMap(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/normalMaps/barrelNormal.png"));
		barrelTexture.setShineDamper(10);
		barrelTexture.setReflectivity(0.5f);
		TexturedModel barrelTexturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader), barrelTexture);
		coordX = -200;
		coordZ = -305;
		coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ);
		Entity barrelModel = new Entity(barrelTexturedModel, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, 2f);
		barrelModel.setSolid(true);
		processEntity(barrelModel);

		ModelTexture boulderTexture = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/normalMaps/boulder.png"));
		boulderTexture.setNormalMap(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/normalMaps/boulderNormal.png"));
		boulderTexture.setShineDamper(10);
		boulderTexture.setReflectivity(0.5f);
		TexturedModel boulderTexturedModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("boulder", loader), boulderTexture);
		coordX = 260;
		coordZ = -330;
		coordY = getCurrentTerrain(coordX, coordZ).getHeightOfTerrain(coordX, coordZ);
		Entity boulderModel = new Entity(boulderTexturedModel, new Vector3f(coordX, coordY, coordZ), 0, 90, 45, 2f);
		boulderModel.setSolid(true);
		Entity boulderModel2 = new Entity(boulderTexturedModel, new Vector3f(coordX + 20, coordY, coordZ), -90, 0, 0, 2f);
		boulderModel2.setSolid(true);
		processEntity(boulderModel);
		processEntity(boulderModel2);
	}

	public Map<TexturedModel, List<Entity>> getEntityList() {
		return entities;
	}

	public List<ITerrain> getTerrains() {
		return terrains;
	}

	public TerrainLowPoly getTerrainLowPoly() {
		return terrainLowPoly;
	}

	public WaterTileLowPoly getWaterLowPoly() {
		return waterLowPoly;
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

	public IPlayer getPlayer() {
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
	}

	@Override
	public void save() {
	}

	public void clearLists() {
		terrains.clear();
		entities.clear();
		lights.clear();
		waterTiles.clear();
		guis.clear();
	}

	@Override
	public void cleanUp() {
		clearLists();
		loader.cleanUp();
		flareManager.cleanUp();
	}

	public Vector3f getLightDirection() {
		return lightDirection;
	}
}
