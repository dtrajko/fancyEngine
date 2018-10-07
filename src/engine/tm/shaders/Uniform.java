package engine.tm.shaders;

import org.lwjgl.opengl.GL20;

public abstract class Uniform {
	
	private static final int NOT_FOUND = -1;
	
	private String name;
	private int location;
	
	protected Uniform(String name){
		this.name = name;
	}
	
	protected void storeUniformLocation(int programID){
		location = GL20.glGetUniformLocation(programID, name);
		if(location == NOT_FOUND) {
			System.err.println("Uniform class: no uniform variable called " + name + " found!");
			new Exception().printStackTrace();
		}
	}
	
	protected int getLocation() {
		return location;
	}

}
