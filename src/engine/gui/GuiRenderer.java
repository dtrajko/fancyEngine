package engine.gui;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import config.Config;
import engine.Utils;
import engine.Window;
import engine.graph.ShaderProgram;
import engine.graph.Transformation;
import engine.models.RawModel;
import engine.utils.Maths;

public class GuiRenderer {

	private final RawModel quad;
	private ShaderProgram guiShaderProgram;
	private final Transformation transformation;
	private GuiLoader loader;

	public GuiRenderer() {
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1};
		loader = new GuiLoader();
		this.quad = loader.loadToVAO(positions);
		transformation = new Transformation();
	}

	public void setupGuiShader() throws Exception {
    	guiShaderProgram = new ShaderProgram();
    	guiShaderProgram.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/gui_vertex.vs"));
    	guiShaderProgram.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/gui_fragment.fs"));
    	guiShaderProgram.link();

        // Create uniforms for orthographic-model transformationMatrix and GUI texture
    	guiShaderProgram.createUniform("transformationMatrix");
    	guiShaderProgram.createUniform("guiTexture");
    }

	public void render(List<GuiTexture> guis, Window window) {
		guiShaderProgram.bind();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (GuiTexture gui:guis) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			Matrix4f orthoMatrix = Maths.createTransformationMatrix(gui.getPosition(), new Vector3f(), gui.getScale());
			guiShaderProgram.setUniform("transformationMatrix", orthoMatrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		guiShaderProgram.unbind();
	}

	public void cleanUp() {
		guiShaderProgram.cleanup();
	}
}
