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
import engine.gui.GuiTexture;
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
    private Hud hud;
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
    private List<GuiTexture> guis = new ArrayList<GuiTexture>();
    private boolean guiVisible = false;

    private enum Sounds {
        FIRE,
        BACKGROUND,
    };

    public Game() {
        renderer = new Renderer();
        camera = new Camera();
        hud = new Hud();
        soundMgr = new SoundManager();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 45;
    }

    @Override
    public void init(Window win) throws Exception {

    	window = win;
        renderer.init(window);
        hud.init(window);
        scene = new Scene();

        PNGDecoder decoder = new PNGDecoder(new FileInputStream(Config.RESOURCES_DIR + "/textures/heightmap_64.png"));
        int height = decoder.getHeight();
        int width = decoder.getWidth();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
        decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        float reflectance = 1.0f;

        Mesh meshGrass = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureGrass = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_textures.png", 2, 1);
        Material materialGrass = new Material(textureGrass, reflectance);
        meshGrass.setMaterial(materialGrass);
        meshGrass.setTransparency(1.0f);

        Mesh meshMount = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureMount = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_textures_mountain.png", 2, 1);
        Material materialMount = new Material(textureMount, reflectance);
        meshMount.setMaterial(materialMount);
        meshMount.setTransparency(1.0f);

        Mesh meshWater = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj", 5000);
        Texture textureWater = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_texture_water.png", 2, 1);
        Material materialWater = new Material(textureWater, reflectance);
        meshWater.setMaterial(materialWater);
        meshWater.setTransparency(0.7f);

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
        int terrainAltitude = 16;
        int terrainDepth = 2;
        int waterLevel = 8;
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
            		} else if (posY < mountLevel){
            			gameItem = new GameItem(meshGrass);
            			
            		} else {
            			gameItem = new GameItem(meshMount);
            		}
            		gameItem.setPosition(posX, posY, posZ);
                	gameItem.setScale(blockScale);
                	gameItem.setBoundingBox();
                	// int textPos = Math.random() > 0.5f ? 0 : 1;
                	// gameItem.setTextPos(textPos);
                	
                	if (gameItem.getMesh().getTransparency() < 1.0f) {
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
        Material partMaterial = new Material(particleTexture, reflectance);
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
    	Texture guiQuad = new Texture(Config.RESOURCES_DIR +  "/textures/inventory.png", 1, 1);
    	GuiTexture guiInventory = new GuiTexture(guiQuad.getId(), new Vector3f(0.0f, 0.0f, 1), new Vector2f(0.3f, 0.5f));
    	guis.add(guiInventory);
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
        sourceBackground.setGain(0.5f);
        soundMgr.setListener(new SoundListener(new Vector3f(0, 0, 0)));
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
    	sceneChanged = false;
        cameraInc.set(0, 0, 0);
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
        
        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_I)) {
        	toggleGui();
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
    	if (!guiVisible) {
    		guiVisible = true;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    	} else {
    		guiVisible = false;
    		GLFW.glfwSetInputMode(window.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    	}
	}

	@Override
    public void update(float interval, MouseInput mouseInput) {

    	// Update camera based on mouse
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);        	

        // Update camera position
        Vector3f newPos = camera.calculateNewPosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

		// Check if there has been a collision
        // newPosCameraBase - the camera imaginary "tripod base" we use to check the collision. It's bellow the camera "lens"
		if (camera.inCollision(scene, newPos)) {
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

        selectDetectorCamera.selectGameItem(scene, camera, mouseInput);
        
		// Update view matrix
		camera.updateViewMatrix();
    }

    @Override
    public void render(Window window) {
        if (hud != null) {
            hud.updateSize(window);
        }
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, hud, sceneChanged);
        if (guiVisible) {
        	renderer.renderGui(guis, window);        	
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        soundMgr.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }
}
