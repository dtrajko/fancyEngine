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
	private int speed;

	public Obstacle(Transform transform, Texture tx, int spd) {
		super(transform);
		this.texture = tx;
		this.speed = spd;
	}

	@Override
	public void update(float delta, Window window, Camera camera, IScene scene, IGameLogic game) {
		transform.position.x -= (float) speed / 20;
		if (transform.position.x < 0 - 2) {
			transform.position.x = window.getWidth() / scene.getScale();
		}
	}

	public void render(Shader shader, Camera camera, IScene scene) {
		texture.bind(0);
		super.render(shader, camera, scene);
	}

}
