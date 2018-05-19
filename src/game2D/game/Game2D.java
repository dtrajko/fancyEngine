package game2D.game;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import engine.Window;
import game.Game;
import game2D.assets.Assets;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.gui.Gui;
import game2D.io.Window2D;
import game2D.render.Camera2D;
import game2D.render.TileSheet;
import game2D.shaders.Shader;
import game2D.world.TileRenderer;
import game2D.world.World;

public class Game2D extends Game {

	private static int current_level = 1;
	private static int TOTAL_LEVELS = 2;
	private static World level;
	private static Camera2D camera;
	private int level_scale = 26;
	private static Map<Gui, Transform> guis = new HashMap<Gui, Transform>();
	private static Player player;
	private static boolean switchLevel = true;
	private static Shader shader;
	private static TileRenderer renderer;
	private static TileSheet sheet;
	private static Window window;

	public Game2D() {
		super();
	}

	@Override
	public void init(Window window) throws Exception {
		this.window = window;
		renderer = new TileRenderer();
		shader = new Shader("shader");
		sheet = new TileSheet("lives", 3);
		camera = new Camera2D(window.getWidth(), window.getHeight());
		renderer.init();
	}

	@Override
	public void input(Window window) {}

	@Override
	public void update(float interval) {
		if (switchLevel == true) {
			beginLevel();
			switchLevel = false;
		}
	}

	@Override
	public void render(Window window) {
		if ( window.isResized() ) {
			GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
			window.setResized(false);
		}
		level.render(renderer, shader, camera);
		renderer.clear();
	}

	public void beginLevel() {
		switch (Game2D.current_level) {
		case 1:
			level = new World("level_1", camera, this.level_scale, 5, this);
			level.calculateView(window);
			break;
		case 2:
			level = new World("level_2", camera, this.level_scale, 0, this);
			level.calculateView(window);
			break;
		default:
			System.err.println("Level index is not correct.");
			break;
		}
	}

	public static void onWindowResize() {
		camera.setProjection(window.getWidth(), window.getHeight());
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
		Game2D.player = player;
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

	public void update2D(float frame_cap) {
		updateGui();
		level.update(frame_cap * 10, (Window2D) window, camera, this);
		level.correctCamera(camera);
	}

	public void cleanUp() {
		Assets.deleteAsset();
	}

}
