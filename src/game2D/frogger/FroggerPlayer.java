package game2D.frogger; 

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL11;
import config.Config;
import engine.IGameLogic;
import engine.Timer;
import engine.Window;
import engine.Scene.Sounds;
import engine.graph.Camera;
import engine.graph.MouseInput;
import engine.sound.SoundBuffer;
import engine.sound.SoundListener;
import engine.sound.SoundManager;
import engine.sound.SoundSource;
import game2D.collision.Collision;
import game2D.entities.Entity;
import game2D.entities.Player;
import game2D.entities.Transform;
import game2D.render.Animation;
import game2D.world.IScene2D;
import game2D.world.Tile;

public class FroggerPlayer extends Player {

	public static final int ANIM_IDLE = 0;
	public static final int ANIM_WALK = 1;
	public static final int ANIM_SIZE = 2;
	private static final int TOTAL_LIVES = 7;
	private static int lives;
	private MouseInput input;
	private Window window;
	private final Timer timer;
	private double lastMovementTime;
	private double lastSubtractTime;
	private final long KEYBOARD_SENSIVITY = 220;
	private SoundManager soundMgr;
	public SoundSource ssCoinIn_1;
	public SoundSource ssCoinIn_2;
	public SoundSource ssHop;
	public SoundSource ssSquash;
	public SoundSource ssPlunk;

	public FroggerPlayer(Transform transform, IGameLogic game) {
		super(transform, game.getInput());
		this.input = game.getInput();
		this.setAnimation(ANIM_IDLE, new Animation(4, 4, "frogger/player/idle"));
		this.setAnimation(ANIM_WALK, new Animation(4, 4, "frogger/player/walking"));
		timer = new Timer();
		lastMovementTime = lastSubtractTime = timer.getTime();
		lives = TOTAL_LIVES;
		setupSound(game);
	}

	private void setupSound(IGameLogic game) {
		this.soundMgr = game.getSoundManager();
		try {
			soundMgr.init();
			soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
			
			SoundBuffer buffCoinIn_1 = new SoundBuffer(Config.RESOURCES_DIR + "/frogger/sound/sound-frogger-coin-in-i.ogg");
			soundMgr.addSoundBuffer(buffCoinIn_1);
		
			SoundBuffer buffCoinIn_2 = new SoundBuffer(Config.RESOURCES_DIR + "/frogger/sound/sound-frogger-coin-in-ii.ogg");
			soundMgr.addSoundBuffer(buffCoinIn_2);

			SoundBuffer buffHop = new SoundBuffer(Config.RESOURCES_DIR + "/frogger/sound/sound-frogger-hop.ogg");
			soundMgr.addSoundBuffer(buffHop);
			
			SoundBuffer buffSquash = new SoundBuffer(Config.RESOURCES_DIR + "/frogger/sound/sound-frogger-squash.ogg");
			soundMgr.addSoundBuffer(buffSquash);
			
			SoundBuffer buffPlunk = new SoundBuffer(Config.RESOURCES_DIR + "/frogger/sound/sound-frogger-plunk.ogg");
			soundMgr.addSoundBuffer(buffPlunk);
			
			ssHop = new SoundSource(false, true);
			ssHop.setBuffer(buffHop.getBufferId());
			soundMgr.addSoundSource(Sounds.BACKGROUND.toString(), ssHop);
			ssHop.setGain(1.0f);
			
			ssSquash = new SoundSource(false, true);
			ssSquash.setBuffer(buffSquash.getBufferId());
			soundMgr.addSoundSource(Sounds.BACKGROUND.toString(), ssSquash);
			ssSquash.setGain(1.0f);
			
			ssCoinIn_1 = new SoundSource(false, true);
			ssCoinIn_1.setBuffer(buffCoinIn_1.getBufferId());
			soundMgr.addSoundSource(Sounds.BACKGROUND.toString(), ssCoinIn_1);
			ssCoinIn_1.setGain(1.0f);
			
			ssCoinIn_2 = new SoundSource(false, true);
			ssCoinIn_2.setBuffer(buffCoinIn_2.getBufferId());
			soundMgr.addSoundSource(Sounds.BACKGROUND.toString(), ssCoinIn_2);
			ssCoinIn_2.setGain(1.0f);

			ssPlunk = new SoundSource(false, true);
			ssPlunk.setBuffer(buffPlunk.getBufferId());
			soundMgr.addSoundSource(Sounds.BACKGROUND.toString(), ssPlunk);
			ssPlunk.setGain(1.0f);

			soundMgr.setListener(new SoundListener(new Vector3f(0, 0, 0)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void input(float delta, Camera camera, IScene2D scene, IGameLogic game) {
		
		double currentTime = timer.getTime();
		
		float moveStep = 2f;
		this.useAnimation(ANIM_IDLE);
		Vector2f movement = new Vector2f();

		if (input.isKeyDown(GLFW.GLFW_KEY_A) || input.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			movement = new Vector2f().add(-moveStep, 0);
			transform.rotation = new Vector3f(0, 0, 90);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_D) || input.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
			movement = new Vector2f().add(moveStep, 0);
			transform.rotation = new Vector3f(0, 0, -90);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_W) || input.isKeyDown(GLFW.GLFW_KEY_UP)) {
			movement = new Vector2f().add(0, moveStep);
			transform.rotation = new Vector3f(0, 0, 0);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyDown(GLFW.GLFW_KEY_S) || input.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			movement = new Vector2f().add(0, -moveStep);
			transform.rotation = new Vector3f(0, 0, -180);
			this.useAnimation(ANIM_WALK);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_F) || input.isKeyReleased(GLFW.GLFW_KEY_ENTER)) {
			window.toggleFullscreen();
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_1)) {
			((Frogger) game).setLevel(1);
		}
		if (input.isKeyReleased(GLFW.GLFW_KEY_2)) {
			((Frogger) game).setLevel(2);
		}

		if (currentTime - lastMovementTime > delta * KEYBOARD_SENSIVITY) {
			move(movement);
			int bounce_direction = transform.position.y > -26 ? Collision.BOUNCE_DIR_DOWN : Collision.BOUNCE_DIR_UP;
			collideWithTiles(scene, bounce_direction);
			correctPosition(window, scene);
			camera.getPosition().lerp(this.transform.position.mul(-scene.getScale(), new Vector3f()), 0.02f);
			if (movement.x != 0 || movement.y != 0) {
				playJumpSound();
				if (movement.y > 0) {
					((Frogger) game).updateScore(10);
				}
				lastMovementTime = currentTime;
			}
		} 
	}

	public void playJumpSound() {
		ssHop.play();			
	}
	
	public void playCoinInSound() {
		ssCoinIn_1.play();
		ssCoinIn_2.play();
	}

	public void playPlunkSound() {
		ssPlunk.play();
	}

	public void update(float delta, Window window, Camera camera, IScene2D scene, IGameLogic game) {
		move(new Vector2f(speed, 0));
	}

	public void checkObstacles(Camera camera, IScene2D scene, IGameLogic game) {
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
				scene.resetScene(window, camera, game);
			}
		}
		
		if (inRiverArea(scene) && isIntersecting && intersectingObstacle instanceof Obstacle && !intersectingObstacle.isCollideFatal()) {
			speed = intersectingObstacle.getSpeed();
			// transform.position.x = intersectingObstacle.getTransform().position.x;
		} else {
			speed = 0;
		}

		if (inRiverArea(scene) && !isIntersecting) {
			if (substractLife()) {
				scene.resetScene(window, camera, game);
			}
		}
		
		if (inBasket()) {
			((FroggerScene) scene).copyFrogToBasket(this, game);
			scene.resetScene(window, camera, game);
		}
	}

	public boolean inBasket() {
		if (transform.position.y == -6 &&
			(transform.position.x == 2 ||
			transform.position.x == 8 ||
			transform.position.x == 14 ||
			transform.position.x == 20 ||
			transform.position.x == 26)) return true;
		return false;
	}
	
	public boolean inRiverArea(IScene2D scene) {
		int playerGridY = (int) transform.position.y;
		if (playerGridY >= -16 && playerGridY <= -8) {
			return true;
		} else {
			return false;			
		}
	}

	public void manageLevels(IGameLogic game, IScene2D scene) {
		if (isNextLevel(scene)) {
			((Frogger) game).setLevel(((Frogger) game).getCurrentLevel() + 1);
		} else if (isPreviousLevel(scene)) {
			// game.setLevel(game.getCurrentLevel() - 1);
		}
	}
	
	public boolean substractLife() {
		double currentTime = timer.getTime();
		if (currentTime - lastSubtractTime > 2) {
			lives--;
			lastSubtractTime = currentTime;
			ssSquash.play();
			try {
				TimeUnit.SECONDS.sleep((long) 1.5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public void resetPosition(Camera camera, IScene2D scene, IGameLogic game) {
		transform.position.x = game.getWindow().getWidth() / scene.getScale() / 2 - 1;
		transform.position.y = -game.getWindow().getHeight() / scene.getScale() + 6;
		camera.getPosition().set(transform.position.mul(-scene.getScale(), new Vector3f()));
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
		lives--;
	}

	public int getLives() {
		return lives;
	}

	public void cleanup() {
		ssHop.cleanup();
		ssSquash.cleanup();
	}
}
