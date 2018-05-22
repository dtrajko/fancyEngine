package game;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.GameItem;
import engine.graph.Camera;
import engine.graph.Mesh;
import engine.graph.MouseInput;

public class CameraBoxSelectionDetector {

    private final Vector3f max;
    private final Vector3f min;
    private final Vector2f nearFar;
    private Vector3f dir;

    public CameraBoxSelectionDetector() {
        dir = new Vector3f();
        min = new Vector3f();
        max = new Vector3f();
        nearFar = new Vector2f();
    }

    public void selectGameItem(GameItem[] gameItems, Camera camera, MouseInput mouseInput) {
        GameItem selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        dir = camera.getViewMatrix().positiveZ(dir).negate();
        for (GameItem gameItem : gameItems) {
        	if (gameItem.getMesh() == null) {
        		continue;
        	}
            gameItem.setSelected(false);
            min.set(gameItem.getPosition());
            max.set(gameItem.getPosition());
            min.add(-gameItem.getScale(), -gameItem.getScale(), -gameItem.getScale());
            max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
            if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, nearFar) && nearFar.x < closestDistance) {
                closestDistance = nearFar.x;
                selectedGameItem = gameItem;
            }
        }
        if (selectedGameItem != null) {
        	selectedGameItem.setSelected(true);
            if (mouseInput.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            	selectedGameItem.setMesh(null);
            }
            if (mouseInput.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            	selectedGameItem.setPosition(
            			selectedGameItem.getPosition().x,
            			selectedGameItem.getPosition().y + selectedGameItem.getScale(),
            			selectedGameItem.getPosition().z);
            }
        }
    }
}
