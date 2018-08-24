package engine.tm2;

import org.joml.Vector3f;
import engine.IGameLogic;
import engine.IScene;
import engine.SceneLight;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.particles.IParticleEmitter;
import engine.tm2.generators.ColorGenerator;
import engine.tm2.generators.PerlinNoise;
import engine.tm2.lens.FlareFactory;
import engine.tm2.lens.FlareManager;
import engine.tm2.settings.WorldSettings;
import engine.tm2.skybox.ThinMatrixSkyBox;
import engine.tm2.sun.Sun;
import engine.tm2.terrains.HybridTerrainGenerator;
import engine.tm2.terrains.ITerrain;
import engine.tm2.terrains.TerrainGenerator;
import engine.tm2.textures.Texture;
import engine.tm2.utils.Light;
import engine.tm2.water.WaterGenerator;
import engine.tm2.water.WaterMesh;

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

	public void init(Window window, ICamera camera) {
		this.camera = (ThinMatrixCamera) camera;

		// init skybox
		String[] textureFiles = ThinMatrixSkyBox.getSkyboxTexFiles(WorldSettings.RESOURCES_SUBDIR + "/skybox/02");		
		Texture cubeMap = Texture.newCubeMap(textureFiles);
		skyBox = new ThinMatrixSkyBox(cubeMap);

		// initialize sun and lens flare and set sun direction
		light = new Light(WorldSettings.LIGHT_DIR, WorldSettings.LIGHT_COL, WorldSettings.LIGHT_BIAS);
		lensFlare = FlareFactory.createLensFlare(window);
		Texture sunTexture = Texture.newTexture(WorldSettings.RESOURCES_SUBDIR + "/lensFlare/sun.png").normalMipMap().create();
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
		this.camera.move();
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
