package engine.gui.fonts;

import java.util.List;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import config.Config;
import engine.Utils;
import engine.graph.ShaderProgram;
import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;

public class FontRenderer {
	
	private ShaderProgram shader;
	
	public FontRenderer() {
		shader = new ShaderProgram();
    	shader.createVertexShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/font_vertex.vs"));
    	shader.createFragmentShader(Utils.loadResource(Config.RESOURCES_DIR + "/shaders/font_fragment.fs"));
    	shader.link();
    	shader.createUniform("color");
    	shader.createUniform("translation");
	}

	public void render(Map<FontType, List<GUIText>> guiTextElements) {
		prepare();
		for (FontType font: guiTextElements.keySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for (GUIText text: guiTextElements.get(font)) {
				renderText(text);
			}
		}
		endRendering();
	}

	private void prepare(){
		shader.bind();
	}

	private void endRendering(){
		shader.unbind();
	}

	private void renderText(GUIText text){
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadColor(text.getColor());
		shader.loadTranslation(text.getPosition());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	public void cleanUp() {
		shader.cleanup();
	}
}
