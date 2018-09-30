package engine.interfaces;

import engine.Window;
import engine.graph.Input;
import engine.sound.SoundManager;

public interface IGameLogic {

    void init(Window window) throws Exception;
    void input(Window window, Input input);
    void update(float interval, Input input);
    void render(Window window);
	Window getWindow();
	Input getInput();
	SoundManager getSoundManager();
	void initGui();
	void cleanUp();
}
