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
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.MouseInput;
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
    
    private CameraBoxSelectionDetector selectDetectorCamera;
    // private MouseBoxSelectionDetector selectDetectorMouse;

	public Game() {
		renderer = new Renderer();
		camera = new Camera();
		cameraInc = new Vector3f(0, 0, 0);
		gameItems = new ArrayList<GameItem>();
	}

	@Override
	public void init(Window window) throws Exception {
		renderer.init(window);

		selectDetectorCamera = new CameraBoxSelectionDetector();
		// selectDetectorMouse = new MouseBoxSelectionDetector();

        Texture texture = new Texture(Config.RESOURCES_DIR + "/textures/grassblock.png");
        Mesh mesh = new Mesh(CubeMesh.positions, CubeMesh.textCoords, CubeMesh.indices, texture);
        Material material = new Material(texture, 1.0f);
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
        camera.setPosition(10, 12, -12);
        camera.setRotation(0, 180, 0);

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
        // Vector3f prevPos = new Vector3f(camera.getPosition());
        Vector3f newPos = camera.calculateNewPosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
		// Check if there has been a collision. If true, set the y position to
		// the maximum height
		if (!camera.inCollision(gameItems, newPos)) {
			camera.movePosition(newPos);			
		} else {
			// camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
		}

        // Update view matrix
        camera.updateViewMatrix();
        
        this.selectDetectorCamera.selectGameItem(gameItems, camera, mouseInput);
        // if (mouseInput.isLeftButtonPressed()) {
        //     this.selectDetectorMouse.selectGameItem(gameItems, window, camera, mouseInput);
        // }
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
