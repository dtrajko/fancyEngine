package engine.tm.shadows;

import org.joml.Matrix4f;
import engine.tm.settings.WorldSettings;
import engine.tm.shaders.ShaderProgram;

public class ShadowShader extends ShaderProgram {

	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/shadowVertexShader.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/shadowFragmentShader.glsl";

	private int location_mvpMatrix;

	protected ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_mvpMatrix = super.getUniformLocation("mvpMatrix");
		
	}
	
	protected void loadMvpMatrix(Matrix4f mvpMatrix) {
		super.loadMatrix(location_mvpMatrix, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
	}

}
