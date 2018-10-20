package game;

import engine.GameEngine;
import engine.Window;
import engine.helloWorld.HelloWorld;
import engine.interfaces.IGameLogic;
import engine.tm.ThinMatrix;
import engine.tm.ThinMatrixLowPoly;
import game2D.frogger.Frogger;

public class Main {

	private static int width = 0;
	private static int height = 0;
	private static String app = "HelloWorld"; // ThinMatrixLP, ThinMatrix, Game3D, Game2D, Frogger, HelloWorld

    public static void main(String[] args) {
        try {
            boolean vSync = false;
            IGameLogic gameLogic;
            GameEngine gameEng;
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;
            opts.capFps = true;

			switch (app) {
			case "ThinMatrixLP":
				gameLogic = new ThinMatrixLowPoly();
				opts.mode3D = true;
				opts.cullFace = true;
				opts.showTriangles = false;
				opts.antialiasing = true;
				width = height = 0;
				break;
			case "ThinMatrix":
				gameLogic = new ThinMatrix();
				opts.mode3D = true;
				opts.cullFace = true;
				opts.showTriangles = false;
				opts.antialiasing = true;
				width = height = 0;
				break;
			case "Game3D":
				gameLogic = new Game3D();
				opts.mode3D = true;
				opts.antialiasing = true;
				width = height = 0;
				break;
			case "HelloWorld":
				gameLogic = new HelloWorld();
				opts.mode3D = false;
				opts.cullFace = false;
				opts.antialiasing = true;
				opts.capFps = false;
				width = 1280;
				height = 720;
				break;
			case "Game2D":
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
