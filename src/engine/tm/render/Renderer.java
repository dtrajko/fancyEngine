package engine.tm.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.IRenderer;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.scene.Scene;
import engine.tm.shaders.StaticShader;

public class Renderer implements IRenderer {

	private StaticShader shader;

	public Renderer() {
	}

	/**
	 * to be called in IGameLogic::update() (?)
	 */
	@Override
	public void prepare() {
		GL11.glClearColor(0, 0, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		shader.start();
	}

	@Override
	public void init(Window window, IScene scene) {
		shader = new StaticShader();
	}

	@Override
	public void render(Window window, ICamera camera, IScene scene, boolean sceneChanged) {
		clear();
		GL11.glViewport(0, 0, window.getWidth(), window.getHeight());

		prepare();
		renderModel(((Scene) scene).getTexturedModel());
	}

	private void renderModel(TexturedModel texturedModel) {
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE0, texturedModel.getTexture().getID());
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	}

	@Override
	public void clear() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
	}

	@Override
	public void cleanup() {
		shader.cleanUp();
	}

}
