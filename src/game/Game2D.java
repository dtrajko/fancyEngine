package game;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import engine.GameEngine;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Input;
import engine.interfaces.IGameLogic;
import engine.sound.SoundManager;
import game2D.assets.Assets;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.gui.Gui;
import game2D.render.TileRenderer;
import game2D.render.TileSheet;
import game2D.world.World;

public class Game2D implements IGameLogic {

	private static int current_level = 1;
	private static int TOTAL_LEVELS = 2;
	private static World level;
	private final Camera camera;
	private int level_scale = 32;
	private static Map<Gui, Transform> guis = new HashMap<Gui, Transform>();
	public static Player player;
	private static boolean switchLevel = true;
	private static TileRenderer renderer;
	private static TileSheet sheet;
	private static Window window;
	private Input input;
	private static float SPEED = 0.1f;
	double frame_cap = 1.0 / GameEngine.TARGET_FPS;
	private final SoundManager soundMgr;

	public Game2D() {
		renderer = new TileRenderer();
		camera = new Camera();
		soundMgr = new SoundManager();
	}

	@Override
	public void init(Window win) throws Exception {
		window = win;
		renderer.init();
		camera.setOrthoProjection(window);
		initGui();
		// beginLevel();
	}

	@Override
	public void initGui() {
		sheet = new TileSheet("textures/game2D/sheets/lives", 3);
	}

	public void beginLevel() {		
		switch (current_level) {
		case 1:
			level = new World("level_1", camera, this.level_scale, 5, this);
			level.calculateView(window);
			player = level.getPlayer();
			break;
		case 2:
			level = new World("level_2", camera, this.level_scale, 0, this);
			level.calculateView(window);
			player = level.getPlayer();
			break;
		default:
			System.err.println("Level index is not correct.");
			break;
		}
	}

	@Override
	public void input(Window window, Input input) {
		this.input = input;
		if (switchLevel == true) {
			beginLevel();
			switchLevel = false;
		}
		player.input(SPEED, camera, level, this);
		level.correctCamera(camera);

        if (input.isKeyReleased(GLFW.GLFW_KEY_ESCAPE)) {
        	window.close();
        }
	}

	@Override
	public void update(float interval, Input input) {
		input.update(window);
		updateGui();
		level.update((float) frame_cap, window, camera, this);
	}

	@Override
	public void render(Window window) {
		renderer.render(level, camera);
		for (Gui gui : guis.keySet()) {
			gui.render(guis.get(gui), 0);
		}		
	}

	public void onWindowResize() {
		camera.setOrthoProjection(window);
		level.calculateView(window);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
	}

	public void updateGui() {
		guis.clear();
		float lives_x = -0.93f;
		float lives_y = -0.9f;
		for (int i = 0; i < player.getLives(); i++) {
			guis.put(new Gui(sheet, window), new Transform(new Vector3f(lives_x, lives_y, 0), new Vector3f(0.04f, 0.06f, 1.0f)));
			lives_x += 0.075f;
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

	public Input getInput() {
		return input;
	}

	public Window getWindow() {
		return window;
	}

	@Override
	public void cleanUp() {
		Assets.deleteAsset();
		renderer.clear();
		level.cleanup();
	}

	@Override
	public SoundManager getSoundManager() {
		return soundMgr;
	}
}
