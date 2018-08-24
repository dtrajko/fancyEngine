package engine.tm2;

import org.lwjgl.glfw.GLFW;

import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.graph.Input;
import engine.sound.SoundManager;
import engine.tm.Camera;

public class ThinMatrix2 implements IGameLogic {

	private Window window;
    private IRenderer renderer;
    private ICamera camera;
    private IScene scene;
    boolean sceneChanged;
	private Input input;

	public ThinMatrix2() {
	}

	@Override
	public void init(Window window) throws Exception {
    	this.window = window;
    	camera = new ThinMatrixCamera(window);
        scene = new ThinMatrixScene();
        ((ThinMatrixScene) scene).init(window, camera);
        renderer = new ThinMatrixRenderer();
        renderer.init(window, scene);
	}

	@Override
	public void input(Window window, Input input) {
		this.input = input;

        if (input.isKeyReleased(GLFW.GLFW_KEY_ESCAPE)) {
        	window.close();
        }
	}

	@Override
	public void update(float interval, Input input) {
		scene.update(interval, input);
		((ThinMatrixCamera) scene.getCamera()).move(input);
	}

	@Override
	public void render(Window window) {
		sceneChanged = true;
		renderer.render(window, camera, scene, sceneChanged);
	}

	@Override
	public Window getWindow() {
		return window;
	}

	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public SoundManager getSoundManager() {
		return null;
	}

	@Override
	public void initGui() {
	}

	@Override
	public void cleanup() {
    	scene.save();
    	scene.cleanup();
        renderer.cleanup();
	}

}
