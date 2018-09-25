package engine.tm.sunRenderer;

import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;
import engine.tm.shaders.UniformMatrix;
import engine.tm.shaders.UniformSampler;

public class SunShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/sunVertex.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/sunFragment.glsl";

	protected UniformSampler sunTexture = new UniformSampler("sunTexture");
	protected UniformMatrix mvpMatrix = new UniformMatrix("mvpMatrix");

	public SunShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "in_position");
		super.storeAllUniformLocations(sunTexture, mvpMatrix);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		sunTexture.loadTexUnit(0);
		super.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void bindAttributes() {
		// TODO Auto-generated method stub
		
	}

}
