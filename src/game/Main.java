package game;

import engine.GameEngine;
import engine.IGameLogic;

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
