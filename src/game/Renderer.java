package game;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import config.Config;
import engine.GameItem;
import engine.Utils;
import engine.Window;
import engine.graph.Camera;
import engine.graph.ShaderProgram;
import engine.graph.Transformation;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;
	private ShaderProgram shaderProgram;

	public Renderer() {
        transformation = new Transformation();
    }

	public void init(Window window) throws Exception {

		// Create shader
        shaderProgram = new ShaderProgram();        
        shaderProgram.createVertexShader(Utils.readFile(Config.RESOURCES_DIR + "/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.readFile(Config.RESOURCES_DIR + "/fragment.fs"));
        shaderProgram.link();

        // Create uniforms for world and projection matrices
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix"); // ex worldMatrix
        shaderProgram.createUniform("texture_sampler");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void clear() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

    public void render(Window window, Camera camera, GameItem[] gameItems) {
        clear();

        if ( window.isResized() ) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();
        
        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(
        	FOV,
        	window.getWidth(),
        	window.getHeight(),
        	Z_NEAR,
        	Z_FAR
        );
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shaderProgram.setUniform("texture_sampler", 0);
        // Render each gameItem
        for(GameItem gameItem : gameItems) {

        	// Set model view matrix for this item
        	Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);

        	shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);

            // Render the mesh for this game item
            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
