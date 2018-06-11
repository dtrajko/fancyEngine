package engine.graph;

import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import engine.Scene;
import engine.items.GameItem;

public class Camera {

	private final Vector3f position;
	private final Vector3f rotation;
	private Matrix4f viewMatrix;
	public static final float HEIGHT = 1.0f;

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
		
		// System.out("Camera new position: " + newPos);
		
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

	public boolean inCollision(Scene scene, Vector3f newPos) {
		boolean inCollision = false;
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
    		for (GameItem gameItem : mapMeshes.get(mesh)) {
    			if (gameItem.getBoundingBox().contains(newPos.x, newPos.y, newPos.z)) {
    				inCollision = true;
    				break;
    			}
    		}        	
        }
        Map<InstancedMesh, List<GameItem>> mapInstancedMeshes = scene.getGameInstancedMeshes();
        for (Mesh mesh : mapInstancedMeshes.keySet()) {
    		for (GameItem gameItem : mapInstancedMeshes.get(mesh)) {
    			if (gameItem.getBoundingBox().contains(newPos.x, newPos.y, newPos.z)) {
    				inCollision = true;
    				break;
    			}
    		}        	
        }
		return inCollision;
	}
}
