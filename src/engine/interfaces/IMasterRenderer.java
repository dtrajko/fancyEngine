package engine.interfaces;

import org.joml.Matrix4f;

import engine.Window;

public interface IMasterRenderer {

	float FOV = 70; // field of view angle
	float NEAR_PLANE = 1.0f;
	float FAR_PLANE = 3000;
	float RED = 0.832f;
	float GREEN = 0.961f;
	float BLUE = 0.996f;

	void init(IScene scene);
	void render(Window window, IScene scene);
	void prepare();
	void cleanUp(IScene scene);
	Matrix4f getProjectionMatrix();

}
