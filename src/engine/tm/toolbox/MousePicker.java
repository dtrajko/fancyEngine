package engine.tm.toolbox;

import java.awt.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import engine.IScene;
import engine.graph.ICamera;
import engine.graph.Input;
import engine.tm.scene.Scene;
import engine.tm.terrains.ITerrain;

public class MousePicker {

	private static final int RECURSION_COUNT = 200;
	private static final float RAY_RANGE = 600;

	private Vector3f currentRay = new Vector3f();

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private IScene scene;
	private ICamera camera;
	private Vector3f currentTerrainPoint;

	public MousePicker(IScene scene, Matrix4f projectionMatrix) {
		this.scene = scene;
		this.camera = scene.getCamera();
		this.projectionMatrix = projectionMatrix;
		this.viewMatrix = Maths.createViewMatrix(camera);
	}

	public Vector3f getCurrentTerrainPoint() {
		return currentTerrainPoint;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}

	private ITerrain getTerrain(float worldX, float worldZ) {
		return scene.getCurrentTerrain(worldX, worldZ);
	}

	public boolean isDragEnabled(Input input) {
		return input.isKeyDown(GLFW.GLFW_KEY_Q) && input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_2);
	}

	public void update(Input input) {
		viewMatrix = Maths.createViewMatrix(camera);
		currentRay = calculateMouseRay(input);
		if (intersectionInRange(0, RAY_RANGE, currentRay)) {
			currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
		} else {
			currentTerrainPoint = null;
		}
	}

	private Vector3f calculateMouseRay(Input input) {
		float mouseX = (float) input.getMousePosition().x;
		float mouseY = (float) input.getMousePosition().y;
		Vector2f normalizedCoords = getNormalizedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, -1f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private Vector2f getNormalizedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2f * mouseX) / Window.WIDTH - 1f;
		float y = (2f * mouseY) / Window.HEIGHT - 1f;
		return new Vector2f(x, y);
	}

	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = projectionMatrix.invert();
		Vector4f eyeCoords = new Vector4f();
		invertedProjection.transform(clipCoords, eyeCoords);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = viewMatrix.invert();
		Vector4f rayWorld = new Vector4f();
		invertedView.transform(eyeCoords, rayWorld);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalize();
		return mouseRay;
	}

	private Vector3f getPointOnRay(Vector3f ray, float distance) {
		Vector3f camPos = camera.getPosition();
		Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
		Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
		return start.add(scaledRay);
	}

	private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
		float half = start + ((finish - start) / 2f);
		if (count >= RECURSION_COUNT) {
			Vector3f endPoint = getPointOnRay(ray, half);
			ITerrain terrain = getTerrain(endPoint.x, endPoint.z);
			if (terrain != null) {
				return endPoint;
			} else {
				return null;
			}
		}
		if (intersectionInRange(start, half, ray)) {
			return binarySearch(count + 1, start, half, ray);
		} else {
			return binarySearch(count + 1, half, finish, ray);
		}
	}

	private boolean intersectionInRange(float start, float finish, Vector3f ray) {
		Vector3f startPoint = getPointOnRay(ray, start);
		Vector3f endPoint = getPointOnRay(ray, finish);
		if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isUnderGround(Vector3f testPoint) {
		ITerrain terrain = getTerrain(testPoint.x, testPoint.z);
		float height = 0;
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(testPoint.x, testPoint.z);
		}
		if (testPoint.y < height) {
			return true;
		} else {
			return false;
		}
	}
}
