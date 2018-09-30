package engine.tm.terrains;

import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.interfaces.ICamera;
import engine.interfaces.IMasterRenderer;
import engine.interfaces.IScene;
import engine.interfaces.ITerrain;
import engine.tm.entities.Light;
import engine.tm.models.RawModel;
import engine.tm.scene.Scene;
import engine.tm.shadows.ShadowBox;
import engine.tm.shadows.ShadowMapMasterRenderer;
import engine.tm.textures.TerrainTexturePack;
import engine.tm.toolbox.Maths;

public class TerrainRenderer {

	private TerrainShader shader;

	public TerrainRenderer(Matrix4f projectionMatrix) {
		this.shader = new TerrainShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadShadowDistance(ShadowBox.SHADOW_DISTANCE);
		shader.loadShadowMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(IScene scene, Vector4f clipPlane, Matrix4f toShadowMapSpace) {

		if (toShadowMapSpace == null) {
			toShadowMapSpace = new Matrix4f();
			toShadowMapSpace.identity();
		}

		List<Light> lights = ((Scene) scene).getLights();
		ICamera camera = ((Scene) scene).getCamera();
		List<ITerrain> terrains = ((Scene) scene).getTerrains();

		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(IMasterRenderer.RED, IMasterRenderer.GREEN, IMasterRenderer.BLUE);
		shader.loadLights(lights);
		shader.loadShineVariables(1, 0);
		shader.loadViewMatrix(camera);
		shader.loadToShadowMapSpaceMatrix(toShadowMapSpace);

		for (ITerrain terrain : terrains) {
			bindTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}

		shader.stop();
	}

	public void render(IScene scene) {
		render(scene, new Vector4f(0, 0, 0, 0));
	}

	public void render(IScene scene, Vector4f clipPlane) {
		Matrix4f toShadowMapSpace = new Matrix4f();
		toShadowMapSpace.identity();
		render(scene, clipPlane, toShadowMapSpace);
	}

	private void bindTerrain(ITerrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain);
	}

	private void bindTextures(ITerrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getRedTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getGreenTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBlueTexture().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
		unbindTextures();
	}

	private void unbindTextures() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	private void loadModelMatrix(ITerrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
			shader.loadTransformationMatrix(transformationMatrix);
	}

	public void cleanUp() {
		shader.cleanUp();
	}
}
