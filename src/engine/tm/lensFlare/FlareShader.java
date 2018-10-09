package engine.tm.lensFlare;

import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;
import engine.tm.shaders.UniformFloat;
import engine.tm.shaders.UniformSampler;
import engine.tm.shaders.UniformVec4;

/**
 * Sets up the shader program for the rendering the lens flare. It gets the
 * locations of the 3 uniform variables, links the "in_position" variable to
 * attribute 0 of the VAO, and connects the sampler uniform to texture unit 0.
 * 
 * @author Karl
 *
 */
public class FlareShader extends ShaderProgram {

	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/flareVertex.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/flareFragment.glsl";

	protected UniformFloat brightness = new UniformFloat("brightness");
	protected UniformVec4 transform = new UniformVec4("transform");
	private UniformSampler flareTexture = new UniformSampler("flareTexture");

	public FlareShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, "in_position");
		super.storeAllUniformLocations(brightness, flareTexture, transform);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		flareTexture.loadTexUnit(0);
		super.stop();
	}
}
