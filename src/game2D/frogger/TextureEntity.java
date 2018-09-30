package game2D.frogger;

import engine.Window;
import engine.graph.Camera;
import engine.interfaces.IGameLogic;
import engine.interfaces.IScene2D;
import game2D.entities.Entity;
import game2D.entities.Transform;
import game2D.shaders.Shader;
import game2D.textures.Texture;

public class TextureEntity extends Entity {

	protected Texture texture;

	public TextureEntity(Transform transform, Texture tx) {
		super(transform);
		this.texture = tx;
	}
	
	public Texture getTexture() {
		return this.texture;
	}

	public TextureEntity(Transform transform) {
		super(transform);
		this.texture = null;
	}

	@Override
	public void update(float delta, Window window, Camera camera, IScene2D scene, IGameLogic game) {
	}
	
	public void render(Shader shader, Camera camera, IScene2D scene) {
		if (this.texture instanceof Texture) {
			this.texture.bind(0);						
		}
		super.render(shader, camera, scene);
	}
}
