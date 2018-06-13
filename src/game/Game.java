package game;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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

    private static final float GRAVITY = -0.5f;
    private static final float WORLD_BOTTOM = -20f;
    private static float SPEED;
    private static boolean gravityOn = true;
    private CameraBoxSelectionDetector selectDetectorCamera;
    private Window window;
    private boolean firstTime;
    private boolean sceneChanged;
    private boolean inventoryOn = false;
    private GuiButton nextBlock;

    private Mesh meshGrass;
    private Mesh meshGround;
    private Mesh meshWater;
    private Mesh meshLava;
    private Mesh meshWood;
    private Mesh meshOakwood;
    private Mesh meshGlass;
    private Mesh meshCobble;

    private boolean updateEnabled = true;
    private long toggleGuiLastTime;
    private GuiManager guiManager;
    private List<GuiButton> guiItems = new ArrayList<GuiButton>();

    private enum Sounds {
        FIRE,
        BACKGROUND,
    };

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
        renderer.init(window);
        scene = new Scene();

        PNGDecoder decoder = new PNGDecoder(new FileInputStream(Config.RESOURCES_DIR + "/textures/heightmap_128.png"));
        int height = decoder.getHeight();
        int width = decoder.getWidth();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
        decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        meshGrass = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureGrass = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_grass.png", 2, 1);
        Material materialGrass = new Material(textureGrass);
        materialGrass.setReflectance(1.0f);
        materialGrass.setTransparency(1.0f);
        meshGrass.setMaterial(materialGrass);

        meshGround = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureGround = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_ground.png", 2, 1);
        Material materialGround = new Material(textureGround);
        materialGround.setReflectance(1.0f);
        materialGround.setTransparency(1.0f);
        meshGround.setMaterial(materialGround);

        meshWater = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureWater = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_water.png", 2, 1);
        Material materialWater = new Material(textureWater);
        materialWater.setReflectance(1.0f);
        materialWater.setTransparency(0.7f);
        meshWater.setMaterial(materialWater);

        meshLava = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureLava = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_lava.png", 2, 1);
        Material materialLava = new Material(textureLava);
        materialLava.setReflectance(1.0f);
        materialLava.setTransparency(1.0f);
        meshLava.setMaterial(materialLava);

        meshWood = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureWood = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_wood.png", 2, 1);
        Material materialWood = new Material(textureWood);
        materialWood.setReflectance(1.0f);
        materialWood.setTransparency(1.0f);
        meshWood.setMaterial(materialWood);

        meshOakwood = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureOakwood = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_oakwood.png", 2, 1);
        Material materialOakwood = new Material(textureOakwood);
        materialOakwood.setReflectance(1.0f);
        materialOakwood.setTransparency(1.0f);
        meshOakwood.setMaterial(materialOakwood);

        meshGlass = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureGlass = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_glass.png", 2, 1);
        Material materialGlass = new Material(textureGlass);
        materialGlass.setReflectance(1.0f);
        materialGlass.setTransparency(0.4f);
        meshGlass.setMaterial(materialGlass);

        meshCobble = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureCobble = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_cobble.png", 2, 1);
        Material materialCobble = new Material(textureCobble);
        materialCobble.setReflectance(1.0f);
        materialCobble.setTransparency(1.0f);
        meshCobble.setMaterial(materialCobble);

        int blockScale = 1;
        int skyBoxScale = 100;
        int extension = 2;
        int startX = extension * (-skyBoxScale + blockScale);
        int startZ = extension * (skyBoxScale - blockScale);
        // int startY = -1;
        int increment = blockScale * 2;
        int posX = startX;
        int posY = 0;
        int posZ = startZ;
        int topY = 0;
        int terrainAltitude = 20;
        int terrainDepth = 2;
        int waterLevel = 8;
        int grassLevel = 10;
        int lavaLevel = 12;
        int mountLevel = 14;

        GameItem gameItem;
        List<GameItem> gameItems = new ArrayList<GameItem>();
        List<GameItem> gameItemsTransparent = new ArrayList<GameItem>();

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
            			gameItem = new GameItem(meshWater);
            		} else if (posY <= grassLevel) {
            			gameItem = new GameItem(meshGrass);
            		} else if (posY <= lavaLevel) {
            			gameItem = new GameItem(meshGrass); // meshLava
            		} else {
            			gameItem = new GameItem(meshGround);
            		}
            		gameItem.setPosition(posX, posY, posZ);
                	gameItem.setScale(blockScale);
                	gameItem.setBoundingBox();

                	if (gameItem.getMesh().getMaterial().getTransparency() < 1.0f) {
                		gameItemsTransparent.add(gameItem);
                	} else {
                		gameItems.add(gameItem);     		
                	}
                }
                posX += increment;
            }
            posX = startX;
            posZ -= increment;
        }

        scene.setGameItems(gameItems);
        scene.setGameItems(gameItemsTransparent);
        gameItems.clear();
        gameItemsTransparent.clear();

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
        camera.getPosition().y = 10.0f;
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
    	Texture textureBtnGrass = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_grass.png");
    	GuiButton guiButtonGrass = new GuiButton(textureBtnGrass, new Vector3f(-0.21f, 0.0f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonGrass.setInventory(true);
    	guiButtonGrass.setMesh(meshGrass);
    	guiItems.add(guiButtonGrass);

    	Texture textureBtnGround = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_ground.png");
    	GuiButton guiButtonGround = new GuiButton(textureBtnGround, new Vector3f(0f, 0.0f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonGround.setInventory(true);
    	guiButtonGround.setMesh(meshGround);
    	guiItems.add(guiButtonGround);

    	Texture textureBtnWater = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_water.png");
    	GuiButton guiButtonWater = new GuiButton(textureBtnWater, new Vector3f(0.21f, 0.0f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonWater.setInventory(true);
    	guiButtonWater.setMesh(meshWater);
    	guiItems.add(guiButtonWater);

    	Texture textureBtnLava = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_lava.png");
    	GuiButton guiButtonLava = new GuiButton(textureBtnLava, new Vector3f(0.0f, -0.38f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonLava.setInventory(true);
    	guiButtonLava.setMesh(meshLava);
    	guiItems.add(guiButtonLava);

    	Texture textureBtnWood = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_wood.png");
    	GuiButton guiButtonWood = new GuiButton(textureBtnWood, new Vector3f(0.0f, 0.38f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonWood.setInventory(true);
    	guiButtonWood.setMesh(meshWood);
    	guiItems.add(guiButtonWood);
    	
    	Texture textureBtnOakWood = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_oakwood.png");
    	GuiButton guiButtonOakWood = new GuiButton(textureBtnOakWood, new Vector3f(-0.21f, 0.38f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonOakWood.setInventory(true);
    	guiButtonOakWood.setMesh(meshOakwood);
    	guiItems.add(guiButtonOakWood);
    	
    	Texture textureBtnGlass = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_glass.png");
    	GuiButton guiButtonGlass = new GuiButton(textureBtnGlass, new Vector3f(-0.21f, -0.38f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonGlass.setInventory(true);
    	guiButtonGlass.setMesh(meshGlass);
    	guiItems.add(guiButtonGlass);
    	
    	Texture textureBtnCobble = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_cobble.png");
    	GuiButton guiButtonCobble = new GuiButton(textureBtnCobble, new Vector3f(0.21f, 0.38f, 1), new Vector2f(0.1f, 0.18f));
    	guiButtonCobble.setInventory(true);
    	guiButtonCobble.setMesh(meshCobble);
    	guiItems.add(guiButtonCobble);

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
        
        if (mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
        	mouseInput.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
        	Vector2f mouseNDC = guiManager.getNormalisedDeviceCoordinates(
        		(float) mouseInput.getMousePosition().x,
        		(float) mouseInput.getMousePosition().y, window);
        	nextBlock = guiManager.selectGuiItem(mouseNDC, guiItems);
        	if (inventoryOn) {
        		toggleGui();
        	}
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_E)) {
        	sceneChanged = true;
        	toggleGui();
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
        	cameraInc.y = GRAVITY;
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

        // Update camera position
        Vector3f newPos = camera.calculateNewPosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

		// Check if there has been a collision
        // newPosCameraBase - the camera imaginary "tripod base" we use to check the collision. It's bellow the camera "lens"
		if (scene.inCollision(newPos, true)) {
			gravityOn = false;
		} else {
			camera.movePosition(newPos);
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
        renderer.cleanup();
        scene.cleanup();
        soundMgr.cleanup();
    }
}
