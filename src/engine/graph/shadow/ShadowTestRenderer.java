package engine.graph.shadow;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import engine.Utils;
import engine.Window;
import engine.graph.Mesh;
import engine.graph.ShaderProgram;
import engine.loaders.obj.OBJLoader;

public class ShadowTestRenderer {

    private ShaderProgram testShaderProgram;

    private Mesh quadMesh;

    public void init(Window window) throws Exception {
        setupTestShader();
    }

    private void setupTestShader() throws Exception {
        testShaderProgram = new ShaderProgram();
        testShaderProgram.createVertexShader(Utils.loadResource("/shaders/test_vertex.vs"));
        testShaderProgram.createFragmentShader(Utils.loadResource("/shaders/test_fragment.fs"));
        testShaderProgram.link();

        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
            testShaderProgram.createUniform("texture_sampler[" + i + "]");
        }

        quadMesh = OBJLoader.loadMesh("/models/quad.obj");
    }

    public void renderTest(ShadowBuffer shadowMap) {
        testShaderProgram.bind();

        testShaderProgram.setUniform("texture_sampler[0]", 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getIds()[0]);

        quadMesh.render();

        testShaderProgram.unbind();
    }

    public void cleanup() {
        if (testShaderProgram != null) {
            testShaderProgram.cleanup();
        }
    }
}
