package engine.interfaces;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ICamera {

	Matrix4f getViewMatrix();
	Vector3f getPosition();
	Vector3f getRotation();
	Matrix4f updateViewMatrix();
	Matrix4f getProjectionViewMatrix();

}