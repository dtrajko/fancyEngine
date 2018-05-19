package game2D.io;

import engine.Window;

public class Window2D extends Window {

	private static Input input;

	public Window2D(String title, int width, int height, boolean vSync) {
		super(title, width, height, vSync);
	}

	public static Input getInput() { return input; }
	
	public void toggleFullscreen() {
		// TODO Auto-generated method stub
	}

}
