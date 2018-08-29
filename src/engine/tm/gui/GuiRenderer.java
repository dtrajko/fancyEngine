package engine.tm.gui;

import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.IScene;
import engine.tm.loaders.Loader;
import engine.tm.models.RawModel;
import engine.tm.scene.Scene;
import engine.tm.toolbox.Maths;

public class GuiRenderer {

	private final RawModel quad;
	private GuiShader shader;
	private Loader loader;

	public GuiRenderer() {
		loader = new Loader();
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1};
		this.quad = loader.loadGuiToVAO(positions);
		shader = new GuiShader();
	}

	public void render(IScene scene) {
		List<GuiTexture> guis = ((Scene) scene).getGuiElements();
		shader.start();
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		for (GuiTexture gui:guis) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
			Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
			shader.loadTransformation(matrix);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.start();
	}

	public void cleanUp() {
		shader.cleanUp();
	}
}
