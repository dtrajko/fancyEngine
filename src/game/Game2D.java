package game;

import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import engine.graph.MouseInput;
import game2D.assets.Assets;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.gui.Gui;
import game2D.render.TileSheet;
import game2D.world.TileRenderer;
import game2D.world.World;

public class Game2D implements IGameLogic {

	private static int current_level = 1;
	private static int TOTAL_LEVELS = 2;
	private static World level;
	private final Camera camera;
	private int level_scale = 26;
	private static Map<Gui, Transform> guis = new HashMap<Gui, Transform>();
	public static Player player;
	private static boolean switchLevel = true;
	private static TileRenderer renderer;
	private static TileSheet sheet;
	private static Window window;
	private MouseInput mouseInput;

	public Game2D() {
		renderer = new TileRenderer();
		camera = new Camera();
	}

	@Override
	public void init(Window win) throws Exception {
		window = win;
		camera.setOrthoProjection(window);
		renderer.init();
		sheet = new TileSheet("lives", 3);
		beginLevel();
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
	public void input(Window window, MouseInput mouseInput) {
		this.mouseInput = mouseInput;
	}

	@Override
	public void update(float interval, MouseInput mouseInput) {
		if (switchLevel == true) {
			beginLevel();
			switchLevel = false;
		}
		mouseInput.update(window);
		player.update(interval * 5, window, camera, level, this);
		level.update(interval * 5, window, camera, this);
		level.correctCamera(camera);
		updateGui();
	}

	@Override
	public void render(Window window) {
		if ( window.isResized() ) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResized(false);
		}
		renderer.render(level, camera);
		renderer.clear();
	}

	public void onWindowResize() {
		camera.setOrthoProjection(window);
		level.calculateView(window);
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
	}

	public void updateGui() {
		guis.clear();
		int lives_x = -600;
		int lives_y = -320;
		for (int i = 0; i < player.getLives(); i++) {
			guis.put(new Gui(sheet, window), new Transform(new Vector3f(lives_x, lives_y, 0), 20));
			lives_x += 45;
		}
	}

	private boolean gameOver() {
		if (player instanceof Player) {
			return player.getLives() <= 0;
		}
		return false;
	}

	public void setPlayer(Player player) {
		player = player;
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
		level.cleanup();
	}
}
