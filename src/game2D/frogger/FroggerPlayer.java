package game2D.frogger; 

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import engine.graph.MouseInput;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.render.Animation;
import game2D.world.IScene;
import game2D.world.Tile;

public class FroggerPlayer extends Player {

	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
	public static final int ANIM_SIZE = 2;
	private static int lives = 5;
	private MouseInput input;
	private Window window;

	public FroggerPlayer(Transform transform, MouseInput input) {
		super(transform, input);
		this.input = input;
		this.setAnimation(ANIM_IDLE, new Animation(4, 10, "frogger/player/idle"));
		this.setAnimation(ANIM_WALK, new Animation(4, 10, "frogger/player/idle"));
	}

	public void input(float delta, Camera camera, IScene scene, IGameLogic game) {

		this.useAnimation(ANIM_IDLE);
		Vector2f movement = new Vector2f();

		if (input.isKeyDown(GLFW.GLFW_KEY_A) || input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			movement.add(-delta, 0);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			movement.add(delta, 0);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_W) || input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			movement.add(0, delta);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S) || input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			movement.add(0, -delta);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_F) || input.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			window.toggleFullscreen();
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_1)) {
			game.setLevel(1);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_2)) {
			game.setLevel(2);
		}

		move(movement);
		collideWithTiles(scene);
		correctPosition(window, scene);

		camera.getPosition().lerp(this.transform.position.mul(-scene.getScale(), new Vector3f()), 0.02f);
		manageLives(game, scene);
		manageLevels(game, scene);
	}

	public void update(float delta, Window window, Camera camera, IScene scene, IGameLogic game) {
	}

	public void manageLevels(IGameLogic game, IScene scene) {
		if (isNextLevel(scene)) {
			game.setLevel(game.getCurrentLevel() + 1);
		} else if (isPreviousLevel(scene)) {
			// game.setLevel(game.getCurrentLevel() - 1);
		}
	}

	public Tile getCurrentTile(IScene scene) {
		int x = (int)(transform.position.x / 2);
		int y = (int)(-transform.position.y / 2);
		Tile tile = scene.getTile(x, y);
		return tile;
	}

	public Tile getTileBellow(IScene scene) {
		int x = (int)(transform.position.x / 2);
		int y = (int)(-transform.position.y / 2 + 1);
		Tile tile = scene.getTile(x, y);
		return tile;
	}

	public boolean isNextLevel(IScene scene) {
		if (getCurrentTile(scene) != null) {
			return getCurrentTile(scene).getType().isNextLevel();			
		}
		return false;
	}

	public boolean isPreviousLevel(IScene scene) {
		if (getCurrentTile(scene) != null) {
			return getCurrentTile(scene).getType().isPreviousLevel();
		}
		return false;
	}

	public void manageLives(IGameLogic game, IScene scene) {
	}

	public int getLives() {
		return lives;
	}
}
