package engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;
import config.Config;
import de.matthiasmann.twl.utils.PNGDecoder;
import engine.graph.Camera;
import engine.graph.HeightMapMesh;
import engine.graph.InstancedMesh;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.graph.lights.DirectionalLight;
import engine.graph.particles.FlowParticleEmitter;
import engine.graph.particles.IParticleEmitter;
import engine.graph.particles.Particle;
import engine.graph.weather.Fog;
import engine.gui.GuiElement;
import engine.gui.GuiManager;
import engine.items.GameItem;
import engine.items.SkyBox;
import engine.loaders.obj.OBJLoader;
import engine.sound.SoundBuffer;
import engine.sound.SoundListener;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import game.Game3D;

public class Scene {

    private final Map<Mesh, List<GameItem>> meshMap;
    private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;
    private SkyBox skyBox;
    private SceneLight sceneLight;
    private Fog fog;
    private boolean renderShadows;
    private IParticleEmitter[] particleEmitters;

    public enum Sounds {
        FIRE,
        BACKGROUND,
    };

    public Scene() {
        meshMap = new HashMap<Mesh, List<GameItem>>();
        instancedMeshMap = new HashMap<InstancedMesh, List<GameItem>>();
        fog = Fog.NOFOG;
        renderShadows = true;
    }

    public void init(HashMap<String, Mesh> meshTypesMap, FlowParticleEmitter particleEmitter, SoundManager soundMgr, Camera camera, GuiManager guiManager, Window window) throws Exception {

        Mesh meshGrass = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("GRASS", meshGrass.setLabel("GRASS"));
        Texture textureGrass = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_grass.png", 2, 1);
        Material materialGrass = new Material(textureGrass);
        materialGrass.setReflectance(1.0f);
        materialGrass.setTransparency(1.0f);
        meshGrass.setMaterial(materialGrass);

        Mesh meshGround = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("GROUND", meshGround.setLabel("GROUND"));
        Texture textureGround = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_ground.png", 2, 1);
        Material materialGround = new Material(textureGround);
        materialGround.setReflectance(1.0f);
        materialGround.setTransparency(1.0f);
        meshGround.setMaterial(materialGround);

        Mesh meshWater = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("WATER", meshWater.setLabel("WATER"));
        Texture textureWater = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_water.png", 2, 1);
        Material materialWater = new Material(textureWater);
        materialWater.setReflectance(1.0f);
        materialWater.setTransparency(0.7f); // 0.7f
        materialWater.setSolid(false);
        meshWater.setMaterial(materialWater);

        Mesh meshLava = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("LAVA", meshLava.setLabel("LAVA"));
        Texture textureLava = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_lava.png", 2, 1);
        Material materialLava = new Material(textureLava);
        materialLava.setReflectance(1.0f);
        materialLava.setTransparency(1.0f);
        meshLava.setMaterial(materialLava);

        Mesh meshWood = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("WOOD", meshWood.setLabel("WOOD"));
        Texture textureWood = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_wood.png", 2, 1);
        Material materialWood = new Material(textureWood);
        materialWood.setReflectance(1.0f);
        materialWood.setTransparency(1.0f);
        meshWood.setMaterial(materialWood);

        Mesh meshTreetop = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("TREETOP", meshTreetop.setLabel("TREETOP"));
        Texture textureTreetop = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_treetop.png", 2, 1);
        Material materialTreetop = new Material(textureTreetop);
        materialTreetop.setReflectance(1.0f);
        materialTreetop.setTransparency(0.8f);
        meshTreetop.setMaterial(materialTreetop);

        Mesh meshOakwood = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("OAKWOOD", meshOakwood.setLabel("OAKWOOD"));
        Texture textureOakwood = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_oakwood.png", 2, 1);
        Material materialOakwood = new Material(textureOakwood);
        materialOakwood.setReflectance(1.0f);
        materialOakwood.setTransparency(1.0f);
        meshOakwood.setMaterial(materialOakwood);

        Mesh meshGlass = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("GLASS", meshGlass.setLabel("GLASS"));
        Texture textureGlass = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_glass.png", 2, 1);
        Material materialGlass = new Material(textureGlass);
        materialGlass.setReflectance(1.0f);
        materialGlass.setTransparency(0.6f);
        meshGlass.setMaterial(materialGlass);

        Mesh meshCobble = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        meshTypesMap.put("COBBLE", meshCobble.setLabel("COBBLE"));
        Texture textureCobble = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_cobble.png", 2, 1);
        Material materialCobble = new Material(textureCobble);
        materialCobble.setReflectance(1.0f);
        materialCobble.setTransparency(1.0f);
        meshCobble.setMaterial(materialCobble);

        Mesh meshStairs = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/stairs.obj", 5000);
        meshTypesMap.put("STAIRS", meshStairs.setLabel("STAIRS"));
        Texture textureStairs = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_stairs.png", 2, 1);
        Material materialStairs = new Material(textureStairs);
        materialStairs.setReflectance(1.0f);
        materialStairs.setTransparency(1.0f);
        meshStairs.setMaterial(materialStairs);
        meshStairs.setSymetric(false);

        Mesh meshStairsCorner = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/stairs_corner.obj", 5000);
        meshTypesMap.put("STAIRS_CORNER", meshStairsCorner.setLabel("STAIRS_CORNER"));
        meshStairsCorner.setMaterial(materialStairs);
        meshStairsCorner.setSymetric(false);
        meshStairsCorner.setCorner(true);

        Mesh meshStairsCornerInner = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/stairs_inner_corner.obj", 5000);
        meshTypesMap.put("STAIRS_CORNER_INNER", meshStairsCornerInner.setLabel("STAIRS_CORNER_INNER"));
        meshStairsCornerInner.setMaterial(materialStairs);
        meshStairsCornerInner.setSymetric(false);
        meshStairsCornerInner.setCorner(true);

        // scene.initMeshMaps(meshTypesMap);

        int skyBoxScale = 150;
        load(meshTypesMap, "snapshot.txt");

        setupParticles(particleEmitter);

        // Shadows
        setRenderShadows(false);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        setFog(new Fog(true, fogColour, 0.01f));

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox(Config.RESOURCES_DIR + "/models/skybox.obj", Config.RESOURCES_DIR + "/textures/skybox_minecraft.png");

        skyBox.setScale(skyBoxScale);
        setSkyBox(skyBox);

        // Setup Lights
        setupLights();        

        // Setup Sounds
        setupSounds(soundMgr, camera);

        // Setup GUI
        setupGui(meshTypesMap, guiManager, window);

        camera.getPosition().x = -skyBoxScale;
        camera.getPosition().y = 20.0f;
        camera.getPosition().z = skyBoxScale;
        camera.setRotation(0, 0, 0);
    }

	private void setupParticles(FlowParticleEmitter particleEmitter) {
        // Particles
        int maxParticles = 200;
        Vector3f particleSpeed = new Vector3f(0, 1, 0);
        particleSpeed.mul(2.5f);
        long ttl = 4000;
        long creationPeriodMillis = 300;
        float range = 0.2f;
        float scale = 1.0f;
        Mesh partMesh = null;
        Texture particleTexture;
        Material partMaterial = null;
		try {
			partMesh = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/particle.obj", maxParticles);
			particleTexture = new Texture(Config.RESOURCES_DIR + "/textures/particle_anim.png", 4, 4);
			partMaterial = new Material(particleTexture);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        partMaterial.setReflectance(1.0f);
        partMesh.setMaterial(partMaterial);
        Particle particle = new Particle(partMesh, particleSpeed, ttl, 100);
        particle.setScale(scale);
        particleEmitter = new FlowParticleEmitter(particle, maxParticles, creationPeriodMillis);
        particleEmitter.setActive(true);
        particleEmitter.setPositionRndRange(range);
        particleEmitter.setSpeedRndRange(range);
        particleEmitter.setAnimRange(10);
        setParticleEmitters(new FlowParticleEmitter[]{particleEmitter});
	}

	private void setupLights() {

        SceneLight sceneLight = new SceneLight();
        setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        directionalLight.setShadowPosMult(10);
        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        sceneLight.setDirectionalLight(directionalLight);
    }

    private void setupSounds(SoundManager soundMgr, Camera camera) throws Exception {

        soundMgr.init();
        soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);

        SoundBuffer buffBackground = new SoundBuffer(Config.RESOURCES_DIR + "/sounds/tomb_raider_01.ogg");
        soundMgr.addSoundBuffer(buffBackground);
        SoundSource sourceBackground = new SoundSource(true, true);
        sourceBackground.setPosition(camera.getPosition());
        sourceBackground.setBuffer(buffBackground.getBufferId());
        soundMgr.addSoundSource(Sounds.BACKGROUND.toString(), sourceBackground);
        sourceBackground.play();
        sourceBackground.setGain(0.3f);
        soundMgr.setListener(new SoundListener(new Vector3f(0, 0, 0)));
    }

    private void setupGui(HashMap<String, Mesh> meshTypesMap, GuiManager guiManager, Window window) throws Exception {
    	// bullseye
    	Texture textureBullseye = new Texture(Config.RESOURCES_DIR +  "/textures/bullseye.png");
    	GuiElement guiBullseye = new GuiElement(textureBullseye, new Vector3f(0f, 0f, 1), new Vector2f(0.026f, 0.04f));
    	guiManager.addGuiElement(guiBullseye);
    	guiManager.init(window, meshTypesMap);
	}

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getGameInstancedMeshes() {
        return instancedMeshMap;
    }

    public boolean isRenderShadows() {
        return renderShadows;
    }

    /**
     * The purpose of this method is to sort meshes by transparency - non-transparent first, transparent last
     * 
     * @param meshTypesMap
     */
    public void initMeshMaps(HashMap<String, Mesh> meshTypesMap) {
    	List<GameItem> gameItems = new ArrayList<GameItem>();
    	GameItem gameItem;
        for (Mesh mesh : meshTypesMap.values()) {
        	gameItem = new GameItem(mesh);
        	gameItems.add(gameItem);
        }
        sortByTransparency(gameItems);
        setGameItems(gameItems);
    }

    public void setGameItemsArray(GameItem[] gameItems) {
        // Create a map of meshes to speed up rendering
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems[i];
            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                boolean instancedMesh = mesh instanceof InstancedMesh;
                List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
                if (list == null) {
                    list = new ArrayList<>();
                    if (instancedMesh) {
                        instancedMeshMap.put((InstancedMesh)mesh, list);
                    } else {
                        meshMap.put(mesh, list);
                    }
                }
                list.add(gameItem);
            }
        }
    }

	public void setGameItems(List<GameItem> gameItems) {
        // Create a map of meshes to speed up rendering
        for (GameItem gameItem : gameItems) {
            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                boolean instancedMesh = mesh instanceof InstancedMesh;
                List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
                if (list == null) {
                    list = new ArrayList<>();
                    if (instancedMesh) {
                        instancedMeshMap.put((InstancedMesh)mesh, list);
                    } else {
                        meshMap.put(mesh, list);
                    }
                }
                list.add(gameItem);
            }
        }
    }

	public List<GameItem> sortByTransparency(List<GameItem> gameItems) {
		Collections.sort(gameItems, new Comparator<GameItem>() {
			@Override
			public int compare(GameItem one, GameItem two) {
				if (one.getMesh().getMaterial().getTransparency() > two.getMesh().getMaterial().getTransparency()) return -1;
				else if (one.getMesh().getMaterial().getTransparency() < two.getMesh().getMaterial().getTransparency()) return 1;
				else return 0;
			}
		});
		return gameItems;
	}

	public void appendGameItem(GameItem gameItem) {
        Mesh mesh = gameItem.getMesh();
        boolean instancedMesh = mesh instanceof InstancedMesh;
        List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
        if (list == null) {
            list = new ArrayList<>();
            if (instancedMesh) {
                instancedMeshMap.put((InstancedMesh)mesh, list);
            } else {
                meshMap.put(mesh, list);
            }
        }
        list.add(gameItem);
    }

	public void removeGameItem(GameItem gameItem) {
		Mesh mesh = gameItem.getMesh();
        boolean instancedMesh = mesh instanceof InstancedMesh;
        List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
        if (list == null) {
        	list = new ArrayList<GameItem>();
        }
        list.remove(gameItem);
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
        for (Mesh mesh : instancedMeshMap.keySet()) {
            mesh.cleanUp();
        }
        if (particleEmitters != null) {
            for (IParticleEmitter particleEmitter : particleEmitters) {
                particleEmitter.cleanup();
            }
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    /**
     * @return the fog
     */
    public Fog getFog() {
        return fog;
    }

    /**
     * @param fog the fog to set
     */
    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        this.particleEmitters = particleEmitters;
    }

	public boolean inCollision(Vector3f newPos, boolean cameraCollision, Camera camera) {
		boolean inCollision = false;
        Map<Mesh, List<GameItem>> mapMeshes = getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
        	if (!cameraCollision || mesh.getMaterial().isSolid()) {
	    		for (GameItem gameItem : mapMeshes.get(mesh)) {
	    			if (gameItem.getBoundingBox().contains(newPos.x, newPos.y, newPos.z, cameraCollision, camera)) {
	    				inCollision = true;
	    				break;
	    			}
	    		}
        	}
        }
        Map<InstancedMesh, List<GameItem>> mapInstancedMeshes = getGameInstancedMeshes();
        for (Mesh mesh : mapInstancedMeshes.keySet()) {
        	if (!cameraCollision || mesh.getMaterial().isSolid()) {
        		for (GameItem gameItem : mapInstancedMeshes.get(mesh)) {
        			if (gameItem.getBoundingBox().contains(newPos.x, newPos.y, newPos.z, cameraCollision, camera)) {
        				inCollision = true;
        				break;
        			}
        		}
        	}
        }
		return inCollision;
	}

	public void save() {

		if (instancedMeshMap.isEmpty()) {
			System.err.println("[Scene::save()] Failed to save the scene, instancedMeshMap is empty.");
			return;
		}

		PrintWriter out;
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd"); // yyyy_MM_dd_HH_mm_ss
			LocalDateTime now = LocalDateTime.now();
			out = new PrintWriter(Config.RESOURCES_DIR + "/saves/snapshot.txt");
			
			for (InstancedMesh mesh : instancedMeshMap.keySet()) {
				String meshLabel = mesh.getLabel();
				for (GameItem gameItem : instancedMeshMap.get(mesh)) {
					out.println(
						meshLabel + "\t" + 
						gameItem.getPosition().x + "\t" +
						gameItem.getPosition().y + "\t" +
						gameItem.getPosition().z + "\t" +
						gameItem.getRotationEulerRadians().x + "\t" +
						gameItem.getRotationEulerRadians().y + "\t" +
						gameItem.getRotationEulerRadians().z + "\t" +
						gameItem.getScale());
				}
			}
			out.close();
			Files.copy(
				new File(Config.RESOURCES_DIR + "/saves/snapshot.txt").toPath(),
				new File(Config.RESOURCES_DIR + "/saves/snapshot_" + dtf.format(now) + ".txt").toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void load(HashMap<String, Mesh> meshTypesMap, String saveFile) {

		String importFilePath = Config.RESOURCES_DIR + "/saves/" + saveFile;
		List<GameItem> gameItems = new ArrayList<GameItem>();
		GameItem gameItem;
		List<String> lines;

		try {
			lines = Utils.readAllLines(importFilePath);
			if (lines.isEmpty()) return;

			instancedMeshMap.clear();
			for (String line : lines) {
				String[] lineParts = line.split("\t");
				if (lineParts.length != 8) {
					System.err.println("[WARNING] Scene::load() Expected number of items in each line is 8. Line content [" + line + "]");
					continue;
				}
    			gameItem = new GameItem(meshTypesMap.get(lineParts[0]));
    			gameItem.setPosition(Float.valueOf(lineParts[1]), Float.valueOf(lineParts[2]), Float.valueOf(lineParts[3]));
    			gameItem.setRotationEulerRadians(Float.valueOf(lineParts[4]), Float.valueOf(lineParts[5]), Float.valueOf(lineParts[6]));
    			gameItem.setScale(Float.valueOf(lineParts[7]));
    			gameItem.setBoundingBox();
    			gameItems.add(gameItem);
    		}
			setGameItems(gameItems);
			gameItems.clear();
		} catch (Exception e) {
			System.err.println("Unable to load the file [" + importFilePath + "]");
			e.printStackTrace();
		}
	}

	public void generateTerrain(HashMap<String, Mesh> meshTypesMap) throws Exception {

		List<GameItem> gameItems = new ArrayList<GameItem>();
		PNGDecoder decoder = new PNGDecoder(new FileInputStream(Config.RESOURCES_DIR + "/textures/heightmap_128.png"));
        int height = decoder.getHeight();
        int width = decoder.getWidth();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
        decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        
        int skyBoxScale = 100;
        int extension = 2;
        int startX = extension * (-skyBoxScale + Game3D.blockScale);
        int startZ = extension * (skyBoxScale - Game3D.blockScale);
        // int startY = -1;
        int increment = Game3D.blockScale * 2;
        int posX = startX;
        int posY = 0;
        int posZ = startZ;
        int topY = 0;
        int terrainAltitude = 20;
        int terrainDepth = 2;
        int waterLevel = 8;
        int grassLevel = 10;
        int lavaLevel = 12;
        // int mountLevel = 14;

        GameItem gameItem;

        this.instancedMeshMap.clear();

        for (int incX = 0; incX < height; incX++) {
            for (int incZ = 0; incZ < width; incZ++) {
            	int rgb = HeightMapMesh.getRGB(incX, incZ, width, buffer);
            	topY = -rgb / (255 / terrainAltitude * 255 * 255);
            	if (topY < waterLevel - terrainDepth - increment) {
            		topY = waterLevel - terrainDepth - increment;
            	}
            	topY = topY - topY % increment;
            	for (int incY = 0; incY < terrainDepth; incY++) {
            		posY = topY + incY * increment;

            		if (posY < waterLevel) {
            			gameItem = new GameItem(meshTypesMap.get("WATER"));
            		} else if (posY <= grassLevel) {
            			gameItem = new GameItem(meshTypesMap.get("GRASS"));
            		} else if (posY <= lavaLevel) {
            			gameItem = new GameItem(meshTypesMap.get("GRASS"));
            		} else {
            			gameItem = new GameItem(meshTypesMap.get("GROUND"));
            		}
            		gameItem.setPosition(posX, posY, posZ);
                	gameItem.setScale(Game3D.blockScale);
                	gameItem.setBoundingBox();
                	
                	gameItems.add(gameItem);                		     		
                }
                posX += increment;
            }
            posX = startX;
            posZ -= increment;
        }

        setGameItems(gameItems);
        gameItems.clear();
	}
}
