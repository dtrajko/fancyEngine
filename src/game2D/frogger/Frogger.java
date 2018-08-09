package game2D.frogger;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import engine.graph.MouseInput;
import game2D.assets.Assets;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.gui.Gui;
import game2D.render.TileRenderer;
import game2D.render.TileSheet;

public class Frogger implements IGameLogic {

	private static int current_level = 1;
	private static int TOTAL_LEVELS = 2;
	private static FroggerScene scene;
	private final Camera camera;
	private int level_scale = 20;
	private static Map<Gui, Transform> guis = new HashMap<Gui, Transform>();
	public static Player player;
	private static boolean switchLevel = true;
	private static TileRenderer renderer;
	private static TileSheet sheet;
	private static Window window;
	private MouseInput mouseInput;
	private static float SPEED = 0.001f;
	double frame_cap = 1.0 / GameEngine.TARGET_FPS;

	public Frogger() {
		renderer = new TileRenderer();
		camera = new Camera();
	}

	@Override
	public void init(Window win) throws Exception {
		window = win;
		renderer.init();
		camera.setOrthoProjection(window);
		sheet = new TileSheet("frogger/textures/sheets/lives", 3);
	}

	public void beginLevel() {
		switch (current_level) {
		case 1:
			scene = new FroggerScene("01", camera, this.level_scale, 4, this);
			scene.calculateView(window);
			player = scene.getPlayer();
			break;
		default:
			System.err.println("Level index is not correct.");
			break;
		}
	}

	@Override
	public void input(Window window, MouseInput mouseInput) {
		this.mouseInput = mouseInput;
		if (switchLevel == true) {
			beginLevel();
			switchLevel = false;
		}
		player.input(SPEED, camera, scene, this);
		scene.correctCamera(camera);
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		mouseInput.update(window);
		updateGui();
		scene.update((float) frame_cap, window, camera, this);
	}

	@Override
	public void render(Window window) {
		renderer.render(scene, camera);
		for (Gui gui : guis.keySet()) {
			gui.render(guis.get(gui), 0);
		}		
	}

	public void onWindowResize() {
		camera.setOrthoProjection(window);
		scene.calculateView(window);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
	}

	public void updateGui() {
		guis.clear();
		float lives_x = -0.93f;
		float lives_y = -0.81f;
		for (int i = 0; i < player.getLives(); i++) {
			guis.put(new Gui(sheet, window), new Transform(new Vector3f(lives_x, lives_y, 0), new Vector3f(0.052f, 0.052f, 1f)));
			lives_x += 0.1f;
		}
	}

	public boolean gameOver() {
		if (player instanceof Player) {
			return player.getLives() <= 0;
		}
		return false;
	}

	public void setPlayer(Player p) {
		player = p;
	}

	public Player getPlayer() {
		return player;
	}

	public void setLevel(int level) {
		if (level < 1) level = 1;
		if (level > TOTAL_LEVELS) level = 1; // TOTAL_LEVELS;
		current_level = level;
		switchLevel = true;
	}

	public int getCurrentLevel() {
		return current_level;
	}

	public MouseInput getInput() {
		return mouseInput;
	}

	public Window getWindow() {
		return window;
	}

	@Override
	public void cleanup() {
		Assets.deleteAsset();
		renderer.clear();
		if (scene != null) scene.cleanup();
	}
}
