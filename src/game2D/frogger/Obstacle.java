package game2D.frogger;

import org.joml.Vector2f;

import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import game2D.entities.Transform;
import game2D.shaders.Shader;
import game2D.textures.Texture;
import game2D.world.IScene;

public class Obstacle extends TextureEntity {

	private float speed;
	private int maxLength;
	private boolean collideFatal;

	public Obstacle(Transform transform, Texture tx, float spd, boolean collFatal) {
		super(transform, tx);
		this.speed = spd;
		this.maxLength = 6;
		this.collideFatal = false;
		this.collideFatal = collFatal;
	}

	@Override
	public void update(float delta, Window window, Camera camera, IScene scene, IGameLogic game) {
		int grid_width = window.getWidth() / scene.getScale();
		move(new Vector2f(speed, 0));
		if (transform.position.x < 0 - maxLength) {
			transform.position.x = grid_width + maxLength;
		} else if (transform.position.x > grid_width + maxLength) {
			transform.position.x = 0 - maxLength;
		}
	}

	public void render(Shader shader, Camera camera, IScene scene) {
		super.render(shader, camera, scene);
	}
	
	public boolean isCollideFatal() {
		return collideFatal;
	}

}
