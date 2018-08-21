package engine.tm.sun;

import config.Config;
import engine.tm.shaders.ShaderProgram;
import engine.tm.shaders.UniformMatrix;
import engine.tm.shaders.UniformSampler;

public class SunShader extends ShaderProgram {

	private static final String VERTEX_SHADER = Config.RESOURCES_DIR + "/ThinMatrix/shaders/sunVertex.glsl";
	private static final String FRAGMENT_SHADER = Config.RESOURCES_DIR + "/ThinMatrix/shaders/sunFragment.glsl";
	
	protected UniformSampler sunTexture = new UniformSampler("sunTexture");
	protected UniformMatrix mvpMatrix = new UniformMatrix("mvpMatrix");

	public SunShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(sunTexture, mvpMatrix);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		sunTexture.loadTexUnit(0);
		super.stop();
	}

}
