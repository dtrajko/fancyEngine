package engine;

import engine.IGameLogic;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.Input;

public interface IScene {

	void update(float interval, Input input);
	void resetScene(Window window, ICamera camera, IGameLogic game);
	void save();
	void cleanup();
	ICamera getCamera();
}
