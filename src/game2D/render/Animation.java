package game2D.render;

import game2D.io.Timer;
import game2D.textures.Texture;

public class Animation {
	
	private Texture[] frames;
	private int pointer;
	private double elapsedTime;
	private double currentTime;
	private double lastTime;
	private double fps;

	public Animation(int amount, int fps, String filename) {
		this.pointer = 0;
		this.elapsedTime = 0;
		this.currentTime = 0;
		this.lastTime = Timer.getTime();
		this.fps = 1.0 / (double) fps;
		this.frames = new Texture[amount];
		for (int i = 0; i < amount; i++) {
			this.frames[i] = new Texture(filename + "/" + i);
		}
	}

	public void bind() { bind(0); }

	public void bind(int sampler) {
		this.currentTime = Timer.getTime();
		this.elapsedTime += this.currentTime - this.lastTime;
		
		if (this.elapsedTime >= fps) {
			this.elapsedTime = 0;
			this.pointer++;
		}

		if (this.pointer >= this.frames.length) {
			this.pointer = 0;
		}

		this.lastTime = this.currentTime;
		
		this.frames[this.pointer].bind(sampler);
	}
	
}
