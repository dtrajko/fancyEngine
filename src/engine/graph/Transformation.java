package engine.graph;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.items.GameItem;

public class Transformation {

    private final Matrix4f projectionMatrix;
    private final Matrix4f modelMatrix;
    private final Matrix4f modelViewMatrix;
    private final Matrix4f modelLightViewMatrix;
    private final Matrix4f worldMatrix;
    private final Matrix4f viewMatrix; // obsolete
    private final Matrix4f lightViewMatrix;
    private final Matrix4f orthoMatrix;    
    private final Matrix4f orthoProjMatrix;
    private final Matrix4f ortho2DMatrix;
    private final Matrix4f orthoModelMatrix;

	public Transformation() {
        projectionMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        modelViewMatrix = new Matrix4f();
        modelLightViewMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        orthoMatrix = new Matrix4f();
        lightViewMatrix = new Matrix4f();
        orthoProjMatrix = new Matrix4f();
        ortho2DMatrix = new Matrix4f();
        orthoModelMatrix = new Matrix4f();
	}

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

	public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
		float aspectRatio = width / height;
		projectionMatrix.identity();
		projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
		return projectionMatrix;
	}

    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;        
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public final Matrix4f getOrtho2DProjectionMatrix(float left, float right, float bottom, float top) {
        return ortho2DMatrix.setOrtho2D(left, right, bottom, top);
    }

    public Matrix4f buildOrthoProjModelMatrix(GameItem gameItem, Matrix4f orthoMatrix) {
        return orthoMatrix.mulOrthoAffine(buildModelMatrix(gameItem), orthoModelMatrix);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public Matrix4f updateViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();
        
        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public final Matrix4f getOrthoProjectionMatrix(float left, float right, float bottom, float top) {
        orthoMatrix.identity();
        orthoMatrix.setOrtho2D(left, right, bottom, top);
        return orthoMatrix;
    }

    public Matrix4f buildModelMatrix(GameItem gameItem) {
        Quaternionf rotation = gameItem.getRotation();
        return modelMatrix.translationRotateScale(
                gameItem.getPosition().x, gameItem.getPosition().y, gameItem.getPosition().z,
                rotation.x, rotation.y, rotation.z, rotation.w,
                gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
    }

    public final Matrix4f getOrthoProjectionMatrix() {
        return orthoProjMatrix;
    }

    public Matrix4f buildModelViewMatrix(GameItem gameItem, Matrix4f viewMatrix) {
        Quaternionf rotation = gameItem.getRotation();
        modelMatrix.identity().translate(gameItem.getPosition()).
                rotateX((float)Math.toRadians(-rotation.x)).
                rotateY((float)Math.toRadians(-rotation.y)).
                rotateZ((float)Math.toRadians(-rotation.z)).
                scale(gameItem.getScale());
        modelViewMatrix.set(viewMatrix);
        return modelViewMatrix.mul(modelMatrix);
    }
    
    public Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        return viewMatrix.mulAffine(modelMatrix, modelViewMatrix);
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
		Quaternionf rotation = gameItem.getRotation();
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

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }

    public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear, float zFar) {
        return orthoProjMatrix.setOrtho(left, right, bottom, top, zNear, zFar);
    }

    public static  Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        // First do the rotation so camera rotates over its position
        matrix.rotationX((float)Math.toRadians(rotation.x));
        matrix.rotateY((float)Math.toRadians(rotation.y));
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public Matrix4f buildOrtoProjModelMatrix(GameItem gameItem, Matrix4f orthoMatrix) {
        Quaternionf rotation = gameItem.getRotation();
        modelMatrix.identity();
        modelMatrix.translate(gameItem.getPosition());
        modelMatrix.rotateX((float) Math.toRadians(-rotation.x));
        modelMatrix.rotateY((float) Math.toRadians(-rotation.y));
        modelMatrix.rotateZ((float) Math.toRadians(-rotation.z));
        modelMatrix.scale(gameItem.getScale());
        orthoModelMatrix.set(orthoMatrix);
        orthoModelMatrix.mul(modelMatrix);
        return orthoModelMatrix;
    }

    public Matrix4f buildModelLightViewMatrix(GameItem gameItem, Matrix4f lightViewMatrix) {
        return buildModelViewMatrix(buildModelMatrix(gameItem), lightViewMatrix);
    }

    public Matrix4f buildModelLightViewMatrix(Matrix4f modelMatrix, Matrix4f lightViewMatrix) {
        return lightViewMatrix.mulAffine(modelMatrix, modelLightViewMatrix);
    }

}
