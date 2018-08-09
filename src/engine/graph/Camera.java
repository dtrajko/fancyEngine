package engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Window;

public class Camera {

	private final Vector3f position;
	private final Vector3f rotation;
	private Matrix4f viewMatrix;
	private Matrix4f orthoProjectionMatrix;
	public static final float HEIGHT = 2.8f;
	public float actualHeight = HEIGHT;

	public Camera() {
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
		viewMatrix = new Matrix4f();
	}

	public Camera(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}
	
	public void reset() {
		setRotation(0, 0, 0);
	}

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f updateViewMatrix() {
        return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
    }

	public Vector3f getPosition() {
		return position;
	}
	
	public void setActualHeight(float height) {
		this.actualHeight = height;
	}
	
	public float getActualHeight() {
		return this.actualHeight;
	}

	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public void setPosition(Vector3f pos) {
		setPosition(pos.x, pos.y, pos.z);
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(float x, float y, float z) {
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}

	public void setRotation(Vector3f rot) {
		setRotation(rot.x, rot.y, rot.z);
	}

	public Vector3f calculateNewPosition(float offsetX, float offsetY, float offsetZ) {
		Vector3f newPos = new Vector3f(position.x, position.y, position.z);
		if ( offsetZ != 0 ) {
			newPos.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
			newPos.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
		}
		if ( offsetX != 0) {
			newPos.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
			newPos.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
		}
		newPos.y += offsetY;		
		return newPos;
	}

	public void movePosition(float offsetX, float offsetY, float offsetZ) {
		Vector3f newPos = calculateNewPosition(offsetX, offsetY, offsetZ);
		position.x = newPos.x;
		position.y = newPos.y;
		position.z = newPos.z;
	}

	public void movePosition(Vector3f newPos) {
		position.x = newPos.x;
		position.y = newPos.y;
		position.z = newPos.z;
	}

	public void moveRotation(float offsetX, float offsetY, float offsetZ) {
		rotation.x += offsetX;
		rotation.y += offsetY;
		rotation.z += offsetZ;
	}

	public void setOrthoProjection(Window window) {
		orthoProjectionMatrix = new Matrix4f().setOrtho2D(
			-(window.getWidth() / 2),
			  window.getWidth() / 2,
			-(window.getHeight() / 2),
			  window.getHeight() / 2);
	}

	public void setOrthoProjection() {
		orthoProjectionMatrix = new Matrix4f().setOrtho2D(-1, 1, -1, 1);
	}

	public Matrix4f getOrthoProjection() {
		return orthoProjectionMatrix.translate(position, new Matrix4f());
	}
}
