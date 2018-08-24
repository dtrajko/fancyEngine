package engine.tm2.lens;

import engine.tm2.settings.WorldSettings;
import engine.tm2.shaders.ShaderProgram;
import engine.tm2.shaders.UniformFloat;
import engine.tm2.shaders.UniformSampler;
import engine.tm2.shaders.UniformVec4;

/**
 * Sets up the shader program for the rendering the lens flare. It gets the
 * locations of the 3 uniform variables, links the "in_position" variable to
 * attribute 0 of the VAO, and connects the sampler uniform to texture unit 0.
 * 
 * @author Karl
 *
 */
public class FlareShader extends ShaderProgram {

	private static final String VERTEX_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/flareVertex.glsl";
	private static final String FRAGMENT_SHADER = WorldSettings.RESOURCES_SUBDIR + "/shaders/flareFragment.glsl";

	protected UniformFloat brightness = new UniformFloat("brightness");
	protected UniformVec4 transform = new UniformVec4("transform");

	private UniformSampler flareTexture = new UniformSampler("flareTexture");

	public FlareShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER, "in_position");
		super.storeAllUniformLocations(brightness, flareTexture, transform);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		flareTexture.loadTexUnit(0);
		super.stop();
	}

}
