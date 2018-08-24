package engine;

import engine.IGameLogic;
import engine.Window;
import engine.graph.ICamera;

public interface IScene {

	void update(float interval);
	void resetScene(Window window, ICamera camera, IGameLogic game);
	void save();
	void cleanup();
	ICamera getCamera();
}
