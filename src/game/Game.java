package game;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import config.Config;
import de.matthiasmann.twl.utils.PNGDecoder;
import engine.GameItem;
import engine.IGameLogic;
import engine.Scene;
import engine.SceneLight;
import engine.Window;
import engine.graph.Camera;
import engine.graph.CubeMesh;
import engine.graph.HeightMapMesh;
import engine.graph.InstancedMesh;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.MouseInput;
import engine.graph.Renderer;
import engine.graph.Texture;
import engine.graph.lights.DirectionalLight;
import engine.graph.weather.Fog;
import engine.items.SkyBox;
import engine.items.Terrain;

public class Game implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.5f;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private Scene scene;
    private Hud hud;
    private static final float CAMERA_POS_STEP = 0.1f;
    private Terrain terrain;
    private float angleInc;
    private float lightAngle;

    private static final float GRAVITY = -1f;
    private static final float WORLD_BOTTOM = -20f;
    private static float SPEED;
    private static boolean gravityOn = true;
    private CameraBoxSelectionDetector selectDetectorCamera;

    private List<GameItem> gameItems;

	public Game() {
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
		angleInc = 0;
		lightAngle = 45;
		gameItems = new ArrayList<GameItem>();
	}

	@Override
	public void init(Window window) throws Exception {

		renderer.init(window);		
		scene = new Scene();

        float reflectance = 1f;

        int blockScale = 1;
        int skyBoxScale = 10;
        int extension = 2;

        int startX = extension * (-skyBoxScale + blockScale);
        int startZ = extension * (skyBoxScale - blockScale);
        int startY = -1;
        int increment = blockScale * 2;

        int posX = startX;
        int posZ = startZ;
        int topY = 0;
        
        int coordX = 0;
        int coordY = 0;
        int coordZ = 0;

        int terrainHeight = 8;

		PNGDecoder decoder = new PNGDecoder(new FileInputStream(Config.RESOURCES_DIR + "/textures/heightmap_64.png"));
        int height = decoder.getHeight();
        int width = decoder.getWidth();
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * width * height);
        decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        InstancedMesh mesh = new InstancedMesh(CubeMesh.positions, CubeMesh.textCoords, CubeMesh.normals, CubeMesh.indices, 10000);
        Texture texture = new Texture(Config.RESOURCES_DIR + "/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

        for (int incX = 0; incX < height; incX++) {
            for (int incZ = 0; incZ < width; incZ++) {

            	int rgb = HeightMapMesh.getRGB(incX, incZ, width, buffer);
            	topY = -rgb / (255 / terrainHeight * 255 * 255);

                for (int incY = topY; incY < topY + 2; incY++) {

                	GameItem gameItem = new GameItem(mesh);
                	gameItem.setScale(blockScale);

                	coordX = posX / 2;
                	coordY = incY;
                	coordZ = posZ / 2;

                	gameItem.setPosition(coordX, coordY, coordZ);

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

        // Shadows
        scene.setRenderShadows(false);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
        scene.setFog(new Fog(true, fogColour, 0.02f));

        // Setup  SkyBox
        SkyBox skyBox = new SkyBox(Config.RESOURCES_DIR + "/models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        selectDetectorCamera = new CameraBoxSelectionDetector();

        camera.setPosition(0, 5, 0);
        camera.setRotation(0, 0, 0);

        // Create HUD
        hud = new Hud("");

        // Setup  GameItems
        // MD5Model md5Meshodel = MD5Model.parse(Config.RESOURCES_DIR + "/models/monster.md5mesh");
        // MD5AnimModel md5AnimModel = MD5AnimModel.parse(Config.RESOURCES_DIR + "/models/monster.md5anim");        
        // monster = MD5Loader.process(md5Meshodel, md5AnimModel, new Vector4f(1, 1, 1, 1));
        // monster.setScale(0.05f);
        // monster.setRotation(90, 0, 90);
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
    public void update(float interval, MouseInput mouseInput, Window window) {
        // Update camera based on mouse
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        
        // GLFW.glfwSetCursorPos(window.getHandle(), window.getWidth() / 2, window.getHeight() / 2);

        // Update HUD compass
        hud.rotateCompass(camera.getRotation().y);

        // Update camera position
        // Vector3f prevPos = new Vector3f(camera.getPosition());
        Vector3f newPos = camera.calculateNewPosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

		// Check if there has been a collision
        // newPosCameraBase - the camera imaginery "tripod base" we use to check the collision. It's bellow the camera "lens"
		if (camera.inCollision(gameItems, newPos)) {
			gravityOn = false;
			// camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
		} else {
			camera.movePosition(newPos);
			gravityOn = true;
		}

        // Update view matrix
        camera.updateViewMatrix();

        this.selectDetectorCamera.selectGameItem(gameItems, camera, mouseInput);

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
        for (GameItem gameItem : gameItems) {
        	gameItem.getMesh().cleanUp();
        }
        if (hud != null) {
            hud.cleanup();
        }
    }
}
