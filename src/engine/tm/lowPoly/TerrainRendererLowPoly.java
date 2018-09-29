package engine.tm.lowPoly;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import engine.graph.ICamera;
import engine.tm.entities.LightDirectional;
import engine.tm.settings.WorldSettings;

/**
 * A simple renderer that renders terrains.
 * 
 * @author Karl
 *
 */
public class TerrainRendererLowPoly {
	
	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/lowPolyTerrainVertex.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/lowPolyTerrainFragment.glsl";

	private final TerrainShader shader;
	private final boolean hasIndices;

	/**
	 * @param shader
	 *            - The shader program used for rendering this terrain.
	 * @param usesIndices
	 *            - Indicates whether the terrain will be rendered with an index
	 *            buffer or not.
	 */
	public TerrainRendererLowPoly(boolean usesIndices) {
		this.shader = new TerrainShader(VERTEX_FILE, FRAGMENT_FILE);
		this.hasIndices = usesIndices;
	}

	/**
	 * Renders a terrain to the screen. If the terrain has an index buffer the
	 * glDrawElements is used. Otherwise glDrawArrays is used.
	 * 
	 * @param terrain
	 *            - The terrain to be rendered.
	 * @param camera
	 *            - The camera being used for rendering the terrain.
	 * @param light
	 *            - The light being used to iluminate the terrain.
	 * 
	 * @param clipPlane
	 *            - The equation of the clipping plane to be used when rendering
	 *            the terrain. The clipping planes cut off anything in the scene
	 *            that is rendered outside of the plane.
	 */
	public void render(TerrainLowPoly terrain, ICamera camera, LightDirectional light, Vector4f clipPlane) {
		prepare(terrain, camera, light, clipPlane);
		if (hasIndices) {
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		} else {
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, terrain.getVertexCount());
		}
		finish(terrain);
	}

	/**
	 * Used when the program closes. Deletes the shader program.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

	/**
	 * Starts the shader program and loads up any necessary uniform variables.
	 * 
	 * @param terrain
	 *            - The terrain to be rendered.
	 * @param camera
	 *            - The camera being used to render the scene.
	 * @param light
	 *            - The light in the scene.
	 * @param clipPlane
	 *            - The equation of the clipping plane to be used when rendering
	 *            the terrain. The clipping planes cut off anything in the scene
	 *            that is rendered outside of the plane.
	 */
	private void prepare(TerrainLowPoly terrain, ICamera camera, LightDirectional light, Vector4f clipPlane) {
		terrain.getVao().bind();
		shader.start();
		shader.plane.loadVec4(clipPlane);
		shader.lightBias.loadVec2(light.getLightBias());
		shader.lightDirection.loadVec3(light.getDirection());
		shader.lightColor.loadVec3(light.getColor().getVector());
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
	}

	/**
	 * End the rendering process by unbinding the VAO and stopping the shader
	 * program.
	 * 
	 * @param terrain
	 */
	private void finish(TerrainLowPoly terrain) {
		terrain.getVao().unbind();
		shader.stop();
	}

}
