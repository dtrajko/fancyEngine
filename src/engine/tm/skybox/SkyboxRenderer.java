package engine.tm.skybox;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.GameEngine;
import engine.interfaces.ICamera;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IScene;
import engine.interfaces.ISkybox;

public class SkyboxRenderer {

	private SkyboxShader shader;

	private float time = 7000; // dawn

	public SkyboxRenderer(Matrix4f projectionMatrix) {
		shader = new SkyboxShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.loadFogColor(IMasterRenderer.RED, IMasterRenderer.GREEN, IMasterRenderer.BLUE);
		shader.stop();
	}

	public void render(IScene scene, Vector4f clipPlane) {
		ICamera camera = scene.getCamera();
		ISkybox skybox = scene.getSkybox();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadViewMatrix(camera);
		GL30.glBindVertexArray(skybox.getCube().getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures(scene);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, skybox.getCube().getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	private void bindTextures(IScene scene) {
		
		ISkybox skybox = scene.getSkybox();

		int texture1;
		int texture2;
		
		int skyboxTexture = skybox.getTexture();
		int skyboxTextureNight = skybox.getTextureNight();

		time += 1f / GameEngine.TARGET_UPS * 10;
		time %= 24000;
		float blendFactor;

		if (time >= 0 && time < 5000) {
			texture1 = skyboxTextureNight;
			texture2 = skyboxTextureNight;
			blendFactor = (time - 0) / (5000 - 0);
		} else if (time >= 5000 && time < 8000) {
			texture1 = skyboxTextureNight;
			texture2 = skyboxTexture;
			blendFactor = (time - 5000) / (8000 - 5000);
		} else if (time >= 8000 && time < 21000) {
			texture1 = skyboxTexture;
			texture2 = skyboxTexture;
			blendFactor = (time - 8000) / (21000 - 8000);
		} else {
			texture1 = skyboxTexture;
			texture2 = skyboxTextureNight;
			blendFactor = (time - 21000) / (24000 - 21000);
		}

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
		shader.loadBlendFactor(blendFactor);
	}

	public void cleanUp() {
		shader.cleanUp();
	}

}
