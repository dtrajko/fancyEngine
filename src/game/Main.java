package game;

import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;

public class Main {

	private static boolean mode3D = true;
	private static int width = 0;
	private static int height = 0;

    public static void main(String[] args) {
        try {
            boolean vSync = false;
            IGameLogic gameLogic;
            GameEngine gameEng;

            Window.WindowOptions opts = new Window.WindowOptions();
            opts.mode3D = mode3D;
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;

            gameLogic = opts.mode3D ? new Game() : new Game2D();
        	gameEng = new GameEngine("Java / LWJGL3 / lwjglbook", width, height, vSync, opts, gameLogic);
        	gameEng.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
