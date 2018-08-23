package engine.tm;

import engine.IGameLogic;
import engine.IScene;
import engine.Window;
import engine.graph.MouseInput;
import engine.sound.SoundManager;
import engine.tm.render.Renderer;
import engine.tm.scene.Scene;

public class ThinMatrix implements IGameLogic {
	
	private Renderer renderer;
	private Window window;
	private IScene scene;

	@Override
	public void init(Window window) throws Exception {
		renderer = new Renderer();
		renderer.init(window, null);
		scene = new Scene();
		scene.init(window, null);
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
	}

	@Override
	public void render(Window window) {
		renderer.render(window, null, scene, true);
	}

	@Override
	public void cleanup() {
		renderer.cleanup();

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

}
