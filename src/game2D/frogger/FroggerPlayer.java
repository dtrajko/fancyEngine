package game2D.frogger; 

import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import engine.IGameLogic;
import engine.Timer;
import engine.Window;
import engine.graph.Camera;
import engine.graph.MouseInput;
import game2D.collision.Collision;
import game2D.entities.Entity;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.render.Animation;
import game2D.textures.Texture;
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
	private double lastSubtractTime;
	private final double KEYBOARD_SENSIVITY = 100;

	public FroggerPlayer(Transform transform, MouseInput input) {
		super(transform, input);
		this.input = input;
		this.setAnimation(ANIM_IDLE, new Animation(4, 10, "frogger/player/idle"));
		this.setAnimation(ANIM_WALK, new Animation(4, 10, "frogger/player/walking"));
		timer = new Timer();
		lastMovementTime = lastSubtractTime = timer.getTime();
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
			lastMovementTime = currentTime;
		} 
	}

	public void checkObstacles(Camera camera, IScene scene, IGameLogic game) {
		List<Entity> entities = scene.getEntities();
		
		boolean isIntersecting = false;
		Obstacle intersectingObstacle = null;
		
		for (int i = 0; i < entities.size(); i++) {
			Entity obstacle = entities.get(i);
			if (obstacle instanceof Obstacle) {		
				if (getBoundingBox().isIntersecting(obstacle.getBoundingBox())) {
					isIntersecting = true;
					intersectingObstacle = (Obstacle) obstacle;
				}
			}
		}
		
		if (!inRiverArea(scene) && isIntersecting && intersectingObstacle instanceof Obstacle && intersectingObstacle.isCollideFatal()) {
			if (substractLife()) {
				resetPosition(camera, scene, game);
			}
		}
		
		if (inRiverArea(scene) && isIntersecting && intersectingObstacle instanceof Obstacle && !intersectingObstacle.isCollideFatal()) {
			transform.position.x = intersectingObstacle.getTransform().position.x;
		}

		if (inRiverArea(scene) && !isIntersecting) {
			if (substractLife()) {
				resetPosition(camera, scene, game);
			}
		}
		
		if (inBasket()) {
			copyFrogToBasket(scene);
			resetPosition(camera, scene, game);
		}
	}
	
	public boolean inBasket() {
		if (transform.position.y == -2 &&
			(transform.position.x == 2 ||
			transform.position.x == 8 ||
			transform.position.x == 14 ||
			transform.position.x == 20 ||
			transform.position.x == 26)) return true;
		return false;
	}
	
	public void copyFrogToBasket(IScene scene) {
		Transform transform = new Transform();
		transform.position.x = this.transform.position.x;
		transform.position.y = this.transform.position.y;
		Texture txFrog = new Texture("frogger/player/idle/0");
		TextureEntity frogCopy = new TextureEntity(transform, txFrog);
		scene.getEntities().add(frogCopy);
	}

	public boolean inRiverArea(IScene scene) {
		int playerGridY = (int) transform.position.y;
		if (playerGridY >= -12 && playerGridY <= -4) {
			return true;
		} else {
			return false;			
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
	
	public boolean substractLife() {
		double currentTime = timer.getTime();
		if (currentTime - lastSubtractTime > 2) {
			lives--;
			lastSubtractTime = currentTime;
			return true;
		}
		return false;
	}
	
	public void resetPosition(Camera camera, IScene scene, IGameLogic game) {
		transform.position.x = game.getWindow().getWidth() / scene.getScale() / 2 - 1;
		transform.position.y = -game.getWindow().getHeight() / scene.getScale() + 6;
		camera.getPosition().set(transform.position.mul(-scene.getScale(), new Vector3f()));
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
		lives--;
	}

	public int getLives() {
		return lives;
	}
}
