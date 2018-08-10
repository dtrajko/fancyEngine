package game2D.entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

	public Vector3f position;
	public Vector3f scale;
	public Vector3f rotation;
	
	public Transform() {
		position = new Vector3f();
		scale = new Vector3f(1, 1, 1);
		rotation = new Vector3f(0, 0, 0);
	}

	public Transform(Vector3f position, float scale) {
		this.position = position;
		this.scale = new Vector3f(scale, scale, 1);
		this.rotation = new Vector3f(0, 0, 0);
	}

	public Transform(Vector3f position, Vector3f scale) {
		this.position = position;
		this.scale = scale;
		this.rotation = new Vector3f(0, 0, 0);
	}

	public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		this.position = position;
		this.scale = scale;
		this.rotation = rotation;
	}

	public Matrix4f getProjection(Matrix4f target) {
		target.translate(position, target);		
		target.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
		target.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
		target.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
		target.scale(scale);
		return target;
	}	
}
