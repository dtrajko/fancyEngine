package game;

import java.util.HashMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import engine.IGameLogic;
import engine.Scene;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Mesh;
import engine.graph.MouseInput;
import engine.graph.Renderer;
import engine.graph.particles.FlowParticleEmitter;
import engine.gui.GuiElement;
import engine.gui.GuiManager;
import engine.gui.fonts.GUIText;
import engine.gui.fonts.TextMaster;
import engine.sound.SoundManager;

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
    private GuiManager guiManager;
    // private boolean crouchEnabled = false;

    public static final int blockScale = 1;

    private HashMap<String, Mesh> meshTypesMap = new HashMap<String, Mesh>();

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
        TextMaster.init();
        scene.init(meshTypesMap, particleEmitter, soundMgr, camera, guiManager, window);
        selectDetectorCamera = new CameraBoxSelectionDetector();
    }

    @Override
    public void render(Window window) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }
        renderer.render(window, camera, scene, sceneChanged);
        renderer.renderGui(guiManager, window);
        renderer.renderGuiText(guiManager);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
    	sceneChanged = false;
        cameraInc.set(0, 0, 0);

        guiManager.input(mouseInput, window);

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_E)) {
        	sceneChanged = true;
        	guiManager.toggleGui(window);
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_K)) {
        	scene.save();
        }

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_L)) {
        	sceneChanged = true;
        	guiManager.toggleImportDialog(window);
        	// scene.load(meshTypesMap);
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

        float mouseWheelDelta = mouseInput.getMouseWheelDelta();
        if (mouseWheelDelta != 0) {
        	cameraInc.z = SPEED * 4 * mouseWheelDelta;
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

        /* crouch mode not working yet
        if (actualGravity < 0 && mouseInput.isKeyReleased(GLFW.GLFW_KEY_LEFT_SHIFT)) {
        	if (!crouchEnabled) {
        		camera.setActualHeight(Camera.HEIGHT - blockScale * 2);
        		crouchEnabled = true;
        	} else {
        		camera.setActualHeight(Camera.HEIGHT);
        		crouchEnabled = false;
        	}
        	cameraInc.y = 0;
        }
        System.out.println("Camera height = " + camera.getActualHeight() + " crouchEnabled = " + crouchEnabled + " actualGravity = " + actualGravity);
        */

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

	@Override
    public void update(float interval, MouseInput mouseInput) {
		if (guiManager.getUpdateEnabled()) {
			updateConditional(mouseInput);
		}
    }

	public void updateConditional(MouseInput mouseInput) {
    	// Update camera based on mouse
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);

        // Vector3f camPos = camera.getPosition();

        // Update camera position
        Vector3f newCamPos = camera.calculateNewPosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

		// Check if there has been a collision
        // newPosCameraBase - the camera imaginary "tripod base" we use to check the collision. It's bellow the camera "lens"
        // if (!newCamPos.equals(camPos)) { // disabled, because of problems with disabled gravity when camera is close to block
    	if (scene.inCollision(newCamPos, true, camera)) {
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
        if (!guiManager.isInventoryOn()) {
        	Mesh nextMesh = guiManager.getNextBlock() instanceof GuiElement ? guiManager.getNextBlock().getMesh() : null;
        	selectDetectorCamera.selectGameItem(scene, camera, mouseInput, nextMesh);
        }

    	// Update view matrix
    	camera.updateViewMatrix();
	}

    @Override
    public void cleanup() {
    	scene.save();
        renderer.cleanup();
        scene.cleanup();
        soundMgr.cleanup();
    }    
}
