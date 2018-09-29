package engine.tm.lowPoly;

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

	protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformVec3 lightColor = new UniformVec3("lightColor");
	protected UniformVec2 lightBias = new UniformVec2("lightBias");
	protected UniformVec4 clipPlane = new UniformVec4("clipPlane");

	public TerrainShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, lightDirection, lightColor, lightBias, clipPlane);
	}

	public TerrainShader(String vertexFile, String geometryFile, String fragmentFile) {
		super(vertexFile, geometryFile, fragmentFile);
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, lightDirection, lightColor, lightBias, clipPlane);
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
