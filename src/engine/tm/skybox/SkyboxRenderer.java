package engine.tm.skybox;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import engine.graph.ICamera;
import engine.tm.ThinMatrixCamera;
import engine.tm.opengl.Vao;
import engine.tm.utils.OpenGlUtils;

public class SkyboxRenderer {

	private static final float SIZE = 200;

	private SkyboxShader shader;
	private Vao box;

	public SkyboxRenderer() {
		this.shader = new SkyboxShader();
		this.box = CubeGenerator.generateCube(SIZE);
	}

	public void render(ISkyBox skybox, ICamera camera) {
		prepare(skybox, camera);
		Vao model = ((ThinMatrixSkyBox) skybox).getCubeVao();
		model.bind(0);
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		model.unbind(0);
		finish();
	}

	/**
	 * Delete the shader when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

	/**
	 * Starts the shader, loads the projection-view matrix to the uniform
	 * variable, and sets some OpenGL state which should be mostly
	 * self-explanatory.
	 * 
	 * @param camera
	 *            - the scene's camera.
	 */
	private void prepare(ICamera camera) {
		shader.start();
		// shader.projectionViewMatrix.loadMatrix(((ThinMatrixCamera) camera).getProjectionViewMatrix());
		shader.projectionViewMatrix.loadMatrix(((ThinMatrixCamera) camera).getProjectionMatrix());
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.antialias(false);
	}

	private void prepare(ISkyBox skybox, ICamera camera) {		
		shader.start();
		GL11.glDepthMask(false);
		shader.projectionViewMatrix.loadMatrix(((ThinMatrixCamera) camera).getProjectionViewMatrix().translate(new Vector3f(0f, -50f, 0f)));
		shader.projectionViewMatrix.loadMatrix(((ThinMatrixCamera) camera).getProjectionMatrix().translate(new Vector3f(0f, -50f, 0f)));
		((ThinMatrixSkyBox) skybox).getTexture().bindToUnit(0);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.antialias(false);
	}

	private void finish() {
		GL11.glDepthMask(true);
		shader.stop();
	}	
}
