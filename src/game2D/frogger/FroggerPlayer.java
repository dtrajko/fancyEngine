package game2D.frogger; 

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import engine.IGameLogic;
import engine.Timer;
import engine.Window;
import engine.graph.Camera;
import engine.graph.MouseInput;
import game2D.collision.Collision;
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
	private final Timer timer;
	private double lastMovementTime;
	private final double KEYBOARD_SENSIVITY = 100;

	public FroggerPlayer(Transform transform, MouseInput input) {
		super(transform, input);
		this.input = input;
		this.setAnimation(ANIM_IDLE, new Animation(4, 10, "frogger/player/idle"));
		this.setAnimation(ANIM_WALK, new Animation(4, 10, "frogger/player/walking"));
		timer = new Timer();
		lastMovementTime = timer.getTime();
	}

	public void input(float delta, Camera camera, IScene scene, IGameLogic game) {
		
		double currentTime = timer.getTime();
		
		float moveStep = 2f;
		this.useAnimation(ANIM_IDLE);
		Vector2f movement = new Vector2f();

		if (input.isKeyDown(GLFW.GLFW_KEY_A) || input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			movement = new Vector2f().add(-moveStep, 0);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			movement = new Vector2f().add(moveStep, 0);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_W) || input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			movement = new Vector2f().add(0, moveStep);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S) || input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			movement = new Vector2f().add(0, -moveStep);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_F) || input.isKeyReleased(GLFW.GLFW_KEY_ENTER)) {
			window.toggleFullscreen();
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_1)) {
			game.setLevel(1);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_2)) {
			game.setLevel(2);
		}
		
		if (currentTime - lastMovementTime > delta * KEYBOARD_SENSIVITY) {
			move(movement);
			int bounce_direction = transform.position.y > -26 ? Collision.BOUNCE_DIR_DOWN : Collision.BOUNCE_DIR_UP;
			collideWithTiles(scene, bounce_direction);
			correctPosition(window, scene);
			
			camera.getPosition().lerp(this.transform.position.mul(-scene.getScale(), new Vector3f()), 0.02f);
			manageLives(game, scene);
			manageLevels(game, scene);

			lastMovementTime = currentTime;
		} 
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
