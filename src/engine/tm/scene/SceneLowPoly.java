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
import engine.tm.hybridTerrain.HybridTerrainGenerator;
import engine.tm.lensFlare.FlareManager;
import engine.tm.lensFlare.FlareTexture;
import engine.tm.loaders.Loader;
import engine.tm.loaders.OBJLoader;
import engine.tm.lowPoly.ColorGenerator;
import engine.tm.lowPoly.PerlinNoise;
import engine.tm.lowPoly.TerrainGenerator;
import engine.tm.lowPoly.TerrainLowPoly;
import engine.tm.lowPoly.WaterGenerator;
import engine.tm.lowPoly.WaterTileLowPoly;
import engine.tm.models.TexturedModel;
import engine.tm.openglObjects.Vao;
import engine.tm.particles.FireMaster;
import engine.tm.particles.ParticleMaster;
import engine.tm.particles.ParticleSystemShoot;
import engine.tm.particles.ParticleTexture;
import engine.tm.render.IMasterRenderer;
import engine.tm.render.MasterRenderer;
import engine.tm.render.MasterRendererLowPoly;
import engine.tm.settings.WorldSettings;
import engine.tm.skybox.Skybox;
import engine.tm.sunRenderer.ISun;
import engine.tm.sunRenderer.Sun;
import engine.tm.terrains.ITerrain;
import engine.tm.terrains.Terrain;
import engine.tm.textures.ModelTexture;
import engine.tm.textures.Texture;
import engine.tm.water.Water;
import engine.tm.water.WaterTile;
import engine.utils.Maths;
import engine.utils.MyFile;

public class SceneLowPoly implements IScene {

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

	private FlareManager flareManager;
	private Vector3f lightDirection = WorldSettings.LIGHT_DIR;

	private TerrainLowPoly terrainLowPoly;
	private WaterTileLowPoly waterLowPoly;
	private LightDirectional lightDirectional;
	
	private boolean wireframeEnabled = false;

	public SceneLowPoly() {
		camera = new Camera();
		loader = new Loader();
		skybox = new Skybox(loader);
		masterRenderer = new MasterRendererLowPoly();
	}

	public void init() {
		setupLowPolyTerrain();
		setupLowPolyWater();
		generateForestModels();
		setupAnimatedPlayer();
		setupCamera();
		setupLights();
		setupLensFlare();
		setupGui();
		setupText();
		masterRenderer.init(this); // should be called after entities list is populated
	}

	private void setupLowPolyTerrain() {
		// initialize terrain
		PerlinNoise noise = new PerlinNoise(WorldSettings.OCTAVES, WorldSettings.AMPLITUDE, WorldSettings.ROUGHNESS);
		ColorGenerator colorGen = new ColorGenerator(WorldSettings.TERRAIN_COLS, WorldSettings.COLOR_SPREAD);
		TerrainGenerator terrainGenerator = new HybridTerrainGenerator(noise, colorGen);
		terrainLowPoly = terrainGenerator.generateTerrain(WorldSettings.WORLD_SIZE);
	}

	private void setupLowPolyWater() {
		waterLowPoly = WaterGenerator.generate(WorldSettings.WORLD_SIZE, WorldSettings.WATER_HEIGHT);
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

			float coordX = rand.nextInt(WorldSettings.WORLD_SIZE);
			float coordZ = rand.nextInt(WorldSettings.WORLD_SIZE);
			float coordY = terrainLowPoly.getHeightOfTerrain(coordX, coordZ);
			if (coordY < 5) {
				continue;
			}

			int modelIndex = rand.nextInt(2);
			int modelSize = rand.nextInt(3) / 2;
			int fernTxIndex = rand.nextInt(4);

			switch (modelIndex) {
			case 0:
				entity = new Entity(fernModel, fernTxIndex, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, modelSize);
				entity.setSolid(false);
				break;
			case 1:
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

	private void setupAnimatedPlayer() {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(new MyFile(WorldSettings.MODELS_DIR + "/cowboy.dae"), WorldSettings.MAX_WEIGHTS);
		Vao model = AnimatedModelLoader.createVao(entityData.getMeshData());
		Texture texture = AnimatedModelLoader.loadTexture(new MyFile(WorldSettings.TEXTURES_DIR + "/cowboy.png"));
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = AnimatedModelLoader.createJoints(skeletonData.headJoint);
		player = new AnimatedPlayer(model, texture, headJoint, skeletonData.jointCount, new Vector3f(0, 0, 0), 0, 45, 0, 0.3f);
		Animation animation = AnimationLoader.loadAnimation(new MyFile(WorldSettings.MODELS_DIR + "/cowboy.dae"));
		((AnimatedModel) player).doAnimation(animation);
	}

	private void setupCamera() {
		((Camera) camera).setDistanceFromPlayer(10).setOffsetY(4.8f).setPitch(0);
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
		GuiTexture reflection  = new GuiTexture(MasterRendererLowPoly.reflectionFbo.getColorBuffer(0), new Vector2f(-0.78f,  0.76f), new Vector2f(0.2f, 0.2f));
		GuiTexture refraction  = new GuiTexture(MasterRendererLowPoly.refractionFbo.getColorBuffer(0), new Vector2f(-0.78f,  0.32f), new Vector2f(0.2f, 0.2f));
		GuiTexture depth       = new GuiTexture(MasterRendererLowPoly.refractionFbo.getDepthBuffer(),  new Vector2f(-0.78f, -0.12f), new Vector2f(0.2f, 0.2f));
		processGui(reflection);
		processGui(refraction);
		processGui(depth);
	}

	@Override
	public void update(float interval, Input input) {
		player.update();
		updateText();
		toggleWireframeMode(input);
	}

	private void toggleWireframeMode(Input input) {
		if (input.isKeyReleased(GLFW.GLFW_KEY_M)) {
			wireframeEnabled = !wireframeEnabled;
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

	public FlareManager getFlareManager() {
		return flareManager;
	}

	public ISun getSun() {
		return sun;
	}

	public LightDirectional getLightDirectional() {
		return lightDirectional;
	}

	private void setupText() {
		font_1 = new FontType(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/arial.png"), WorldSettings.FONTS_DIR + "/arial.fnt");
		text = new GUIText[10];
	}

	private void updateText() {
		Camera camera = (Camera) this.camera;
		TextMaster.emptyTextMap();
		float offsetX = 0.01f;
		float offsetY = 0.77f;
		Vector3f color = new Vector3f(1, 1, 1);
		String line_0 = "FPS: " + GameEngine.getFPS() + " / " + GameEngine.TARGET_FPS;
		String line_1 = "Player position:  " + (int) player.getPosition().x + "  " + (int) player.getPosition().y + "  " + (int) player.getPosition().z;
		String line_2 = "Player rotation:  " + (int) player.getRotX() + "  " + Maths.angleTo360Range((int) player.getRotY()) + "  " + (int) player.getRotZ();
		String line_3 = "Camera position:  " + (int) camera.getPosition().x + "  " + (int) camera.getPosition().y + "  " + (int) camera.getPosition().z;
		String line_4 = "Camera rotation  [ pitch: " + (int) camera.getRotation().x + "  yaw: " + Maths.angleTo360Range((int) camera.getRotation().y) + "  roll: " + (int) camera.getRotation().z + " ]";
		String line_5 = "Entities spawned:  " + getEntitiesCount();
		String line_6 = "Particles active:  " + ParticleMaster.getParticlesCount();
		text[0] = new GUIText(line_0, 1, font_1, new Vector2f(offsetX, offsetY), 1f, false).setColor(color);
		text[1] = new GUIText(line_1, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[2] = new GUIText(line_2, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[3] = new GUIText(line_3, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[4] = new GUIText(line_4, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[5] = new GUIText(line_5, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		text[6] = new GUIText(line_6, 1, font_1, new Vector2f(offsetX, offsetY += 0.03f), 1f, false).setColor(color);
		TextMaster.loadText(text[0]);
		TextMaster.loadText(text[1]);
		TextMaster.loadText(text[2]);
		TextMaster.loadText(text[3]);
		TextMaster.loadText(text[4]);
		TextMaster.loadText(text[5]);
		TextMaster.loadText(text[6]);
	}

	public int getEntitiesCount() {
		int count = 0;
		for (TexturedModel model : entities.keySet()) {
			count += entities.get(model).size();
		}
		return count;
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
		return (ITerrain) terrainLowPoly;
	}

	public ITerrain getCurrentTerrain(float x, float z) {
		return (ITerrain) terrainLowPoly;
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

	@Override
	public FireMaster getFireMaster() {
		return null;
	}
}
