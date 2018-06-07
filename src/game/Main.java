package game;

import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;

public class Main {
	 
    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic = new Game();
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = false;
            GameEngine gameEng = new GameEngine("Java / LWJGL3 / lwjglbook", vSync, opts, gameLogic);
            gameEng.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
