package game;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import config.Config;
import engine.GameItem;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import engine.graph.CubeMesh;
import engine.graph.DirectionalLight;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.MouseInput;
import engine.graph.PointLight;
import engine.graph.Renderer;
import engine.graph.Texture;
import engine.items.Box3D;

public class Game implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.5f;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private Hud hud;
    private List<GameItem> gameItems;
    private static final float CAMERA_POS_STEP = 0.05f;                          
    private static final float GRAVITY = -1f;
    private static boolean gravityOn = true;
    private static float SPEED;
    private static final float WORLD_BOTTOM = -10f;

    private Vector3f ambientLight;
    private PointLight pointLight;
    private DirectionalLight directionalLight;
    private float lightAngle;

    private CameraBoxSelectionDetector selectDetectorCamera;
    // private MouseBoxSelectionDetector selectDetectorMouse;

	public Game() {
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
		gameItems = new ArrayList<GameItem>();
		lightAngle = 0;
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);
		
		float reflectance = 1f;

		selectDetectorCamera = new CameraBoxSelectionDetector();
		// selectDetectorMouse = new MouseBoxSelectionDetector();

        Texture texture = new Texture(Config.RESOURCES_DIR + "/textures/grassblock.png");
        Mesh mesh = new Mesh(CubeMesh.positions, CubeMesh.textCoords, CubeMesh.normals, CubeMesh.indices, texture);
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

        int CUBES_X = 20;
        int CUBES_Y = 10;
        int CUBES_Z = 20;

        for(int x = 0; x < CUBES_X; x++) {
            for(int y = 0; y < CUBES_Y; y++) {
            	for(int z = 0; z < CUBES_Z; z++) {
	                GameItem gameItem = new GameItem(mesh);
	                gameItem.setScale(1);
	                gameItem.setPosition(x, y, z);
	                gameItems.add(gameItem);
	                gameItem.setBoundingBox();
            	}
            }
        }
        camera.setPosition(10, 11, 10);
        camera.setRotation(0, 0, 0);

        ambientLight = new Vector3f(0.8f, 0.8f, 0.8f);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 20, 0);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);

        lightPosition = new Vector3f(0, 20, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, lightIntensity);

        // Create HUD
        hud = new Hud("DEMO");

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

        // controlling the light
        float lightPos = pointLight.getPosition().z;
        if (window.isKeyPressed(GLFW.GLFW_KEY_N)) {
            this.pointLight.getPosition().z = lightPos + 0.1f;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_M)) {
            this.pointLight.getPosition().z = lightPos - 0.1f;
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

        directionalLight.setIntensity(1);
        directionalLight.getColor().x = 1;
        directionalLight.getColor().y = 1;
        directionalLight.getColor().z = 1;
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
    	hud.updateSize(window);
    	// renderer.render(window, camera, gameItems, hud);
    	renderer.render(window, camera, gameItems, ambientLight, pointLight, directionalLight);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (GameItem gameItem : gameItems) {
        	gameItem.getMesh().cleanUp();
        }
        if (hud != null) {
            hud.cleanup();
        }
    }
}
