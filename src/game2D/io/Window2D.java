package game2D.io;

import engine.Window;

public class Window2D extends Window {

	public Window2D(String title, int width, int height, boolean vSync, WindowOptions opts) {
		super(title, width, height, vSync, opts);
		// TODO Auto-generated constructor stub
	}

	private static Input input;


	public static Input getInput() { return input; }
	
	public void toggleFullscreen() {
		// TODO Auto-generated method stub
	}
}
