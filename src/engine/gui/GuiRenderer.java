package engine.gui;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import config.Config;
import engine.Utils;
import engine.Window;
import engine.graph.ShaderProgram;
import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;
import engine.utils.Maths;

public class GuiRenderer {

	private ShaderProgram shader;
	private final RawModel quad;
	private Loader loader;

	public GuiRenderer() {
		loader = new Loader();
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1};
		this.quad = loader.loadToVAO(positions, 2);
		shader = new ShaderProgram();
		shader.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/gui_vertex.vs"));
		shader.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/gui_fragment.fs"));
		shader.link();
		// Create uniforms for orthographic-model transformationMatrix and GUI texture
		shader.createUniform("transformationMatrix");
		shader.createUniform("guiTexture");
		shader.createUniform("mouseOver");
	}

	public void render(GuiManager guiManager, Window window) {
		shader.bind();
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
			if (gui.isSplashBackground() && guiManager.areAllGuisClosed()) {
				continue;
			}
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			Matrix4f orthoMatrix = Maths.createTransformationMatrix(
				new Vector3f(gui.getGuiTexture().getPosition(), 0f),
				new Vector3f(gui.getGuiTexture().getRotation(), 0f),
				gui.getGuiTexture().getScale());
			shader.setUniform("transformationMatrix", orthoMatrix);
			int mouseOver = gui.isMouseOver() ? 1 : 0;
			shader.setUniform("mouseOver", mouseOver);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.unbind();
	}

	public void cleanUp() {
		shader.cleanup();
	}
}
