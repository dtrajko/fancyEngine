package engine.tm.skybox;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.tm.entities.Camera;
import engine.tm.scene.Scene;

public class SkyboxRenderer {

	private SkyboxShader shader;

	public SkyboxRenderer(Matrix4f projectionMatrix) {
		shader = new SkyboxShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(IScene scene) {
		Camera camera = (Camera) ((Scene) scene).getCamera();
		Skybox skybox = ((Scene) scene).getSkybox();
		shader.start();
		shader.loadViewMatrix(camera);
		GL30.glBindVertexArray(skybox.getCube().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, skybox.getTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, skybox.getCube().getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
}
