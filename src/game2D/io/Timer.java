package game2D.io;

public class Timer {

	public Timer() {
		
	}

	public static double getTime() {
		return (double) System.nanoTime() / (double) 1000000000L;
	}
}
