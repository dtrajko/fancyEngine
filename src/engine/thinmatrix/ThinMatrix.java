package engine.thinmatrix;

import org.lwjgl.glfw.GLFW;

import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.Camera;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.graph.MouseInput;
import engine.graph.Renderer;
import engine.sound.SoundManager;
import game2D.entities.Player;

public class ThinMatrix implements IGameLogic {

	private Window window;
    private final IRenderer renderer;
    private final ICamera camera;
    private IScene scene;
    boolean sceneChanged;
	private MouseInput mouseInput;

	public ThinMatrix() {
        renderer = new ThinMatrixRenderer();
        camera = new Camera();
	}

	@Override
	public void init(Window window) throws Exception {
    	this.window = window;
        scene = new ThinMatrixScene();
        renderer.init(window, scene);
        scene.init(window, camera);
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
		// TODO Auto-generated method stub

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
