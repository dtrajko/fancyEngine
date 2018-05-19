package game3D.shaders;

import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glMatrixMode;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import game3D.render.Camera3D;
import game3D.utils.Maths;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "./res/shaders/shader_3d_vs.glsl";
	private static final String FRAGMENT_FILE = "./res/shaders/shader_3d_fs.glsl";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindFragOutput(0, "out_Color");
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		// System.out.println("StaticShader loadTransformationMatrix location=" + location_transformationMatrix + " matrix=" + matrix);
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadViewMatrix(Camera3D camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		// System.out.println("StaticShader loadViewMatrix location=" + location_viewMatrix + " matrix=" + viewMatrix);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		// System.out.println("StaticShader loadProjectionMatrix location=" + location_projectionMatrix + " matrix=" + matrix);
		super.loadMatrix(location_projectionMatrix, matrix);
	}
}
