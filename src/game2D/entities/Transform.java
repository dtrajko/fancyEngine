package game2D.entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transform {

	public Vector3f position;
	public Vector3f scale;
	
	public Transform() {
		position = new Vector3f();
		scale = new Vector3f(1, 1, 1);
	}

	public Transform(Vector3f position, float scale) {
		this.position = position;
		this.scale = new Vector3f(scale, scale, scale);
	}

	public Transform(Vector3f position, Vector3f scale) {
		this.position = position;
		this.scale = scale;
	}

	public Matrix4f getProjection(Matrix4f target) {
		target.translate(position);
		target.scale(scale);
		return target;
	}	
}
