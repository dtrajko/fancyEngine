package engine.tm;

import org.lwjgl.glfw.GLFW;

import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.Input;
import engine.sound.SoundManager;
import engine.tm.entities.Camera;
import engine.tm.render.Renderer;
import engine.tm.scene.Scene;

public class ThinMatrix implements IGameLogic {

	private Renderer renderer;
	private Window window;
	private IScene scene;
	private Input input;

	@Override
	public void init(Window window) throws Exception {
		renderer = new Renderer();
		renderer.init(window, null);
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
		renderer.render(window, scene.getCamera(), scene, true);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();
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
