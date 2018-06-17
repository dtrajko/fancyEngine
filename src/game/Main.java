package game;

import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;

public class Main {
	
	private static String mode = "3D"; // 2D/3D
	private static int width = 1280;
	private static int height = 720;

    public static void main(String[] args) {
        try {
            boolean vSync = true;
            IGameLogic gameLogic;
            GameEngine gameEng;

            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;

            if (mode == "3D") {
            	gameLogic = new Game();
            	gameEng = new GameEngine("Java / LWJGL3 / lwjglbook", vSync, opts, gameLogic);
            	gameEng.start();
            } else if (mode == "2D") {
            	gameLogic = new Game2D();
            	gameEng = new GameEngine("Java / LWJGL3 / lwjglbook", width, height, vSync, opts, gameLogic);
            	gameEng.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
