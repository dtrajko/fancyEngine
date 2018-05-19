package game;

import engine.GameEngine;
import engine.IGameLogic;
import game2D.game.Game2D;
import game3D.game.Game3D;

public class Main {

	public static void main(String[] args) {
		try {
			boolean vSync = true;
			IGameLogic gameLogic = new Game();
			GameEngine gameEng = new GameEngine("Java / LWJGL3 / lwjglbook", 1280, 720, vSync, gameLogic);
			gameEng.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
