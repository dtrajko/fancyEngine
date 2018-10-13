package engine.interfaces;

import engine.Window;
import engine.graph.Input;

public interface IGameLogic {

    void init(Window window) throws Exception;
    void input(Window window, Input input);
    void update(float interval, Input input);
    void render(Window window);
	Window getWindow();
	Input getInput();
	void cleanUp();
}
