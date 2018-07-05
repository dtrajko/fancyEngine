package engine.gui;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import config.Config;
import engine.Scene;
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
    	guiShaderProgram.createUniform("mouseOver");
    }

	public void render(GuiManager guiManager, Window window) {
		guiShaderProgram.bind();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (GuiElement gui:guiManager.getGuiElements()) {
			if (gui.isInventory() && !guiManager.isInventoryOn()) {
				continue;
			}
			if (gui.isImportDialog() && !guiManager.getImportPopup().isEnabled()) {
				continue;
			}
			if (gui.isQuitPopup() && !guiManager.getQuitPopup().isEnabled()) {
				continue;
			}
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			Matrix4f orthoMatrix = Maths.createTransformationMatrix(gui.getGuiTexture().getPosition(), gui.getGuiTexture().getRotation(), gui.getGuiTexture().getScale());
			guiShaderProgram.setUniform("transformationMatrix", orthoMatrix);
			int mouseOver = gui.isMouseOver() ? 1 : 0;
			guiShaderProgram.setUniform("mouseOver", mouseOver);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		guiShaderProgram.unbind();
	}

	public void cleanUp() {
		guiShaderProgram.cleanup();
	}
}
