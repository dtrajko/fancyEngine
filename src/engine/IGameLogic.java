package engine;

import engine.graph.MouseInput;
import engine.sound.SoundManager;

public interface IGameLogic {

    void init(Window window) throws Exception;
    void input(Window window, MouseInput mouseInput);
    void update(float interval, MouseInput mouseInput);
    void render(Window window);
	Window getWindow();
	MouseInput getInput();
	SoundManager getSoundManager();
	void initGui();
	void cleanup();
}
