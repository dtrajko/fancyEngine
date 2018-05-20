package game;

import java.io.File;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import config.Config;
import engine.Utils;
import engine.Window;
import graph.Mesh;
import graph.ShaderProgram;

public class Renderer {

	private ShaderProgram shaderProgram;

    public Renderer() {}

	public void init() throws Exception {
        shaderProgram = new ShaderProgram();        
        shaderProgram.createVertexShader(Utils.readFile(Config.RESOURCES_DIR + "/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.readFile(Config.RESOURCES_DIR + "/fragment.fs"));
        shaderProgram.link();
	}

	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

    public void render(Window window, Mesh mesh) {
        clear();

        if ( window.isResized() ) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Draw the mesh
        GL30.glBindVertexArray(mesh.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // Restore state
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
