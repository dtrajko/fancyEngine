package engine.tm;

import org.joml.Vector3f;
import config.Config;
import engine.IGameLogic;
import engine.IScene;
import engine.SceneLight;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.particles.IParticleEmitter;
import engine.tm.generators.ColorGenerator;
import engine.tm.generators.PerlinNoise;
import engine.tm.lens.FlareFactory;
import engine.tm.lens.FlareManager;
import engine.tm.settings.WorldSettings;
import engine.tm.skybox.ThinMatrixSkyBox;
import engine.tm.sun.Sun;
import engine.tm.terrains.HybridTerrainGenerator;
import engine.tm.terrains.ITerrain;
import engine.tm.terrains.TerrainGenerator;
import engine.tm.utils.Light;
import engine.tm.water.WaterGenerator;
import engine.tm.water.WaterMesh;
import engine.tm.textures.Texture;

public class ThinMatrixScene implements IScene {

	private ThinMatrixCamera camera;
	// private final Player animatedPlayer;
	private Vector3f lightDirection = new Vector3f(0, -1, 0);
	private float waterHeight = -0.1f; // should set elsewhere
	private ThinMatrixSkyBox skyBox;
	private ITerrain terrain;
	// private WaterTileAux waterAux;
	private Light light;
	private Sun sun;
	private FlareManager lensFlare;
	private WaterMesh water;
	// private List<ParticleSystemComplex> particleSystems = new ArrayList<ParticleSystemComplex>();

	@Override
	public void init(Window window, ICamera camera) {
		this.camera = (ThinMatrixCamera) camera;

		// init skybox
		String[] textureFiles = ThinMatrixSkyBox.getSkyboxTexFiles(Config.RESOURCES_DIR + "/ThinMatrix/skybox/02");		
		Texture cubeMap = Texture.newCubeMap(textureFiles);
		skyBox = new ThinMatrixSkyBox(cubeMap);

		// initialize sun and lens flare and set sun direction
		light = new Light(WorldSettings.LIGHT_DIR, WorldSettings.LIGHT_COL, WorldSettings.LIGHT_BIAS);
		lensFlare = FlareFactory.createLensFlare(window);
		Texture sunTexture = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/sun.png").normalMipMap().create();
		sun = new Sun(sunTexture, 40, light, lensFlare);
		sun.setDirection(WorldSettings.LIGHT_DIR);
		setLightDirection(sun.getLight().getDirection());
		light = sun.getLight();

		// initialize terrain
		PerlinNoise noise = new PerlinNoise(WorldSettings.OCTAVES, WorldSettings.AMPLITUDE, WorldSettings.ROUGHNESS);
		ColorGenerator colorGen = new ColorGenerator(WorldSettings.TERRAIN_COLS, WorldSettings.COLOUR_SPREAD);		
		TerrainGenerator terrainGenerator = new HybridTerrainGenerator(noise, colorGen);
		terrain = terrainGenerator.generateTerrain(WorldSettings.WORLD_SIZE);
		
		water = WaterGenerator.generate(WorldSettings.WORLD_SIZE, WorldSettings.WATER_HEIGHT);
		
	}

	public void setLightDirection(Vector3f lightDir) {
		this.lightDirection.set(lightDir);
	}

	@Override
	public void update(float interval) {
		camera.move();
	}

	public ITerrain getTerrain() {
		return terrain;
	}

	public ThinMatrixCamera getCamera() {
		return camera;
	}

	public Light getLight() {
		return light;
	}

	public ThinMatrixSkyBox getSkyBox() {
		return skyBox;
	}

	public Sun getSun() {
		return sun;
	}

	public FlareManager getLensFlare() {
		return lensFlare;
	}

	public WaterMesh getWaterMesh() {
		return water;
	}

	public float getWaterHeight() {
		return waterHeight;
	}

	@Override
	public boolean isRenderShadows() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRenderShadows(boolean shadowsEnabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneLight getSceneLight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IParticleEmitter[] getParticleEmitters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		skyBox.delete();
		sun.delete();
	}

	@Override
	public void resetScene(Window window, ICamera camera, IGameLogic game) {
		// TODO Auto-generated method stub
	}
}
