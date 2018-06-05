package game;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
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
import engine.graph.OBJLoader;
import engine.graph.Renderer;
import engine.graph.Texture;
import engine.graph.lights.DirectionalLight;
import engine.graph.particles.FlowParticleEmitter;
import engine.graph.particles.Particle;
import engine.graph.weather.Fog;
import engine.items.GameItem;
import engine.items.SkyBox;

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
    
    private static final float GRAVITY = -2f;
    private static final float WORLD_BOTTOM = -20f;
    private static float SPEED;
    private static boolean gravityOn = true;
    private CameraBoxSelectionDetector selectDetectorCamera;
    private Window window;

    public Game() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 45;
    }

    @Override
    public void init(Window win) throws Exception {

    	window = win;
        renderer.init(window);
        scene = new Scene();

        PNGDecoder decoder = new PNGDecoder(new FileInputStream(Config.RESOURCES_DIR + "/textures/heightmap_64.png"));
        int height = decoder.getHeight();
        int width = decoder.getWidth();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
        decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        Mesh mesh = OBJLoader.loadMesh(Config.RESOURCES_DIR + "/models/cube.obj"); // 10000
        Texture texture = new Texture(Config.RESOURCES_DIR +  "/textures/terrain_textures.png", 2, 1);

        float reflectance = 1f;
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

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
        int terrainHeight = 8;
        
        List<GameItem> gameItems = new ArrayList<GameItem>();

        for (int incX = 0; incX < height; incX++) {
            for (int incZ = 0; incZ < width; incZ++) {
            	int rgb = HeightMapMesh.getRGB(incX, incZ, width, buffer);
            	topY = -rgb / (255 / terrainHeight * 255 * 255);
            	topY = topY - topY % increment;
            	for (int incY = 0; incY < 2; incY++) {
            		posY = topY + incY * increment;
                	GameItem gameItem = new GameItem(mesh);
                	gameItem.setScale(blockScale);
                	gameItem.setPosition(posX, posY, posZ);                	
                	gameItem.setBoundingBox();
                	// int textPos = Math.random() > 0.5f ? 0 : 1;
                	// gameItem.setTextPos(textPos);
                	gameItems.add(gameItem);
                }
                posX += increment;
            }
            posX = startX;
            posZ -= increment;
        }

        scene.setGameItems(gameItems);
        gameItems.clear();

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
        // SkyBox skyBox = new SkyBox(Config.RESOURCES_DIR + "/models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        SkyBox skyBox = new SkyBox(Config.RESOURCES_DIR + "/models/skybox.obj", Config.RESOURCES_DIR + "/textures/skybox_minecraft.png");
        
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();        

        camera.getPosition().x = -skyBoxScale;
        camera.getPosition().y = 10.0f;
        camera.getPosition().z = skyBoxScale;
        camera.setRotation(0, 0, 0);

        selectDetectorCamera = new CameraBoxSelectionDetector();

        hud = new Hud(window);
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

    @Override
    public void input(Window window, MouseInput mouseInput) {

        cameraInc.set(0, 0, 0);
        // reset camera position/rotation
        if (window.isKeyPressed(GLFW.GLFW_KEY_R)) {
            camera.reset();
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_CAPS_LOCK)) {
        	SPEED = 1;
        } else {
        	SPEED = 5;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -SPEED;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = SPEED;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -SPEED;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = SPEED;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = SPEED;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -SPEED;
        } else if (gravityOn && camera.getPosition().y > WORLD_BOTTOM) {
        	cameraInc.y = GRAVITY;
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
            angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            angleInc += 0.05f;
        } else {
            angleInc = 0;
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

		// Update view matrix
		camera.updateViewMatrix();

        selectDetectorCamera.selectGameItem(scene, camera, mouseInput);

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

        particleEmitter.update((long) (interval * 1000));
    }

    @Override
    public void render(Window window) {
        if (hud != null) {
            hud.updateSize(window);
        }
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }
}
