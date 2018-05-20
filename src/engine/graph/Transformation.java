package engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.GameItem;

public class Transformation {

	private final Matrix4f projectionMatrix;
	private final Matrix4f worldMatrix;
	private final Matrix4f viewMatrix; // obsolete
	private final Matrix4f modelViewMatrix;
	
	public Transformation() {
		worldMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
		viewMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
	}
	
	public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
		float aspectRatio = width / height;
		projectionMatrix.identity();
		projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
		return projectionMatrix;
	}
	
	// obsolete, replaced by the model view matrix
	public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, float scale) {
		worldMatrix.identity();
		worldMatrix.translate(offset);
		worldMatrix.rotateX((float)Math.toRadians(rotation.x));
		worldMatrix.rotateY((float)Math.toRadians(rotation.y));
		worldMatrix.rotateZ((float)Math.toRadians(rotation.z));
		worldMatrix.scale(scale);
		return worldMatrix;
	}

	public Matrix4f getModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
		Vector3f rotation = gameItem.getRotation();
		modelViewMatrix.identity();
		modelViewMatrix.translate(gameItem.getPosition());
		modelViewMatrix.rotateX((float)Math.toRadians(-rotation.x));
		modelViewMatrix.rotateY((float)Math.toRadians(-rotation.y));
		modelViewMatrix.rotateZ((float)Math.toRadians(-rotation.z));
		modelViewMatrix.scale(gameItem.getScale());
		Matrix4f viewCurr = new Matrix4f(viewMatrix);
		return viewCurr.mul(modelViewMatrix);
	}

	public Matrix4f getViewMatrix(Camera camera) {

		Vector3f cameraPos = camera.getPosition();
		Vector3f rotation = camera.getRotation();

		viewMatrix.identity();

		// First do the rotation so camera rotates over its position
		viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));

		// Then do the translation
		viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		return viewMatrix;
	}
}
