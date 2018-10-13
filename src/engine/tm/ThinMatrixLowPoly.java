package engine.tm;

import org.lwjgl.glfw.GLFW;
import engine.Window;
import engine.graph.Input;
import engine.interfaces.IGameLogic;
import engine.interfaces.IPlayer;
import engine.interfaces.IScene;
import engine.tm.entities.Camera;
import engine.tm.particles.ParticleMaster;
import engine.tm.scene.SceneLowPoly;

public class ThinMatrixLowPoly implements IGameLogic {

	private Window window;
	private IScene scene;
	private Input input;

	@Override
	public void init(Window window) throws Exception {
		scene = new SceneLowPoly();
		((SceneLowPoly) scene).init();
		ParticleMaster.init(scene, ((SceneLowPoly) scene).getMasterRenderer().getProjectionMatrix());
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
		IPlayer player = ((SceneLowPoly) scene).getPlayer();
		scene.update(interval, input);
		player.move(interval, input, scene);
		((Camera) scene.getCamera()).moveWithPlayer(scene, input);
		ParticleMaster.update(scene);
	}

	@Override
	public void render(Window window) {
		scene.getMasterRenderer().render(window, scene);
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
