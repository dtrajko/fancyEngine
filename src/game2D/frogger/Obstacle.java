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

	private boolean collideFatal;

	public Obstacle(Transform transform, Texture tx, float spd, boolean collFatal) {
		super(transform, tx);
		this.speed = spd;
		this.collideFatal = collFatal;
	}

	public Obstacle(Transform transform) {
		super(transform);
		this.speed = 0;
		this.collideFatal = false;
	}

	@Override
	public void update(float delta, Window window, Camera camera, IScene scene, IGameLogic game) {
		int grid_width = window.getWidth() / scene.getScale();
		move(new Vector2f(speed, 0));
		if (transform.position.x < 0 - this.texture.getLengthX() - 1) {
			transform.position.x = grid_width + this.texture.getLengthX();
		} else if (transform.position.x > grid_width + this.texture.getLengthX()) {
			transform.position.x = 0 - this.texture.getLengthX() - 1;
		}
	}

	public void render(Shader shader, Camera camera, IScene scene) {
		super.render(shader, camera, scene);
	}
	
	public boolean isCollideFatal() {
		return collideFatal;
	}
}
