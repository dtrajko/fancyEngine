package game2D.frogger;

import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import game2D.entities.Entity;
import game2D.entities.Transform;
import game2D.shaders.Shader;
import game2D.textures.Texture;
import game2D.world.IScene;

public class Obstacle extends Entity {

	private Texture texture;
	private float speed;
	private int maxLength = 6;

	public Obstacle(Transform transform, Texture tx, float spd) {
		super(transform);
		this.texture = tx;
		this.speed = spd;
	}

	@Override
	public void update(float delta, Window window, Camera camera, IScene scene, IGameLogic game) {
		transform.position.x -= speed;
		if (transform.position.x < 0 - maxLength) {
			transform.position.x = window.getWidth() / scene.getScale() + maxLength;
		}
	}

	public void render(Shader shader, Camera camera, IScene scene) {
		texture.bind(0);
		super.render(shader, camera, scene);
	}

}
