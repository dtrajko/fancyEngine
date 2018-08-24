package engine.tm2.sun;

import engine.tm2.settings.WorldSettings;
import engine.tm2.shaders.ShaderProgram;
import engine.tm2.shaders.UniformMatrix;
import engine.tm2.shaders.UniformSampler;

public class SunShader extends ShaderProgram {

	private static final String VERTEX_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/sunVertex.glsl";
	private static final String FRAGMENT_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/sunFragment.glsl";
	
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
