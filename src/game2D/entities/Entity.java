package game2D.entities;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.IGameLogic;
import engine.Window;
import engine.graph.Camera;
import game2D.assets.Assets;
import game2D.collision.AABB;
import game2D.collision.Collision;
import game2D.render.Animation;
import game2D.render.Model;
import game2D.shaders.Shader;
import game2D.world.World;

public abstract class Entity {

	protected static Model model;
	protected AABB bounding_box;
	// private Texture texture;
	protected Transform transform;
	protected Animation[] animations;
	private int max_animations;
	private int use_animation;

	public Entity(int max_animations, Transform transform) {
		this.max_animations = max_animations;
		this.animations = new Animation[this.max_animations];
		this.use_animation = 0;
		this.transform = transform;
		this.bounding_box = new AABB(
			new Vector2f(transform.position.x, transform.position.y), 
			new Vector2f(this.transform.scale.x, this.transform.scale.y));
	}

	public abstract void update(float delta, Window window, Camera camera, World world, IGameLogic game);

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

	public void move(Vector2f direction) {
		transform.position.add(new Vector3f(direction, 0));
		bounding_box.getCenter().set(transform.position.x, transform.position.y);
	}

	public void collideWithTiles(World world) {
		AABB[] boxes = new AABB[25];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				boxes[i + j * 5] = world.getTileBoundingBox(
					(int)((transform.position.x / 2 + 0.5f) - 5/2) + i,
					(int)((-transform.position.y / 2 + 0.5f) - 5/2) + j
				);
			}
		}
		AABB box = null;
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i] != null) {
				if (box == null) {
					box = boxes[i];
				}
				Vector2f length1 = box.getCenter().sub(transform.position.x, transform.position.y, new Vector2f());
				Vector2f length2 = boxes[i].getCenter().sub(transform.position.x, transform.position.y, new Vector2f());
				if (length1.lengthSquared() > length2.lengthSquared()) {
					box = boxes[i];
				}
			}
		}
		if (box != null) {
			Collision data = this.bounding_box.getCollision(box);
			if (data.isIntersecting) {
				bounding_box.correctPosition(box, data);
				transform.position.set(bounding_box.getCenter(), 0);
			}			
		}
	}

	public void collideWithEntity(Entity entity) {
		Collision collision = this.bounding_box.getCollision(entity.bounding_box);
		if (collision.isIntersecting) {

			collision.distance.x /= 2;
			collision.distance.y /= 2;

			this.bounding_box.correctPosition(entity.bounding_box, collision);
			this.transform.position.set(this.bounding_box.getCenter().x, this.bounding_box.getCenter().y, 0);
			
			entity.bounding_box.correctPosition(this.bounding_box, collision);
			entity.transform.position.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
		}
	}

	public void correctPosition(Window window, World world) {

		Vector3f pos = this.transform.position;

		if (pos.x < 0) {
			pos.x = 0;
		}
		if (pos.x > world.getWidth() * 2 - 2) {
			pos.x = world.getWidth() * 2 - 2;
		}
		if (pos.y > 0) {
			pos.y = 0;
		}
		if (pos.y < -world.getHeight() * 2 + 2) {
			pos.y = -world.getHeight() * 2 + 2;
		}
	}

	public void render(Shader shader, Camera camera, World world) {
		Matrix4f target = camera.getOrthoProjection();
		target.mul(world.getWorldMatrix());
		shader.bind();
		shader.setUniform("sampler", 0);
		shader.setUniform("projection", this.transform.getProjection(target));
		this.animations[this.use_animation].bind(0);
		Assets.getModel().render();
	}
}
