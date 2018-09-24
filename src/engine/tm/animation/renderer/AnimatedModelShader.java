package engine.tm.animation.renderer;

import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;
import engine.tm.shaders.UniformMat4Array;
import engine.tm.shaders.UniformMatrix;
import engine.tm.shaders.UniformSampler;
import engine.tm.shaders.UniformVec3;
import engine.utils.MyFile;
import engine.utils.Util;

public class AnimatedModelShader extends ShaderProgram {

	private static final int MAX_JOINTS = 50; // max number of joints in a skeleton
	private static final int DIFFUSE_TEX_UNIT = 0;
	
	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/animatedEntityVertex.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/animatedEntityFragment.glsl";

	protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
	private UniformSampler diffuseMap = new UniformSampler("diffuseMap");

	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		super(VERTEX_FILE, FRAGMENT_FILE, new String[]{ "in_position", "in_textureCoords", "in_normal", "in_jointIndices", "in_weights" });
		super.storeAllUniformLocations(projectionViewMatrix, diffuseMap, lightDirection, jointTransforms, transformationMatrix);		
		connectTextureUnits();
	}

	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	private void connectTextureUnits() {
		super.start();
		diffuseMap.loadTexUnit(DIFFUSE_TEX_UNIT);
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
