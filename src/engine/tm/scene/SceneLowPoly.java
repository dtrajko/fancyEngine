package engine.tm.scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.joml.Matrix4f;
import org.joml.Vector2f;
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
import engine.interfaces.ISkybox;
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
import engine.tm.lowPoly.SkyboxLowPoly;
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
import engine.tm.settings.WorldSettingsLowPoly;
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
	private ISkybox skybox;
	private Water water;
	private Sun sun;

	private List<Light> lights = new ArrayList<Light>();
	// contains both regular and normal map entities
	private static Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<ITerrain> terrains = new ArrayList<ITerrain>();
	private List<WaterTileLowPoly> waterTiles = new ArrayList<WaterTileLowPoly>();
	private List<GuiTexture> guis = new ArrayList<GuiTexture>();

	private TextMaster textMaster;
	private FontType font_1;
	private String[] lines;
	private GUIText[] guiTexts;

	private FireMaster fireMaster;
	private ParticleTexture particleTexture;
	private ParticleSystemShoot particleSystemShoot;
	private boolean fireMode;

	private FlareManager flareManager;
	private Vector3f lightDirection = WorldSettingsLowPoly.LIGHT_DIR;
	private LightDirectional lightDirectional;

	private boolean wireframeEnabled;

	public static float waterLevelOffset = 0;
	private int gridSize = WorldSettingsLowPoly.GRID_SIZE;
	private static float terrainScale = WorldSettingsLowPoly.TERRAIN_SCALE;
	private static TerrainGenerator terrainGenerator;
	private double lastTerrainUpdate;
	private InfiniteTerrainManager infiniteTerrainManager;
	// private Vector2i prevTerrainIndices = new Vector2i(0, 0);
	private TexturedModel fernModel;
	private TexturedModel pineModel;
	private Random rand;
	private TerrainLowPoly terrainHiFi;

	public SceneLowPoly() {
		camera = new Camera();
		loader = new Loader();
		skybox = new SkyboxLowPoly(loader);
		masterRenderer = new MasterRendererLowPoly();
		wireframeEnabled = false;
		fireMaster = new FireMaster(loader);
		fireMode = true;
		infiniteTerrainManager = new InfiniteTerrainManager();
		textMaster = new TextMaster(loader);
		rand = new Random();
	}

	public void init() {
		lastTerrainUpdate = GameEngine.getTimer().getLastLoopTime();
		setupTerrainGenerator();
		setupTerrainObjects();
		setupLowPolyTerrain();
		setupLowPolyWater();
		setupAnimatedPlayer();
		setupCamera();
		setupLights();
		setupLensFlare();
		setupParticles();
		setupGui();
		setupText();
		masterRenderer.init(this);
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

	private void setupTerrainGenerator() {
		PerlinNoise noise = new PerlinNoise(WorldSettingsLowPoly.OCTAVES, WorldSettingsLowPoly.AMPLITUDE, WorldSettingsLowPoly.ROUGHNESS);
		ColorGenerator colorGen = new ColorGenerator(WorldSettingsLowPoly.TERRAIN_COLS, WorldSettingsLowPoly.COLOR_SPREAD);
		Matrix4f projectionMatrix = MasterRendererLowPoly.createProjectionMatrix();
		terrainGenerator = new HybridTerrainGenerator(projectionMatrix, noise, colorGen);		
	}

	private void setupTerrainObjects() {
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture(WorldSettingsLowPoly.TEXTURES_DIR + "/fern_atlas.png"));
		fernTextureAtlas.setNumberOfRows(2);
		fernTextureAtlas.setTransparent(true).setUseFakeLighting(true);
		fernModel = new TexturedModel(OBJLoader.loadOBJModel("fern", loader), fernTextureAtlas);
		pineModel = new TexturedModel(OBJLoader.loadOBJModel("pine", loader), new ModelTexture(loader.loadTexture(WorldSettingsLowPoly.TEXTURES_DIR + "/pine.png")));
	}

	private void setupLowPolyTerrain() {
		generateTerrainChunks(0, 0);
	}

	private void setupLowPolyWater() {
		generateWaterChunks(0, 0);
	}

	private void updateInfiniteTerrain(Input input) {
		if (GameEngine.getTimer().getLastLoopTime() - lastTerrainUpdate > 1) {

			float projDistance = WorldSettingsLowPoly.WORLD_SIZE / 2 * terrainScale;
			float playerX = player != null ? ((AnimatedPlayer) player).getProjectedFuturePosition(projDistance).x : 0;
			float playerZ = player != null ? ((AnimatedPlayer) player).getProjectedFuturePosition(projDistance).z : 0;
			int indX = (int) (playerX / WorldSettingsLowPoly.WORLD_SIZE / terrainScale);
			int indZ = (int) (playerZ / WorldSettingsLowPoly.WORLD_SIZE / terrainScale);

			float projDistanceFuture = WorldSettingsLowPoly.WORLD_SIZE * 2 * terrainScale;
			float playerFutureX = player != null ? ((AnimatedPlayer) player).getProjectedFuturePosition(projDistanceFuture).x : 0;
			float playerFutureZ = player != null ? ((AnimatedPlayer) player).getProjectedFuturePosition(projDistanceFuture).z : 0;
			int futureIndX = (int) (playerFutureX / WorldSettingsLowPoly.WORLD_SIZE / terrainScale);
			int futureIndZ = (int) (playerFutureZ / WorldSettingsLowPoly.WORLD_SIZE / terrainScale);
			generateTerrainChunks(futureIndX, futureIndZ);
			generateWaterChunks(futureIndX, futureIndZ);
			manageLOD(indX, indZ);
			lastTerrainUpdate = GameEngine.getTimer().getLastLoopTime();
			// prevTerrainIndices = new Vector2i(futureIndX, futureIndZ);
			((MasterRendererLowPoly) masterRenderer).initInfinite(this);
		}
	}

	private void manageLOD(int indX, int indZ) {
		if (WorldSettingsLowPoly.LOD_TOTAL > 1) {
			int LOD_SCOPE = 3;
			for (int x = -LOD_SCOPE; x <= LOD_SCOPE; x++) {
				for (int z = -LOD_SCOPE; z <= LOD_SCOPE; z++) {
					int LOD = WorldSettingsLowPoly.LOD_TOTAL / 2;
					if (Math.abs(x) < LOD_SCOPE && Math.abs(z) < LOD_SCOPE) {
						LOD = WorldSettingsLowPoly.LOD_TOTAL / 4;
					}
					increaseTerrainLOD(x + indX, z + indZ, LOD);
				}
			}
		}
	}

	private void increaseTerrainLOD(int indX, int indZ, int LOD) {
		float indexLOD = (LOD - 1) * 0.5f;
		if (infiniteTerrainManager.terrainChunkExists(indX, indZ)) {
			List<Entity> terrainEntities = new ArrayList<Entity>();
			InfiniteTerrainChunk currentTerrainChunk = infiniteTerrainManager.getTerrainChunk(indX, indZ);
			if (currentTerrainChunk != null) {
				terrainEntities = currentTerrainChunk.getEntities();
				terrains.remove(currentTerrainChunk.getTerrain());
				infiniteTerrainManager.removeTerrainChunk(currentTerrainChunk);				
			}
			TerrainLowPoly terrainLowPoly = terrainGenerator.generateTerrain(
					indX * WorldSettingsLowPoly.WORLD_SIZE,
					indZ * WorldSettingsLowPoly.WORLD_SIZE,
					WorldSettingsLowPoly.WORLD_SIZE, terrainScale, LOD);
			terrainLowPoly.setX((int) ((indX + indexLOD) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));
			terrainLowPoly.setZ((int) ((indZ + indexLOD) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));
			processTerrain(terrainLowPoly);
			InfiniteTerrainChunk newTerrainChunk = new InfiniteTerrainChunk(terrainLowPoly, indX, indZ, terrainEntities);
			infiniteTerrainManager.addTerrainChunk(newTerrainChunk);			
		}
	}

	private void generateTerrainChunks(int indX, int indZ) {
		// add new
		int LOD = WorldSettingsLowPoly.LOD_TOTAL;
		float indexLOD = (LOD - 1) * 0.5f;
		for (int x = -gridSize / 2; x <= gridSize / 2; x++) {
			for (int z = -gridSize / 2; z <= gridSize / 2; z++) {
				if (!infiniteTerrainManager.terrainChunkExists(x + indX, z + indZ)) {

					TerrainLowPoly terrainLowPoly = terrainGenerator.generateTerrain(
							(x + indX) * WorldSettingsLowPoly.WORLD_SIZE,
							(z + indZ) * WorldSettingsLowPoly.WORLD_SIZE,
							WorldSettingsLowPoly.WORLD_SIZE, terrainScale, LOD);
					terrainLowPoly.setX((int) ((x + indX + indexLOD) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));
					terrainLowPoly.setZ((int) ((z + indZ + indexLOD) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));
					processTerrain(terrainLowPoly);

					terrainHiFi = terrainGenerator.generateTerrain(
							(x + indX) * WorldSettingsLowPoly.WORLD_SIZE,
							(z + indZ) * WorldSettingsLowPoly.WORLD_SIZE,
							WorldSettingsLowPoly.WORLD_SIZE, terrainScale, 1);
					terrainHiFi.setX((int) ((x + indX) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));
					terrainHiFi.setZ((int) ((z + indZ) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));

					InfiniteTerrainChunk newTerrainChunk = new InfiniteTerrainChunk(terrainLowPoly, x + indX, z + indZ);
					populateTerrainChunk(newTerrainChunk, terrainHiFi);
					infiniteTerrainManager.addTerrainChunk(newTerrainChunk);
				}
			}
		}
		// remove old
		List<InfiniteTerrainChunk> remoteTerrainChunks = infiniteTerrainManager.getRemoteTerrainChunks(indX, indZ);		
		for (InfiniteTerrainChunk remoteTerrainChunk : remoteTerrainChunks) {
			List<Entity> remoteEntities = remoteTerrainChunk.getEntities();
			for (Entity remoteEntity : remoteEntities) {
				removeEntity(remoteEntity);				
			}
			terrains.remove(remoteTerrainChunk.getTerrain());
			infiniteTerrainManager.removeTerrainChunk(remoteTerrainChunk);
			remoteTerrainChunk.cleanUp();
		}
	}

	private void generateWaterChunks(int indX, int indZ) {
		// add new
		for (int x = -gridSize / 2; x <= gridSize / 2; x++) {
			for (int z = -gridSize / 2; z <= gridSize / 2; z++) {
				if (!infiniteTerrainManager.waterChunkExists(x + indX, z + indZ)) {
					WaterTileLowPoly waterLowPoly = WaterGenerator.generate(WorldSettingsLowPoly.WORLD_SIZE, WorldSettingsLowPoly.WATER_HEIGHT, terrainScale);
					waterLowPoly.setX((int) ((x + indX) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));
					waterLowPoly.setZ((int) ((z + indZ) * WorldSettingsLowPoly.WORLD_SIZE * terrainScale));
					this.processWaterTile(waterLowPoly);
					infiniteTerrainManager.addWaterChunk(new InfiniteWaterChunk(waterLowPoly, x + indX, z + indZ));
				}
			}
		}
		// remove old
		List<InfiniteWaterChunk> remoteWaterChunks = infiniteTerrainManager.getRemoteWaterChunks(indX, indZ);
		Iterator<InfiniteWaterChunk> iterator = remoteWaterChunks.iterator();
		while (iterator.hasNext()) {
			InfiniteWaterChunk waterChunk = iterator.next();
			waterTiles.remove(waterChunk.getWaterTile());
			infiniteTerrainManager.removeWaterChunk(waterChunk);
		}
	}

	private void populateTerrainChunk(InfiniteTerrainChunk terrainChunk, ITerrain terrainHiFi) {
		float terrainX = terrainChunk.getIndexX() * WorldSettingsLowPoly.WORLD_SIZE * terrainScale;
		float terrainZ = terrainChunk.getIndexZ() * WorldSettingsLowPoly.WORLD_SIZE * terrainScale;
		int modelsSpawnedMax = WorldSettingsLowPoly.WORLD_SIZE / 4;
		int modelsSpawned = 0;

		while (modelsSpawned < modelsSpawnedMax) {
			float coordX = terrainX + rand.nextFloat() * WorldSettingsLowPoly.WORLD_SIZE * terrainScale - WorldSettingsLowPoly.WORLD_SIZE / 2 * terrainScale;
			float coordZ = terrainZ + rand.nextFloat() * WorldSettingsLowPoly.WORLD_SIZE * terrainScale - WorldSettingsLowPoly.WORLD_SIZE / 2 * terrainScale;
			float coordY = terrainHiFi.getHeightOfTerrain(coordX, coordZ);
			if (coordY > 0 && coordY > WorldSettingsLowPoly.WATER_HEIGHT + waterLevelOffset + 5) {
				int modelIndex = rand.nextInt(2);
				float modelSize = rand.nextFloat() + 1;
				int fernTxIndex = rand.nextInt(4);

				Entity entity = null;
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
					terrainChunk.addEntity(entity);
				}
			}
			modelsSpawned++;
		}
	}

	private void setupAnimatedPlayer() {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(new MyFile(WorldSettingsLowPoly.MODELS_DIR + "/cowboy.dae"),
			WorldSettingsLowPoly.MAX_WEIGHTS);
		Vao model = AnimatedModelLoader.createVao(entityData.getMeshData());
		Texture texture = AnimatedModelLoader.loadTexture(new MyFile(WorldSettingsLowPoly.TEXTURES_DIR + "/cowboy.png"));
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = AnimatedModelLoader.createJoints(skeletonData.headJoint);
		int coordX = 0;
		int coordZ = 0;
		ITerrain currentTerrain = getCurrentTerrain(coordX, coordZ);
		float coordY = currentTerrain != null ? currentTerrain.getHeightOfTerrain(coordX, coordZ) : WorldSettingsLowPoly.WATER_HEIGHT;
		player = new AnimatedPlayer(model, texture, headJoint, skeletonData.jointCount, new Vector3f(coordX, coordY, coordZ), 0, 0, 0, 1.0f);
		Animation animation = AnimationLoader.loadAnimation(new MyFile(WorldSettingsLowPoly.MODELS_DIR + "/cowboy.dae"));
		((AnimatedModel) player).doAnimation(animation);
	}

	private void setupCamera() {
		((Camera) camera).setDistanceFromPlayer(30).setOffsetY(15f).setPitch(0);
	}

	private void setupLensFlare() {
		// loading textures for lens flare
		Texture texture1 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex1.png")).normalMipMap().create();
		Texture texture2 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex2.png")).normalMipMap().create();
		Texture texture3 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex3.png")).normalMipMap().create();
		Texture texture4 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex4.png")).normalMipMap().create();
		Texture texture5 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex5.png")).normalMipMap().create();
		Texture texture6 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex6.png")).normalMipMap().create();
		Texture texture7 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex7.png")).normalMipMap().create();
		Texture texture8 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex8.png")).normalMipMap().create();
		Texture texture9 = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/tex9.png")).normalMipMap().create();
		Texture textureSun = Texture.newTexture(new MyFile(WorldSettingsLowPoly.LENS_FLARE_DIR + "/sun.png")).normalMipMap().create();

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
		sun.setDirection(WorldSettingsLowPoly.LIGHT_DIR);
		lightDirectional = new LightDirectional(WorldSettingsLowPoly.LIGHT_DIR, WorldSettingsLowPoly.LIGHT_COL, WorldSettingsLowPoly.LIGHT_BIAS);
	}

	private void setupParticles() {
		fireMode = true;
		particleTexture = new ParticleTexture(loader.loadTexture(WorldSettingsLowPoly.TEXTURES_DIR + "/particles/particleAtlas.png"), 4, true);
		particleSystemShoot = new ParticleSystemShoot(particleTexture, 800f, 20f, 0.0f, 0.5f, fireMaster);
	}

	private void updateParticles(Input input) {
		if (input.isKeyReleased(GLFW.GLFW_KEY_F)) {
			fireMode = !fireMode;
			if (fireMode) {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 800f, 20f, 0.0f, 0.5f, fireMaster);
			} else {
				particleSystemShoot = new ParticleSystemShoot(particleTexture, 20f, 50f, -0.25f, 2f, fireMaster); // magic circle around the player
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
	}

	private void setupLights() {
		Light light_sun = new Light(new Vector3f(-5000, 8000, -5000), new Vector3f(1, 1, 1));
		lights.add(light_sun);
	}

	private void setupGui() {
		float coordY = 0.88f;
		float offsetY = -0.22f;
		GuiTexture reflection  = new GuiTexture(MasterRendererLowPoly.reflectionFbo.getColorBuffer(0), new Vector2f(-0.89f, coordY), new Vector2f(0.1f, 0.1f));
		GuiTexture refraction  = new GuiTexture(MasterRendererLowPoly.refractionFbo.getColorBuffer(0), new Vector2f(-0.89f, coordY += offsetY), new Vector2f(0.1f, 0.1f));
		GuiTexture depth       = new GuiTexture(MasterRendererLowPoly.refractionFbo.getDepthBuffer(),  new Vector2f(-0.89f, coordY += offsetY), new Vector2f(0.1f, 0.1f));
		GuiTexture shadowMap   = new GuiTexture(MasterRendererLowPoly.getShadowMapTexture(),           new Vector2f(-0.89f, coordY += offsetY), new Vector2f(0.1f, 0.1f));
		GuiTexture aimTarget   = new GuiTexture(loader.loadTexture(WorldSettingsLowPoly.TEXTURES_DIR + "/gui/bullseye.png"), new Vector2f(0.0f, 0.0f), new Vector2f(0.02f, 0.036f));
		processGui(reflection);
		processGui(refraction);
		processGui(depth);
		processGui(shadowMap);
		processGui(aimTarget);
	}

	private void updateWaterLevel(Input input) {
		if (input.isKeyDown(GLFW.GLFW_KEY_PAGE_UP)) {
			waterLevelOffset += 0.1f;
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_PAGE_DOWN)) {
			waterLevelOffset -= 0.1f;
		}
	}

	public static float getWaterLevelOffset() {
		return waterLevelOffset;
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
		font_1 = new FontType(loader.loadTexture(WorldSettingsLowPoly.TEXTURES_DIR + "/arial.png"), WorldSettingsLowPoly.FONTS_DIR + "/arial.fnt");
		lines = new String[10];
		guiTexts = new GUIText[10];
		float coordX = 0.005f;
		float coordY = 0.79f;
		float offsetY = 0.025f;
		Vector3f color = new Vector3f(1, 1, 1);
		guiTexts[0] = new GUIText(lines[0], 0.8f, font_1, new Vector2f(coordX, coordY), 1f, false).setColor(color);
		guiTexts[1] = new GUIText(lines[1], 0.8f, font_1, new Vector2f(coordX, coordY += offsetY), 1f, false).setColor(color);
		guiTexts[2] = new GUIText(lines[2], 0.8f, font_1, new Vector2f(coordX, coordY += offsetY), 1f, false).setColor(color);
		guiTexts[3] = new GUIText(lines[3], 0.8f, font_1, new Vector2f(coordX, coordY += offsetY), 1f, false).setColor(color);
		guiTexts[4] = new GUIText(lines[4], 0.8f, font_1, new Vector2f(coordX, coordY += offsetY), 1f, false).setColor(color);
		guiTexts[5] = new GUIText(lines[5], 0.8f, font_1, new Vector2f(coordX, coordY += offsetY), 1f, false).setColor(color);
		guiTexts[6] = new GUIText(lines[6], 0.8f, font_1, new Vector2f(coordX, coordY += offsetY), 1f, false).setColor(color);
		guiTexts[7] = new GUIText(lines[7], 0.8f, font_1, new Vector2f(coordX, coordY += offsetY), 1f, false).setColor(color);
	}

	private void updateText(float interval) {

		textMaster.removeText(guiTexts[0]);
		textMaster.removeText(guiTexts[1]);
		textMaster.removeText(guiTexts[2]);
		textMaster.removeText(guiTexts[3]);
		textMaster.removeText(guiTexts[4]);
		textMaster.removeText(guiTexts[5]);
		textMaster.removeText(guiTexts[6]);
		textMaster.removeText(guiTexts[7]);
		textMaster.prepare();

		Camera camera = (Camera) this.camera;
		lines[0] = "FPS: " + GameEngine.getFPS() + " / " + GameEngine.TARGET_FPS;
		lines[1] = "Player position:  " + (int) player.getPosition().x + "  " + (int) player.getPosition().y + "  " + (int) player.getPosition().z;
		lines[2] = "Player rotation:  " + (int) player.getRotX() + "  " + Maths.angleTo360Range((int) player.getRotY()) + "  " + (int) player.getRotZ();
		lines[3] = "Camera position:  " + (int) camera.getPosition().x + "  " + (int) camera.getPosition().y + "  " + (int) camera.getPosition().z;
		lines[4] = "Camera rotation  [ pitch: " + (int) camera.getRotation().x + "  yaw: " + Maths.angleTo360Range((int) camera.getRotation().y) + "  roll: " + (int) camera.getRotation().z + " ]";
		lines[5] = "Entities spawned:  " + getEntitiesCount();
		lines[6] = "Particles active:  " + ParticleMaster.getParticlesCount();
		lines[7] = "Visible terrain chunks: " + infiniteTerrainManager.getVisibleTerrainChunks().size();

		guiTexts[0].setTextString(lines[0]);
		guiTexts[1].setTextString(lines[1]);
		guiTexts[2].setTextString(lines[2]);
		guiTexts[3].setTextString(lines[3]);
		guiTexts[4].setTextString(lines[4]);
		guiTexts[5].setTextString(lines[5]);
		guiTexts[6].setTextString(lines[6]);
		guiTexts[7].setTextString(lines[7]);

		textMaster.loadText(guiTexts[0]);
		textMaster.loadText(guiTexts[1]);
		textMaster.loadText(guiTexts[2]);
		textMaster.loadText(guiTexts[3]);
		textMaster.loadText(guiTexts[4]);
		textMaster.loadText(guiTexts[5]);
		textMaster.loadText(guiTexts[6]);
		textMaster.loadText(guiTexts[7]);
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

	public ISkybox getSkybox() {
		return skybox;
	}

	public Water getWater() {
		return water;
	}

	public TextMaster getTextMaster() {
		return textMaster;
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

	public ITerrain getCurrentTerrain(float worldX, float worldZ) {
		ITerrain currentTerrain = null;
		for (ITerrain terrain : terrains) {			
			if (worldX >= (terrain.getX() - WorldSettingsLowPoly.WORLD_SIZE / 2 * terrainScale) && worldX < (terrain.getX() + WorldSettingsLowPoly.WORLD_SIZE / 2 * terrainScale) &&
				worldZ >= (terrain.getZ() - WorldSettingsLowPoly.WORLD_SIZE / 2 * terrainScale) && worldZ < (terrain.getZ() + WorldSettingsLowPoly.WORLD_SIZE / 2 * terrainScale)) {
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
		textMaster.cleanUp();
		masterRenderer.cleanUp();
	}

	public Vector3f getLightDirection() {
		return lightDirection;
	}
}
