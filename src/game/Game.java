package game;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;
import config.Config;
import de.matthiasmann.twl.utils.PNGDecoder;
import engine.IGameLogic;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
import engine.graph.Camera;
import engine.graph.HeightMapMesh;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.MouseInput;
import engine.graph.Renderer;
import engine.graph.Texture;
import engine.graph.lights.DirectionalLight;
import engine.graph.particles.FlowParticleEmitter;
import engine.graph.particles.Particle;
import engine.graph.weather.Fog;
import engine.gui.GuiButton;
import engine.gui.GuiManager;
import engine.items.GameItem;
import engine.items.SkyBox;
import engine.loaders.obj.OBJLoader;
import engine.sound.SoundBuffer;
import engine.sound.SoundListener;
import engine.sound.SoundManager;
import engine.sound.SoundSource;

public class Game implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private Scene scene;
    private static final float CAMERA_POS_STEP = 0.1f;
    private float angleInc;
    private float lightAngle;
    private FlowParticleEmitter particleEmitter;
    private final SoundManager soundMgr;

    private static float GRAVITY = -2.0f;
    private static float actualGravity = 0;
    private static final float WORLD_BOTTOM = -20f;
    private static float SPEED;
    private static boolean gravityOn = true;
    private CameraBoxSelectionDetector selectDetectorCamera;
    private Window window;
    private boolean firstTime;
    private boolean sceneChanged;
    private boolean inventoryOn = false;
    private GuiButton nextBlock;

    private boolean updateEnabled = true;
    private long toggleGuiLastTime;
    private GuiManager guiManager;
    private List<GuiButton> guiItems = new ArrayList<GuiButton>();

    private enum Sounds {
        FIRE,
        BACKGROUND,
    };

    private HashMap<String, Mesh> meshTypesMap= new HashMap<String, Mesh>();

    public Game() {
        renderer = new Renderer();
        camera = new Camera();
        soundMgr = new SoundManager();
        guiManager = new GuiManager();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 45;
    }

    @Override
    public void init(Window win) throws Exception {

    	window = win;
        scene = new Scene();
        renderer.init(window, scene);

        /*
        Mesh meshCustom = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/minecraft_sword.obj");
        Texture textureCustom = new Texture(Config.RESOURCES_DIR +  "/textures/minecraft_sword.png");
        Material materialCustom = new Material(textureCustom);
        materialCustom.setReflectance(1.0f);
        materialCustom.setTransparency(1.0f);
        meshCustom.setMaterial(materialCustom);
        GameItem gameItemCustom = new GameItem(meshCustom);
        gameItemCustom.setRotation(0.5f, 0.5f, 0.5f);
        gameItemCustom.setPosition(-100, 30f, 50);
        gameItemCustom.setScale(10f);
        gameItems.add(gameItemCustom);
        */

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
        scene.load(meshTypesMap);

        // Particles
        int maxParticles = 200;
        Vector3f particleSpeed = new Vector3f(0, 1, 0);
        particleSpeed.mul(2.5f);
        long ttl = 4000;
        long creationPeriodMillis = 300;
        float range = 0.2f;
        float scale = 1.0f;
        Mesh partMesh = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/particle.obj", maxParticles);
        Texture particleTexture = new Texture(Config.RESOURCES_DIR + "/textures/particle_anim.png", 4, 4);
        Material partMaterial = new Material(particleTexture);
        partMaterial.setReflectance(1.0f);
        partMesh.setMaterial(partMaterial);
        Particle particle = new Particle(partMesh, particleSpeed, ttl, 100);
        particle.setScale(scale);
        particleEmitter = new FlowParticleEmitter(particle, maxParticles, creationPeriodMillis);
        particleEmitter.setActive(true);
        particleEmitter.setPositionRndRange(range);
        particleEmitter.setSpeedRndRange(range);
        particleEmitter.setAnimRange(10);
        this.scene.setParticleEmitters(new FlowParticleEmitter[]{particleEmitter});

        // Shadows
        scene.setRenderShadows(false);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        scene.setFog(new Fog(true, fogColour, 0.01f));

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox(Config.RESOURCES_DIR + "/models/skybox.obj", Config.RESOURCES_DIR + "/textures/skybox_minecraft.png");

        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();        

        // Setup Sounds
        setupSounds();
        
        // Setup GUI
        setupGui();

        camera.getPosition().x = -skyBoxScale;
        camera.getPosition().y = 20.0f;
        camera.getPosition().z = skyBoxScale;
        camera.setRotation(0, 0, 0);

        selectDetectorCamera = new CameraBoxSelectionDetector();
    }

    private void setupGui() throws Exception {

    	// bullseye
    	Texture textureBullseye = new Texture(Config.RESOURCES_DIR +  "/textures/bullseye.png");
    	GuiButton guiBullseye = new GuiButton(textureBullseye, new Vector3f(0f, 0f, 1), new Vector2f(0.026f, 0.04f));
    	guiItems.add(guiBullseye);

    	// inventory
    	Texture textureBtnStairs = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_stairs.png");
    	GuiButton guiButtonStairs = new GuiButton(textureBtnStairs, new Vector3f(-0.21f, 0.56f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonStairs.setInventory(true);
    	guiButtonStairs.setMesh(meshTypesMap.get("STAIRS"));
    	guiItems.add(guiButtonStairs);

    	Texture textureBtnStairsCorner = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_stairs_corner.png");
    	GuiButton guiButtonStairsCorner = new GuiButton(textureBtnStairsCorner, new Vector3f(0.0f, 0.56f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonStairsCorner.setInventory(true);
    	guiButtonStairsCorner.setMesh(meshTypesMap.get("STAIRS_CORNER"));
    	guiItems.add(guiButtonStairsCorner);

    	Texture textureBtnStairsCornerInner = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_stairs_corner_inner.png");
    	GuiButton guiButtonStairsCornerInner = new GuiButton(textureBtnStairsCornerInner, new Vector3f(0.21f, 0.56f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonStairsCornerInner.setInventory(true);
    	guiButtonStairsCornerInner.setMesh(meshTypesMap.get("STAIRS_CORNER_INNER"));
    	guiItems.add(guiButtonStairsCornerInner);

    	Texture textureBtnOakWood = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_oakwood.png");
    	GuiButton guiButtonOakWood = new GuiButton(textureBtnOakWood, new Vector3f(-0.21f, 0.18f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonOakWood.setInventory(true);
    	guiButtonOakWood.setMesh(meshTypesMap.get("OAKWOOD"));
    	guiItems.add(guiButtonOakWood);

    	Texture textureBtnWood = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_wood.png");
    	GuiButton guiButtonWood = new GuiButton(textureBtnWood, new Vector3f(0.0f, 0.18f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonWood.setInventory(true);
    	guiButtonWood.setMesh(meshTypesMap.get("WOOD"));
    	guiItems.add(guiButtonWood);

    	Texture textureBtnCobble = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_cobble.png");
    	GuiButton guiButtonCobble = new GuiButton(textureBtnCobble, new Vector3f(0.21f, 0.18f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonCobble.setInventory(true);
    	guiButtonCobble.setMesh(meshTypesMap.get("COBBLE"));
    	guiItems.add(guiButtonCobble);

    	Texture textureBtnGrass = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_grass.png");
    	GuiButton guiButtonGrass = new GuiButton(textureBtnGrass, new Vector3f(-0.21f, -0.2f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonGrass.setInventory(true);
    	guiButtonGrass.setMesh(meshTypesMap.get("GRASS"));
    	guiItems.add(guiButtonGrass);

    	Texture textureBtnGround = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_ground.png");
    	GuiButton guiButtonGround = new GuiButton(textureBtnGround, new Vector3f(0f, -0.2f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonGround.setInventory(true);
    	guiButtonGround.setMesh(meshTypesMap.get("GROUND"));
    	guiItems.add(guiButtonGround);

    	Texture textureBtnWater = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_water.png");
    	GuiButton guiButtonWater = new GuiButton(textureBtnWater, new Vector3f(0.21f, -0.2f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonWater.setInventory(true);
    	guiButtonWater.setMesh(meshTypesMap.get("WATER"));
    	guiItems.add(guiButtonWater);

    	Texture textureBtnGlass = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_glass.png");
    	GuiButton guiButtonGlass = new GuiButton(textureBtnGlass, new Vector3f(-0.21f, -0.58f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonGlass.setInventory(true);
    	guiButtonGlass.setMesh(meshTypesMap.get("GLASS"));
    	guiItems.add(guiButtonGlass);

    	Texture textureBtnTreetop = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_treetop.png");
    	GuiButton guiButtonTreetop = new GuiButton(textureBtnTreetop, new Vector3f(0.21f, -0.58f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonTreetop.setInventory(true);
    	guiButtonTreetop.setMesh(meshTypesMap.get("TREETOP"));
    	guiItems.add(guiButtonTreetop);

    	Texture textureBtnLava = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_lava.png");
    	GuiButton guiButtonLava = new GuiButton(textureBtnLava, new Vector3f(0.0f, -0.58f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonLava.setInventory(true);
    	guiButtonLava.setMesh(meshTypesMap.get("LAVA"));
    	guiItems.add(guiButtonLava);
	}

	private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

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

    private void setupSounds() throws Exception {

        this.soundMgr.init();
        this.soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);

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

    @Override
    public void input(Window window, MouseInput mouseInput) {
    	sceneChanged = false;
        cameraInc.set(0, 0, 0);
        
        if (inventoryOn) {
        	Vector2f mouseNDC = guiManager.getNormalisedDeviceCoordinates(
	        		(float) mouseInput.getMousePosition().x,
	        		(float) mouseInput.getMousePosition().y, window);
        		for (GuiButton gb : guiItems) {
        			gb.setMouseOver(false);
        		}
	        	nextBlock = guiManager.selectGuiItem(mouseNDC, guiItems);
	        	if (nextBlock instanceof GuiButton) {
	        		nextBlock.setMouseOver(true);
	        	}
	        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
	        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
	        	toggleGui();
	        }
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_E)) {
        	sceneChanged = true;
        	toggleGui();
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_K)) {
        	scene.save();
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_L)) {
        	scene.load(meshTypesMap);
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_C)) {
        	try {
				scene.generateTerrain(meshTypesMap);
			} catch (Exception e) {
				System.err.println("Failed to generate terrain.");
				e.printStackTrace();
			}
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_G)) {
        	actualGravity = (actualGravity == 0.0f) ? GRAVITY : 0.0f;
        }

        // reset camera position/rotation
        if (window.isKeyPressed(GLFW.GLFW_KEY_R)) {
        	sceneChanged = true;
            camera.reset();
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_CAPS_LOCK)) {
        	SPEED = 1;
        } else {
        	SPEED = 5;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
        	sceneChanged = true;
            cameraInc.z = -SPEED;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
        	sceneChanged = true;
            cameraInc.z = SPEED;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
        	sceneChanged = true;
            cameraInc.x = -SPEED;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
        	sceneChanged = true;
            cameraInc.x = SPEED;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
        	sceneChanged = true;
            cameraInc.y = SPEED;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
        	sceneChanged = true;
            cameraInc.y = -SPEED;
        } else if (gravityOn && camera.getPosition().y > WORLD_BOTTOM) {
        	sceneChanged = true;
        	cameraInc.y = actualGravity;
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
        	sceneChanged = true;
            angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
        	sceneChanged = true;
            angleInc += 0.05f;
        } else {
            angleInc = 0;
        }
    }

    private void toggleGui() {
    	long currentTime = System.currentTimeMillis();
    	if (currentTime - toggleGuiLastTime < 100) {
    		return;
    	}
    	toggleGuiLastTime = currentTime;
    	if (!inventoryOn) {
    		inventoryOn = true;
    		updateEnabled = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		inventoryOn = false;
    		updateEnabled = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}

	@Override
    public void update(float interval, MouseInput mouseInput) {
		if (updateEnabled) {
			updateConditional(mouseInput);
		}
    }
	
	public void updateConditional(MouseInput mouseInput) {
    	// Update camera based on mouse
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        
        Vector3f camPos = camera.getPosition();

        // Update camera position
        Vector3f newCamPos = camera.calculateNewPosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

		// Check if there has been a collision
        // newPosCameraBase - the camera imaginary "tripod base" we use to check the collision. It's bellow the camera "lens"
        // if (!newCamPos.equals(camPos)) { // disabled, because of problems with disabled gravity when camera is close to block
    	if (scene.inCollision(newCamPos, true)) {
    		gravityOn = false;
    	} else {
    		camera.movePosition(newCamPos);
    		gravityOn = true;
    	}        	

        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();

        // Update sound listener position;
        soundMgr.updateListenerPosition(camera);

        // particleEmitter.update((long) (interval * 1000));

        // disable editing while inventory GUI is open
        if (!inventoryOn) {
        	Mesh nextMesh = nextBlock instanceof GuiButton ? nextBlock.getMesh() : null;
        	selectDetectorCamera.selectGameItem(scene, camera, mouseInput, nextMesh);
        }

    	// Update view matrix
    	camera.updateViewMatrix();
	}

    @Override
    public void render(Window window) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, sceneChanged);
        renderer.renderGui(guiItems, window, inventoryOn);
    }

    @Override
    public void cleanup() {
    	scene.save();
        renderer.cleanup();
        scene.cleanup();
        soundMgr.cleanup();
    }    
}
