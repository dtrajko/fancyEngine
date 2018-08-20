package game;

import engine.GameEngine;
import engine.IGameLogic;
import engine.Window;
import engine.thinmatrix.ThinMatrix;
import game2D.frogger.Frogger;

public class Main {

	private static int width = 0;
	private static int height = 0;
	private static String app = "3D"; // 3D, 2D, Frogger, ThinMatrix

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
			case "ThinMatrix":
				gameLogic = new ThinMatrix();
				opts.mode3D = true;
				width = height = 0;
				break;
			case "3D":
				gameLogic = new Game3D();
				opts.mode3D = true;
				width = height = 0;
				break;
			case "2D":
				gameLogic = new Game2D();
				opts.mode3D = false;
				width = height = 0;
				break;
			case "Frogger":
				gameLogic = new Frogger();
				opts.mode3D = false;
				width = 750;
				height = 900;
				break;
			default:
				gameLogic = new Game3D();
				opts.mode3D = true;
				width = height = 0;
            }

        	gameEng = new GameEngine("Java / LWJGL3 / lwjglbook", width, height, vSync, opts, gameLogic);
        	gameEng.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
