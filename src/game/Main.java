package game;

import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;
import game2D.frogger.Frogger;

public class Main {

	private static int width = 0;
	private static int height = 0;
	private static String app = "2D"; // 3D, 2D, Frogger

    public static void main(String[] args) {
        try {
            boolean vSync = false;
            IGameLogic gameLogic;
            GameEngine gameEng;

            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;
            
			switch (app) {
			case "3D":
				gameLogic = new Game3D();
				opts.mode3D = true;
				break;
			case "2D":
				gameLogic = new Game2D();
				opts.mode3D = false;
				break;
			case "Frogger":
				gameLogic = new Frogger();
				opts.mode3D = false;
				break;
			default:
				gameLogic = new Game3D();
				opts.mode3D = true;
            }

        	gameEng = new GameEngine("Java / LWJGL3 / lwjglbook", width, height, vSync, opts, gameLogic);
        	gameEng.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
