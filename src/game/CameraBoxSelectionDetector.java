package game;

import java.util.List;
import java.util.Map;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import engine.IScene;
import engine.Scene;
import engine.graph.Camera;
import engine.graph.InstancedMesh;
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

    public void selectGameItem(IScene scene, Camera camera, MouseInput mouseInput, Mesh nextMesh) {
    	
    	GameItem selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;
        dir = camera.getViewMatrix().positiveZ(dir).negate();

        Map<Mesh, List<GameItem>> meshMap = ((Scene) scene).getGameMeshes();
        for (Mesh mesh : meshMap.keySet()) {
        	for (GameItem gameItem : meshMap.get(mesh)) {
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
        Map<InstancedMesh, List<GameItem>> instancedMeshMap = ((Scene) scene).getGameInstancedMeshes();
        for (Mesh mesh : instancedMeshMap.keySet()) {
        	for (GameItem gameItem : instancedMeshMap.get(mesh)) {
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
            	((Scene) scene).playSoundBreakingBlock();
            	((Scene) scene).removeGameItem(selectedGameItem);
            	((Scene) scene).generateBlockParticles(selectedGameItem, camera);
            }

            nextMesh = nextMesh != null ? nextMesh : selectedGameItem.getMesh();
            
            // middle button - create a new cube above the selected cube
            if (mouseInput.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_3)) {
            	
            	GameItem newGameItem = new GameItem(nextMesh);
            	newGameItem.setPosition(
        			selectedGameItem.getPosition().x,
        			selectedGameItem.getPosition().y + selectedGameItem.getScale() * 2,
        			selectedGameItem.getPosition().z);

            	if (!nextMesh.isSymetric()) {
            		int cornerAngle = nextMesh.isCorner() ? 45 : 0;
                	newGameItem.setRotationEulerDegrees(0, calculateItemOrientation(camera, cornerAngle), 0);	
            	}

            	newGameItem.setBoundingBox();
            	Vector3f pos = new Vector3f(newGameItem.getPosition().x + 1, newGameItem.getPosition().y + 1, newGameItem.getPosition().z + 1);
            	if (!((Scene) scene).inCollision(pos, false, camera)) {
            		((Scene) scene).appendGameItem(newGameItem);
            	}
            }
            // left button - create a new cube in camera direction
            if (mouseInput.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_2)) {

            	GameItem newGameItem = new GameItem(nextMesh);
            	Vector3f camPos = camera.getPosition();
            	Vector3f cubePos = selectedGameItem.getPosition();
            	float cubeSize = selectedGameItem.getScale() * 2;
            	float offsetX = 0;
            	float offsetY = 0;
            	float offsetZ = 0;
            	if (Math.abs(camPos.x - cubePos.x) > Math.abs(camPos.y - cubePos.y) &&
            		Math.abs(camPos.x - cubePos.x) > Math.abs(camPos.z - cubePos.z)) {
            		offsetX = (camPos.x > cubePos.x) ? cubeSize : -cubeSize;
                	offsetY = 0;
                	offsetZ = 0;
            	}
            	if (Math.abs(camPos.y - cubePos.y) > Math.abs(camPos.x - cubePos.x) &&
                	Math.abs(camPos.y - cubePos.y) > Math.abs(camPos.z - cubePos.z)) {
            		offsetX = 0;
            		offsetY = (camPos.y > cubePos.y) ? cubeSize : -cubeSize;
                	offsetZ = 0;
            	}
            	if (Math.abs(camPos.z - cubePos.z) > Math.abs(camPos.x - cubePos.x) &&
                	Math.abs(camPos.z - cubePos.z) > Math.abs(camPos.y - cubePos.y)) {
            		offsetX = 0;
            		offsetY = 0;
            		offsetZ = (camPos.z > cubePos.z) ? cubeSize : -cubeSize;
            	}
            	newGameItem.setPosition(
        			selectedGameItem.getPosition().x + offsetX,
        			selectedGameItem.getPosition().y + offsetY,
        			selectedGameItem.getPosition().z + offsetZ);

            	if (!nextMesh.isSymetric()) {
            		int cornerAngle = nextMesh.isCorner() ? 45 : 0;
                	newGameItem.setRotationEulerDegrees(0, calculateItemOrientation(camera, cornerAngle), 0);	
            	}

            	newGameItem.setBoundingBox();
            	Vector3f pos = new Vector3f(newGameItem.getPosition().x + 1, newGameItem.getPosition().y + 1, newGameItem.getPosition().z + 1);
            	if (!((Scene) scene).inCollision(pos, false, camera)) {
            		((Scene) scene).appendGameItem(newGameItem);
            	}
            }
        }
    }

    public float calculateItemOrientation(Camera camera, int cornerAngle) {
    	double yaw = camera.getRotation().y % 360;
    	yaw = yaw < 0 ? 360 + yaw : yaw;
    	float angleY = 0;
    	if(yaw >= 315 - cornerAngle || yaw < 45 - cornerAngle) {
    		angleY = 270;
    	} else if(yaw >= 45 - cornerAngle && yaw < 135 - cornerAngle) {
    		angleY = 180;
    	} else if(yaw >= 135 - cornerAngle && yaw < 225 - cornerAngle) {
    		angleY = 90;
    	} else if(yaw >= 225 - cornerAngle && yaw < 315 - cornerAngle) {
    		angleY = 0;
    	}
    	return angleY;
    }
}
