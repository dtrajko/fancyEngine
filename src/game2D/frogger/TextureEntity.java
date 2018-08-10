package game2D.frogger;

import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import game2D.entities.Entity;
import game2D.entities.Transform;
import game2D.shaders.Shader;
import game2D.textures.Texture;
import game2D.world.IScene;

public class TextureEntity extends Entity {

	private Texture texture;

	public TextureEntity(Transform transform, Texture tx) {
		super(transform);
		this.texture = tx;
	}

	@Override
	public void update(float delta, Window window, Camera camera, IScene scene, IGameLogic game) {
	}
	
	public void render(Shader shader, Camera camera, IScene scene) {
		texture.bind(0);
		super.render(shader, camera, scene);
	}
}
