package engine.tm;

import org.lwjgl.glfw.GLFW;

import engine.Window;
import engine.graph.Input;
import engine.interfaces.IGameLogic;
import engine.interfaces.IPlayer;
import engine.interfaces.IScene;
import engine.tm.entities.Camera;
import engine.tm.particles.ParticleMaster;
import engine.tm.scene.Scene;
import engine.tm.toolbox.MousePicker;

public class ThinMatrix implements IGameLogic {

	private Window window;
	private IScene scene;
	private Input input;
	private MousePicker mousePicker;

	@Override
	public void init(Window window) throws Exception {
		scene = new Scene();
		((Scene) scene).init();
		ParticleMaster.init(scene, ((Scene) scene).getMasterRenderer().getProjectionMatrix());
		mousePicker = new MousePicker(scene, ((Scene) scene).getMasterRenderer().getProjectionMatrix());
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
		IPlayer player = ((Scene) scene).getPlayer();
		scene.update(interval, input);
		player.move(interval, input, scene);
		((Camera) scene.getCamera()).moveWithPlayer(scene, input);
		mousePicker.update(input);
		ParticleMaster.update(scene);
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
		ParticleMaster.cleanUp();
		scene.cleanUp();
	}
}
