package game2D.entities; 

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import engine.graph.MouseInput;
import game.Game2D;
import game2D.collision.Collision;
import game2D.render.Animation;
import game2D.world.IScene2D;
import game2D.world.Tile;

public class Player extends AnimatedEntity {

	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
	public static final int ANIM_SIZE = 2;
	private static final float GRAVITY = 0.09f;
	private static final float JUMP_FORCE = 3f;
	private static boolean jump_allowed;
	private static float previous_height;
	private static int subsequent_jumps = 0;
	private static int lives = 5;
	private MouseInput input;
	private Window window;

	public Player(Window window, MouseInput input) {
		this(new Transform(), input);
		this.window = window;
	}

	public Player(Transform transform, MouseInput input) {
		super(ANIM_SIZE, transform);
		this.input = input;
		this.setAnimation(ANIM_IDLE, new Animation(4, 10, "textures/game2D/player/idle"));
		this.setAnimation(ANIM_WALK, new Animation(4, 10, "textures/game2D/player/walking"));
		previous_height = this.transform.position.y;
	}

	public void input(float delta, Camera camera, IScene2D scene, IGameLogic game) {

		this.useAnimation(ANIM_IDLE);
		Vector2f movement = new Vector2f();

		movement.add(0, -GRAVITY);

		Tile tileBellow = getTileBellow(scene);
		float tileDeltaCoef = delta * 2.75f; // adjustment to sync speed with tile bellow
		movement.add(tileBellow.deltaX * tileDeltaCoef, tileBellow.deltaY * tileDeltaCoef);

		if (this.transform.position.y >= previous_height) {
			jump_allowed = true;
		} else {
			jump_allowed = false;			
		}
		previous_height = this.transform.position.y;

		if (input.isKeyDown(GLFW.GLFW_KEY_A) || input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			movement.add(-delta, 0);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			movement.add(delta, 0);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_W)) {
			// movement.add(0, delta);
			// this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S)) {
			// movement.add(0, -delta);
			// this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_SPACE) && jump_allowed) {
			subsequent_jumps++;
			movement.add(0, delta * (JUMP_FORCE / (subsequent_jumps * (GRAVITY * 2))));
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_SPACE)) {
			subsequent_jumps = 0;
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_F) || input.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
			window.toggleFullscreen();
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_1)) {
			((Game2D) game).setLevel(1);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_2)) {
			((Game2D) game).setLevel(2);
		}

		move(movement);
		collideWithTiles(scene, Collision.BOUNCE_DIR_UP);
		correctPosition(window, scene);

		camera.getPosition().lerp(this.transform.position.mul(-scene.getScale(), new Vector3f()), 0.02f);
		manageLives(game, scene);
		manageLevels(game, scene);
	}

	public void update(float delta, Window window, Camera camera, IScene2D scene, IGameLogic game) {
	}

	public void manageLevels(IGameLogic game, IScene2D scene) {
		if (isNextLevel(scene)) {
			((Game2D) game).setLevel(((Game2D) game).getCurrentLevel() + 1);
		} else if (isPreviousLevel(scene)) {
			// game.setLevel(game.getCurrentLevel() - 1);
		}
	}

	public Tile getCurrentTile(IScene2D scene) {
		int x = (int)(transform.position.x / 2);
		int y = (int)(-transform.position.y / 2);
		Tile tile = scene.getTile(x, y);
		return tile;
	}

	public Tile getTileBellow(IScene2D scene) {
		int x = (int)(transform.position.x / 2);
		int y = (int)(-transform.position.y / 2 + 1);
		Tile tile = scene.getTile(x, y);
		return tile;
	}

	public boolean isNextLevel(IScene2D scene) {
		if (getCurrentTile(scene) != null) {
			return getCurrentTile(scene).getType().isNextLevel();			
		}
		return false;
	}

	public boolean isPreviousLevel(IScene2D scene) {
		if (getCurrentTile(scene) != null) {
			return getCurrentTile(scene).getType().isPreviousLevel();
		}
		return false;
	}

	public void manageLives(IGameLogic game, IScene2D scene) {
		if (previous_height == this.transform.position.y) {
			return;
		}
		int y = (int)(-transform.position.y / 2);
		if (y >= scene.getHeight() - 1) {
			lives--;
			if (lives < 0) lives = 0;
			((Game2D) game).setLevel(((Game2D) game).getCurrentLevel());
		}
	}

	public int getLives() {
		return lives;
	}
}
