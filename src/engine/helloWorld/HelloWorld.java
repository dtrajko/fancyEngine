package engine.helloWorld;

import org.lwjgl.glfw.GLFW;

import engine.Window;
import engine.graph.Input;
import engine.interfaces.IGameLogic;
import engine.interfaces.IScene;

public class HelloWorld implements IGameLogic {

	private Window window;
	private IScene scene;
	private Input input;

	@Override
	public void init(Window window) throws Exception {
		scene = new SceneHelloWorld();
		scene.init();
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
	}

	@Override
	public void render(Window window) {
		scene.render(window);
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
	public void cleanUp() {
		scene.cleanUp();
	}
}
