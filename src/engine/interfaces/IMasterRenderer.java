package engine.interfaces;

import org.joml.Matrix4f;
import engine.Window;

public interface IMasterRenderer {

	public static float FOV = 70; // field of view angle
	public static float NEAR_PLANE = 1.0f;
	public static float FAR_PLANE = 3000;
	public static float RED = 0.832f;
	public static float GREEN = 0.961f;
	public static float BLUE = 0.996f;

	public void init(IScene scene);
	public Matrix4f getProjectionMatrix();
	public void prepare();
	public void render(Window window, IScene scene);
	public void cleanUp();

}
