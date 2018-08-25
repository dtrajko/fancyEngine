package engine.tm.render;

import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import engine.IScene;
import engine.graph.ICamera;
import engine.tm.entities.Light;
import engine.tm.models.RawModel;
import engine.tm.scene.Scene;
import engine.tm.shaders.TerrainShader;
import engine.tm.terrains.Terrain;
import engine.tm.textures.ModelTexture;
import engine.tm.toolbox.Maths;

public class TerrainRenderer {

	private TerrainShader shader;

	public TerrainRenderer(Matrix4f projectionMatrix) {
		this.shader = new TerrainShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public void render(IScene scene) {

		Light light = ((Scene) scene).getLight();
		ICamera camera = ((Scene) scene).getCamera();
		List<Terrain> terrains = ((Scene) scene).getTerrains();

		shader.start();
		shader.loadLight(light);
		shader.loadViewMatrix(camera);

		for (Terrain terrain : terrains) {
			bindTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}

		shader.stop();
	}

	private void bindTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = terrain.getTexture();		
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
			shader.loadTransformationMatrix(transformationMatrix);
	}

	public void cleanUp() {
		shader.cleanUp();
	}
}
