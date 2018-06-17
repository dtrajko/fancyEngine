package game2D.entities;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import game.Game2D;
import game2D.io.Input;
import game2D.render.Animation;
import game2D.world.Tile;
import game2D.world.World;

public class Player extends Entity {

	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
	public static final int ANIM_SIZE = 2;
	private static final float GRAVITY = 0.08f;
	private static final float JUMP_FORCE = 3f;
	private static boolean jump_allowed;
	private static float previous_height;
	private static int subsequent_jumps = 0;
	private static int lives = 5;
	private static Input input;
	private Window window;

	public Player(Window window) {
		this(new Transform());
		this.window = window;
		input = new Input(window.getWindowHandle());
	}

	public Player(Transform transform) {
		super(ANIM_SIZE, transform);
		this.setAnimation(ANIM_IDLE, new Animation(4, 10, "player/idle"));
		this.setAnimation(ANIM_WALK, new Animation(4, 10, "player/walking"));
		previous_height = this.transform.position.y;		
	}

	public void update(float delta, Window window, Camera camera, World world, IGameLogic game) {

		this.useAnimation(ANIM_IDLE);
		Vector2f movement = new Vector2f();

		movement.add(0, -GRAVITY);
		
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
			System.out.println("Player idemo pravo!!!");
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
		collideWithTiles(world);
		correctPosition(window, world);
		camera.getPosition().lerp(this.transform.position.mul(-world.getScale(), new Vector3f()), 0.02f);	
		manageLives((Game2D) game, world);
		manageLevels((Game2D) game, world);
	}

	public void manageLevels(Game2D game, World world) {
		if (isNextLevel(world)) {
			game.setLevel(game.getCurrentLevel() + 1);
		} else if (isPreviousLevel(world)) {
			// game.setLevel(game.getCurrentLevel() - 1);
		}
	}

	public Tile getCurrentTile(World world) {
		int x = (int)(transform.position.x / 2);
		int y = (int)(-transform.position.y / 2);
		Tile tile = world.getTile(x, y);
		return tile;
	}

	public boolean isNextLevel(World world) {
		return getCurrentTile(world).isNextLevel();
	}

	public boolean isPreviousLevel(World world) {
		return getCurrentTile(world).isPreviousLevel();
	}

	public void manageLives(Game2D game, World world) {
		if (previous_height == this.transform.position.y) {
			return;
		}
		int y = (int)(-transform.position.y / 2);
		if (y >= world.getHeight() - 1) {
			lives--;
			if (lives < 0) lives = 0;
			game.setLevel(game.getCurrentLevel());
		}
	}

	public int getLives() {
		return lives;
	}
}
