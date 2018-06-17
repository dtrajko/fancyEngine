package game2D.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera2D {

	private Vector3f position;
	private Matrix4f orthoProjectionMatrix;

	public Camera2D() {
		position = new Vector3f(0, 0, 0);
	}

	public void setProjection(int width, int height) {
		orthoProjectionMatrix = new Matrix4f().setOrtho2D(-width/2, width/2, -height/2, height/2);
	}

	public void setPosition(Vector3f position) {
		this.position.set(position);
	}

	public void addPosition(Vector3f position) {
		this.position.add(position);
	}

	public Vector3f getPosition() {
		return this.position;
	}

	public Matrix4f getUntransformedProjection() {
		return orthoProjectionMatrix;
	}

	public Matrix4f getProjection() {
		return orthoProjectionMatrix.translate(position, new Matrix4f());
	}
}
