package engine.tm;

import org.lwjgl.opengl.GL11;

import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.graph.Transformation;

public class ThinMatrixRenderer implements IRenderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;
    private final Transformation transformation;

	public ThinMatrixRenderer() {
		transformation = new Transformation();
	}

	@Override
	public void init(Window window, IScene scene) {
        setupSkyBoxShader();
        setupSceneShader();
        setupParticlesShader();
	}

	private void setupParticlesShader() {
		// TODO Auto-generated method stub
	}

	private void setupSceneShader() {
		// TODO Auto-generated method stub
	}

	private void setupSkyBoxShader() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(Window window, ICamera camera, IScene scene, boolean sceneChanged) {
		clear();
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection matrix once per render cycle
        window.updateProjectionMatrix();

        // Update projection and view matrices once per render cycle
        transformation.updateProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        transformation.updateViewMatrix(camera);
	}

	@Override
	public void clear() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

}
