package engine.tm.terrains;

import org.joml.Matrix4f;

import engine.tm.shaders.ShaderProgram;
import engine.tm.shaders.UniformMatrix;
import engine.tm.shaders.UniformVec2;
import engine.tm.shaders.UniformVec3;
import engine.tm.shaders.UniformVec4;

/**
 * Represents the shader program that is used for rendering the terrain.
 * 
 * @author Karl
 *
 */
public class TerrainShader extends ShaderProgram {

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformVec3 lightColour = new UniformVec3("lightColour");
	protected UniformVec2 lightBias = new UniformVec2("lightBias");
	protected UniformVec4 plane = new UniformVec4("plane");

	private int location_transformationMatrix;
	private int location_toShadowMapSpace;
	private int location_shineDamper;
	private int location_reflectivity;

	public TerrainShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
		super.storeAllUniformLocations(projectionViewMatrix, lightDirection, lightColour, lightBias, plane);
	}

	public TerrainShader(String vertexFile, String geometryFile, String fragmentFile) {
		super(vertexFile, geometryFile, fragmentFile, true);
		super.storeAllUniformLocations(projectionViewMatrix, lightDirection, lightColour, lightBias, plane);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadToShadowSpaceMatrix(Matrix4f matrix) {
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
}
