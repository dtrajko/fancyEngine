package engine;

import engine.graph.MouseInput;
import engine.sound.SoundManager;
import game2D.entities.Player;

public interface IGameLogic {

    void init(Window window) throws Exception;
    void input(Window window, MouseInput mouseInput);
    void update(float interval, MouseInput mouseInput);
    void render(Window window);
    void cleanup();
	int getCurrentLevel();
	void setLevel(int currentLevel);
	Window getWindow();
	MouseInput getInput();
	void setPlayer(Player player);
	SoundManager getSoundManager();
	void initGui();
}
