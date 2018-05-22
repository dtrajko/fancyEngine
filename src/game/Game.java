package game;

import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import config.Config;
import engine.GameItem;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import engine.graph.CubeMesh;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.MouseInput;
import engine.graph.Renderer;
import engine.graph.Texture;

public class Game implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 2.0f;
    private final Vector3f cameraInc;
    private final Renderer renderer;
    private final Camera camera;
    private Hud hud;
    private GameItem[] gameItems;
    private static final float CAMERA_POS_STEP = 0.05f;
    
    private CameraBoxSelectionDetector selectDetector;

	public Game() {
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);

		selectDetector = new CameraBoxSelectionDetector();

        Texture texture = new Texture(Config.RESOURCES_DIR + "/textures/grassblock.png");
        Mesh mesh = new Mesh(CubeMesh.positions, CubeMesh.textCoords, CubeMesh.indices, texture);
        Material material = new Material(texture, 1.0f);
        mesh.setMaterial(material);
        
        int NUM_ROWS = 40;
        int NUM_COLS = 40;
        int offsetX = -NUM_ROWS / 2;
        int offsetZ = -NUM_COLS / 2;
        int rangeY = 2;
        int posX = 0;
        int posY = 0;
        int posZ = 0;
        Random rand = new Random();

        gameItems  = new GameItem[NUM_ROWS * NUM_COLS];

        for(int i = 0; i < NUM_ROWS; i++) {
            for(int j = 0; j < NUM_COLS; j++) {
                GameItem gameItem = new GameItem(mesh);
                gameItem.setScale(1);
                posX = i + offsetX;
                posZ = j + offsetZ;
                posY = rand.nextInt(rangeY);
                gameItem.setPosition(posX, posY - rangeY, posZ);
                gameItems[i * NUM_COLS + j] = gameItem;
            }
        }

        // Create HUD
        hud = new Hud("DEMO");
	}

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -5;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 5;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -5;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 5;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = 5;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -5;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        // Update camera based on mouse            
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

        // Update HUD compass
        hud.rotateCompass(camera.getRotation().y);

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update view matrix
        camera.updateViewMatrix();
        
        this.selectDetector.selectGameItem(gameItems, camera, mouseInput);
    }

    @Override
    public void render(Window window) {
    	hud.updateSize(window);
    	renderer.render(window, camera, gameItems, hud);
        // renderer.render(window, camera, scene, hud);
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
