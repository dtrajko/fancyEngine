package engine.tm.toolbox;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.interfaces.ICamera;
import engine.tm.entities.Camera;

public class Maths {

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation);
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1));
		matrix.scale(new Vector3f(scale, scale, scale));
		return matrix;
	}

	// 2D, GUI
	public static Matrix4f createTransformationMatrix2D(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(new Vector3f(translation, 0f));
		matrix.scale(new Vector3f(scale, 1f));
		return matrix;
	}

	public static Matrix4f createViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(((Camera) camera).getPitch()), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(((Camera) camera).getYaw()),   new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(((Camera) camera).getRoll()),  new Vector3f(0, 0, 1));		
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);		
		viewMatrix.translate(negativeCameraPos);
		return viewMatrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static float clamp(float value, float min, float max){
		return Math.max(Math.min(value, max), min);
	}

	/**
	 * Calculates the normal of the triangle made from the 3 vertices. The vertices must be specified in counter-clockwise order.
	 * @param vertex0
	 * @param vertex1
	 * @param vertex2
	 * @return
	 */
	public static Vector3f calcNormal(Vector3f vertex0, Vector3f vertex1, Vector3f vertex2) {
		Vector3f tangentA = new Vector3f();
		Vector3f tangentB = new Vector3f();
		Vector3f normal = new Vector3f();
		vertex1.sub(vertex0, tangentA);
		vertex2.sub(vertex0, tangentB);
		tangentA.cross(tangentB, normal);
		normal.normalize();
		return normal;
	}

	public static void updateViewMatrix(Matrix4f viewMatrix, float x, float y, float z, float pitch, float yaw){
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(yaw),   new Vector3f(0, 1, 0));		
		Vector3f negativeCameraPos = new Vector3f(-x, -y, -z);
		viewMatrix.translate(negativeCameraPos);
	}
}
