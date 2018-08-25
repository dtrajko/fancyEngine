package engine.tm;

import org.lwjgl.glfw.GLFW;

import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.Input;
import engine.sound.SoundManager;
import engine.tm.entities.Camera;
import engine.tm.render.MasterRenderer;
import engine.tm.scene.Scene;

public class ThinMatrix implements IGameLogic {

	private MasterRenderer masterRenderer;
	private Window window;
	private IScene scene;
	private Input input;

	@Override
	public void init(Window window) throws Exception {
		masterRenderer = new MasterRenderer();
		scene = new Scene();
		((Scene) scene).init(window);
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
		((Camera) scene.getCamera()).move(input);
	}

	@Override
	public void render(Window window) {
		masterRenderer.render(window, scene);
	}

	@Override
	public void cleanup() {
		masterRenderer.cleanUp(scene);
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
}
