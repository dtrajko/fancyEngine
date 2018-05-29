package game;

import java.util.List;
import java.util.Map;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.Scene;
import engine.graph.Camera;
import engine.graph.Mesh;
import engine.graph.MouseInput;
import engine.items.GameItem;

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

    public void selectGameItem(Scene scene, Camera camera, MouseInput mouseInput) {

        GameItem selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;
        dir = camera.getViewMatrix().positiveZ(dir).negate();

        Map<Mesh, List<GameItem>> meshMap = scene.getGameMeshes();
        for (List<GameItem> gameItems : meshMap.values()) {
        	for (GameItem gameItem : gameItems) {
	            gameItem.setSelected(false);
	            min.set(gameItem.getPosition());
	            max.set(gameItem.getPosition());
	            min.sub(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
	            min.add(0.2f, 0.2f, 0.2f);
	            max.add(gameItem.getScale(), gameItem.getScale(), gameItem.getScale());
	            max.sub(0.2f, 0.2f, 0.2f);
	            if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, nearFar) && nearFar.x < closestDistance) {
	                closestDistance = nearFar.x;
	                selectedGameItem = gameItem;
	            }
        	}
        }
        if (selectedGameItem != null) {
        	selectedGameItem.setSelected(true);
            if (mouseInput.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
            	scene.removeGameItem(selectedGameItem);
            }
            if (mouseInput.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {
            	GameItem newGameItem = new GameItem(selectedGameItem.getMesh());
            	newGameItem.setPosition(
        			selectedGameItem.getPosition().x,
        			selectedGameItem.getPosition().y + selectedGameItem.getScale() * 2,
        			selectedGameItem.getPosition().z);
            	newGameItem.setBoundingBox();
            	scene.appendGameItem(newGameItem);
            }
        }
    }
}
