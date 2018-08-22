package engine.tm;

import org.lwjgl.glfw.GLFW;

import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.Camera;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.graph.MouseInput;
import engine.sound.SoundManager;

public class ThinMatrix implements IGameLogic {

	private Window window;
    private IRenderer renderer;
    private ICamera camera;
    private IScene scene;
    boolean sceneChanged;
	private MouseInput mouseInput;

	public ThinMatrix() {
	}

	@Override
	public void init(Window window) throws Exception {
    	this.window = window;
    	renderer = new ThinMatrixRenderer();
    	camera = new ThinMatrixCamera(window);
        scene = new ThinMatrixScene();
        scene.init(window, camera);
        renderer.init(window, scene);
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		this.mouseInput = mouseInput;

        if (mouseInput.isKeyReleased(GLFW.GLFW_KEY_ESCAPE)) {
        	window.close();
        }
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		scene.update(interval);

    	// Update view matrix
    	camera.updateViewMatrix();
	}

	@Override
	public void render(Window window) {
		sceneChanged = true;
		renderer.render(window, camera, scene, sceneChanged);
	}

	@Override
	public int getCurrentLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setLevel(int currentLevel) {
		// TODO Auto-generated method stub

	}

	@Override
	public Window getWindow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MouseInput getInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoundManager getSoundManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initGui() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanup() {
    	scene.save();
    	scene.cleanup();
        renderer.cleanup();
	}

}
