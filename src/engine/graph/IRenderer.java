package engine.graph;

import engine.IScene;
import engine.Window;

public interface IRenderer {

	void prepare();
	void init(Window window, IScene scene);
	void render(Window window, ICamera camera, IScene scene, boolean sceneChanged);
	void clear();
	void cleanup();
}
