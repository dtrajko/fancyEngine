package game2D.entities;

import engine.graph.Camera;
import game2D.frogger.TextureEntity;
import game2D.render.Animation;
import game2D.shaders.Shader;
import game2D.world.IScene;

public abstract class AnimatedEntity extends TextureEntity {

	protected Animation[] animations;
	private int max_animations;
	private int use_animation;

	public AnimatedEntity(int max_animations, Transform transform) {
		super(transform);
		this.max_animations = max_animations;
		this.animations = new Animation[this.max_animations];
		this.use_animation = 0;
	}

	public void setAnimation(int index, Animation animation) {
		try {
			this.animations[index] = animation;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Index is out of boundaries (max_animations=" + this.max_animations + ")");
			e.printStackTrace();
		}
	}
	
	public void useAnimation(int index) {
		this.use_animation = index;
	}

	public void render(Shader shader, Camera camera, IScene scene) {
		this.animations[this.use_animation].bind(0);
		super.render(shader, camera, scene);
	}
}
