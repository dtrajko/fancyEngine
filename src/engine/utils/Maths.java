package engine.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.graph.ICamera;
import engine.tm.Camera;
import engine.tm2.ThinMatrixCamera;

public class Maths {

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation);
		matrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
		matrix.scale(new Vector3f(scale.x, scale.y, 1f));
		return matrix;
	}

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

	public static Matrix4f createViewMatrix(ICamera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float) Math.toRadians(camera.getPosition().x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(camera.getPosition().y),   new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(camera.getPosition().z),  new Vector3f(0, 0, 1));		
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

	/*
	 * returns a vector that contains larger of each component of input vectors
	 */
	public static Vector3f vectorMax3f(Vector3f v1, Vector3f v2) {
		return new Vector3f(
			Math.max(v1.x, v2.x),
			Math.max(v1.y, v2.y),
			Math.max(v1.z, v2.z)
		);
	}

	/*
	 * returns max component in a vector
	 */
	public static float vectorMaxComponent3f(Vector3f v) {
		float max = v.x;
		if (v.y > max) max = v.y;
		if (v.z > max) max = v.z;
		return max;
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
		Vector3f tangentA = vertex1.sub(vertex0);
		Vector3f tangentB = vertex2.sub(vertex0);
		Vector3f normal = tangentA.cross(tangentB);
		normal.normalize();
		return normal;
	}
}
