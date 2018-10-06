package engine.tm.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import engine.GameEngine;
import engine.Window;
import engine.graph.Input;
import engine.interfaces.ICamera;
import engine.interfaces.IGameLogic;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IPlayer;
import engine.interfaces.IScene;
import engine.interfaces.ISun;
import engine.interfaces.ITerrain;
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
import engine.tm.entities.Light;
import engine.tm.entities.LightDirectional;
import engine.tm.gui.GuiTexture;
import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;
import engine.tm.gui.fonts.TextMaster;
import engine.tm.hybridTerrain.HybridTerrainGenerator;
import engine.tm.infiniteTerrain.InfiniteTerrainChunk;
import engine.tm.infiniteTerrain.InfiniteTerrainManager;
import engine.tm.infiniteTerrain.InfiniteWaterChunk;
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
import engine.tm.render.MasterRendererLowPoly;
import engine.tm.settings.WorldSettings;
import engine.tm.skybox.Skybox;
import engine.tm.sunRenderer.Sun;
import engine.tm.textures.ModelTexture;
import engine.tm.textures.Texture;
import engine.tm.water.Water;
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
	private List<WaterTileLowPoly> waterTiles = new ArrayList<WaterTileLowPoly>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();

	private FontType font_1;
	private GUIText[] text;

	private FireMaster fireMaster;
	private ParticleTexture particleTexture;
	private ParticleSystemShoot particleSystemShoot;
	private boolean fireMode;

	private FlareManager flareManager;
	private Vector3f lightDirection = WorldSettings.LIGHT_DIR;
	private LightDirectional lightDirectional;

	public static float waterLevelOffset = 0;
	private int gridSize = WorldSettings.GRID_SIZE;
	private static float terrainScale = 5;
	private static TerrainGenerator terrainGenerator;
	private double lastTerrainUpdate;
	private InfiniteTerrainManager infiniteTerrainManager;
	private Vector2i previousTerrainIndices = new Vector2i(0, 0);

	private boolean wireframeEnabled;

	public SceneLowPoly() {
		camera = new Camera();
		loader = new Loader();
		skybox = new Skybox(loader);
		masterRenderer = new MasterRendererLowPoly();
		wireframeEnabled = false;
		fireMaster = new FireMaster(loader);
		fireMode = true;
		infiniteTerrainManager = new InfiniteTerrainManager();
	}

	public void init() {
		lastTerrainUpdate = GameEngine.getTimer().getLastLoopTime();
		setupTerrainGenerator();
		setupLowPolyTerrain();
		setupLowPolyWater();
		// generateForestModels();
		setupAnimatedPlayer();
		setupCamera();
		setupLights();
		setupLensFlare();
		setupParticles();
		setupGui();
		setupText();
		masterRenderer.init(this); // should be called after entities list is populated
	}

	private void setupTerrainGenerator() {
		PerlinNoise noise = new PerlinNoise(WorldSettings.OCTAVES, WorldSettings.AMPLITUDE, WorldSettings.ROUGHNESS);
		ColorGenerator colorGen = new ColorGenerator(WorldSettings.TERRAIN_COLS, WorldSettings.COLOR_SPREAD);
		Matrix4f projectionMatrix = MasterRendererLowPoly.createProjectionMatrix();
		terrainGenerator = new HybridTerrainGenerator(projectionMatrix, noise, colorGen);		
	}

	private void setupLowPolyTerrain() {
		generateTerrainChunks(0, 0);
	}

	private void setupLowPolyWater() {
		generateWaterChunks(0, 0);
	}

	private void updateInfiniteTerrain(Input input) {
		if (GameEngine.getTimer().getLastLoopTime() - lastTerrainUpdate > 1) {
			float playerX = player != null ? player.getPosition().x : 0;
			float playerZ = player != null ? player.getPosition().z : 0;
			int indX = (int) (playerX / WorldSettings.WORLD_SIZE / terrainScale);
			int indZ = (int) (playerZ / WorldSettings.WORLD_SIZE / terrainScale);
			int prevIndX = previousTerrainIndices.x;
			int prevIndZ = previousTerrainIndices.y;
			if (indX != prevIndX || indZ != prevIndZ) { // ignore if no change
				int nextIndX = indX + (indX - prevIndX);
				int nextIndZ = indZ + (indZ - prevIndZ);
				generateTerrainChunks(nextIndX, nextIndZ);
				generateWaterChunks(nextIndX, nextIndZ);
				lastTerrainUpdate = GameEngine.getTimer().getLastLoopTime();
				previousTerrainIndices = new Vector2i(indX, indZ);				
			}
		}
	}

	private void generateTerrainChunks(int indX, int indZ) {
		for (int x = -gridSize / 2; x <= gridSize / 2; x++) {
			for (int z = -gridSize / 2; z <= gridSize / 2; z++) {
				if (!infiniteTerrainManager.terrainChunkExists(x + indX, z + indZ)) {
					TerrainLowPoly terrainLowPoly = terrainGenerator.generateTerrain(
							(x + indX + gridSize / 2) * WorldSettings.WORLD_SIZE,
							(z + indZ + gridSize / 2) * WorldSettings.WORLD_SIZE,
							WorldSettings.WORLD_SIZE, terrainScale);
					terrainLowPoly.setX((int) ((x + indX) * WorldSettings.WORLD_SIZE * terrainScale));
					terrainLowPoly.setZ((int) ((z + indZ) * WorldSettings.WORLD_SIZE * terrainScale));
					processTerrain(terrainLowPoly);
					infiniteTerrainManager.addTerrainChunk(new InfiniteTerrainChunk(terrainLowPoly, x + indX, z + indZ));
				}
			}
		}
		List<InfiniteTerrainChunk> remoteTerrainChunks = infiniteTerrainManager.getRemoteTerrainChunks(indX, indZ);
		Iterator<InfiniteTerrainChunk> iterator = remoteTerrainChunks.iterator();
		while (iterator.hasNext()) {
			InfiniteTerrainChunk terrainChunk = iterator.next();
			terrains.remove(terrainChunk.getTerrain());
			infiniteTerrainManager.removeTerrainChunk(terrainChunk);
		}
	}

	private void generateWaterChunks(int indX, int indZ) {
		for (int x = -gridSize / 2; x <= gridSize / 2; x++) {
			for (int z = -gridSize / 2; z <= gridSize / 2; z++) {
				if (!infiniteTerrainManager.waterChunkExists(x + indX, z + indZ)) {
					WaterTileLowPoly waterLowPoly = WaterGenerator.generate(WorldSettings.WORLD_SIZE, WorldSettings.WATER_HEIGHT, terrainScale);
					waterLowPoly.setX((int) ((x + indX) * WorldSettings.WORLD_SIZE * terrainScale));
					waterLowPoly.setZ((int) ((z + indZ) * WorldSettings.WORLD_SIZE * terrainScale));
					this.processWaterTile(waterLowPoly);
					infiniteTerrainManager.addWaterChunk(new InfiniteWaterChunk(waterLowPoly, x + indX, z + indZ));
				}
			}
		}
		List<InfiniteWaterChunk> remoteWaterChunks = infiniteTerrainManager.getRemoteWaterChunks(indX, indZ);
		Iterator<InfiniteWaterChunk> iterator = remoteWaterChunks.iterator();
		while (iterator.hasNext()) {
			InfiniteWaterChunk waterChunk = iterator.next();
			waterTiles.remove(waterChunk.getWaterTile());
			infiniteTerrainManager.removeWaterChunk(waterChunk);
		}
	}

	private void generateForestModels() {
		Random rand = new Random();
		Entity entity;
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/fern_atlas.png"));
		fernTextureAtlas.setNumberOfRows(2);
		fernTextureAtlas.setTransparent(true).setUseFakeLighting(true);
		TexturedModel fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);
		TexturedModel pineModel = new TexturedModel(OBJLoader.loadOBJModel("pine", loader), new ModelTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/pine.png")));

		int modelsSpawnedMax = WorldSettings.WORLD_SIZE * gridSize / 10 * 20;

		int modelsSpawned = 0;
		while (modelsSpawned < modelsSpawnedMax) {

			int coordX = (int) (rand.nextFloat() * WorldSettings.WORLD_SIZE - WorldSettings.WORLD_SIZE / 2);
			int coordZ = (int) (rand.nextFloat() * WorldSettings.WORLD_SIZE - WorldSettings.WORLD_SIZE / 2);
			coordX = (int) (coordX * terrainScale * gridSize);
			coordZ = (int) (coordZ * terrainScale * gridSize);
			ITerrain currentTerrain = getCurrentTerrain(coordX, coordZ);
			float coordY = currentTerrain != null ? currentTerrain.getHeightOfTerrain(coordX, coordZ) : WorldSettings.WATER_HEIGHT;			
			if (coordY < WorldSettings.WATER_HEIGHT + 20) {
				continue;
			}

			int modelIndex = rand.nextInt(2);
			float modelSize = rand.nextFloat() + 1;
			int fernTxIndex = rand.nextInt(4);

			entity = null;

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
				modelsSpawned++;
			}
		}
	}

	private void setupAnimatedPlayer() {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(new MyFile(WorldSettings.MODELS_DIR + "/cowboy.dae"), WorldSettings.MAX_WEIGHTS);
		Vao model = AnimatedModelLoader.createVao(entityData.getMeshData());
		Texture texture = AnimatedModelLoader.loadTexture(new MyFile(WorldSettings.TEXTURES_DIR + "/cowboy.png"));
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = AnimatedModelLoader.createJoints(skeletonData.headJoint);
		int coordX = 0;
		int coordZ = 0;
		ITerrain currentTerrain = getCurrentTerrain(coordX, coordZ);
		float coordY = currentTerrain != null ? currentTerrain.getHeightOfTerrain(coordX, coordZ) : WorldSettings.WATER_HEIGHT;
		player = new AnimatedPlayer(model, texture, headJoint, skeletonData.jointCount, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, 1.0f);
		Animation animation = AnimationLoader.loadAnimation(new MyFile(WorldSettings.MODELS_DIR + "/cowboy.dae"));
		((AnimatedModel) player).doAnimation(animation);
	}

	private void setupCamera() {
		((Camera) camera).setDistanceFromPlayer(30).setOffsetY(15f).setPitch(0);
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

	private void setupParticles() {
		fireMode = true;
		particleTexture = new ParticleTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/particles/particleAtlas.png"), 4, true);
		particleSystemShoot = new ParticleSystemShoot(particleTexture, 800f, 20f, 0.0f, 0.5f);
	}

	private void updateParticles(Input input) {
		if (input.isKeyReleased(GLFW.GLFW_KEY_F)) {
			fireMode = !fireMode;
			if (fireMode) {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 800f, 20f, 0.0f, 0.5f);
			} else {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 20f, 50f, -0.25f, 2f); // magic circle around the player
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
			particleSystemShoot.generateParticles(camera, new Vector3f(coordX, coordY, coordZ), playerDirection);
		}
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
		GuiTexture aimTarget   = new GuiTexture(loader.loadTexture(WorldSettings.TEXTURES_DIR + "/gui/bullseye.png"), new Vector2f(0.0f, 0.0f), new Vector2f(0.02f, 0.036f));

		processGui(reflection);
		processGui(refraction);
		processGui(depth);
		processGui(aimTarget);
	}

	@Override
	public void update(float interval, Input input) {
		player.update();
		updateInfiniteTerrain(input);
		updateWaterLevel(input);
		updateParticles(input);
		fireMaster.update();
		toggleWireframeMode(input);
		updateText(interval);
	}

	private void updateWaterLevel(Input input) {
		if (input.isKeyDown(GLFW.GLFW_KEY_PAGE_UP)) {
			waterLevelOffset += 0.1f;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_PAGE_DOWN)) {
			waterLevelOffset -= 0.1f;
		}
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

	@Override
	public FireMaster getFireMaster() {
		return fireMaster;
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

	private void updateText(float interval) {
		Camera camera = (Camera) this.camera;
		TextMaster.emptyTextMap();
		float offsetX = 0.01f;
		float offsetY = 0.74f;
		Vector3f color = new Vector3f(1, 1, 1);
		String line_0 = "FPS: " + GameEngine.getFPS() + " / " + GameEngine.TARGET_FPS;
		String line_1 = "Player position:  " + (int) player.getPosition().x + "  " + (int) player.getPosition().y + "  " + (int) player.getPosition().z;
		String line_2 = "Player rotation:  " + (int) player.getRotX() + "  " + Maths.angleTo360Range((int) player.getRotY()) + "  " + (int) player.getRotZ();
		String line_3 = "Camera position:  " + (int) camera.getPosition().x + "  " + (int) camera.getPosition().y + "  " + (int) camera.getPosition().z;
		String line_4 = "Camera rotation  [ pitch: " + (int) camera.getRotation().x + "  yaw: " + Maths.angleTo360Range((int) camera.getRotation().y) + "  roll: " + (int) camera.getRotation().z + " ]";
		String line_5 = "Entities spawned:  " + getEntitiesCount();
		String line_6 = "Particles active:  " + ParticleMaster.getParticlesCount();
		String line_7 = "Visible terrain chunks: " + infiniteTerrainManager.getVisibleTerrainChunks().size();
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

	public Map<TexturedModel, List<Entity>> getEntityList() {
		return entities;
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

	public List<WaterTileLowPoly> getWaterTiles() {
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

	public void processWaterTile(WaterTileLowPoly waterTile) {
		waterTiles.add(waterTile);
	}

	public void processGui(GuiTexture gui) {
		guis.add(gui);
	}

	public ITerrain getCurrentTerrain(int worldX, int worldZ) {
		ITerrain currentTerrain = null;
		for (ITerrain terrain : terrains) {			
			if (worldX >= (terrain.getX() - WorldSettings.WORLD_SIZE * terrainScale / 2) && worldX < (terrain.getX() + WorldSettings.WORLD_SIZE * terrainScale / 2) &&
				worldZ >= (terrain.getZ() - WorldSettings.WORLD_SIZE * terrainScale / 2) && worldZ < (terrain.getZ() + WorldSettings.WORLD_SIZE * terrainScale / 2)) {
				currentTerrain = terrain;
				break;
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
		fireMaster.cleanUp();
	}

	public Vector3f getLightDirection() {
		return lightDirection;
	}
}
