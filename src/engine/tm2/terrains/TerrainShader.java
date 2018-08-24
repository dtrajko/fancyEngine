package engine.tm2.terrains;

import org.joml.Matrix4f;
import engine.tm2.shaders.ShaderProgram;
import engine.tm2.shaders.UniformVec2;
import engine.tm2.shaders.UniformVec3;
import engine.tm2.shaders.UniformVec4;

/**
 * Represents the shader program that is used for rendering the terrain.
 * 
 * @author Karl
 *
 */
public class TerrainShader extends ShaderProgram {

	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformVec3 lightColor = new UniformVec3("lightColor");
	protected UniformVec2 lightBias = new UniformVec2("lightBias");
	protected UniformVec4 plane = new UniformVec4("plane");

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_toShadowMapSpace;
	private int location_shineDamper;
	private int location_reflectivity;

	public TerrainShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
		super.storeAllUniformLocations(lightDirection, lightColor, lightBias, plane);
	}

	public TerrainShader(String vertexFile, String geometryFile, String fragmentFile) {
		super(vertexFile, geometryFile, fragmentFile, true);
		super.storeAllUniformLocations(lightDirection, lightColor, lightBias, plane);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		// System.out.println("TerrainShader transformationMatrix: " + matrix);
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		// System.out.println("TerrainShader projectionMatrix: " + matrix);
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Matrix4f viewMatrix) {
		// System.out.println("TerrainShader viewMatrix: " + viewMatrix);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadToShadowSpaceMatrix(Matrix4f matrix) {
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

}
