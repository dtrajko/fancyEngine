package engine.tm.lowPoly;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import engine.interfaces.ICamera;
import engine.interfaces.IScene;
import engine.interfaces.ITerrain;
import engine.tm.entities.LightDirectional;
import engine.tm.scene.SceneLowPoly;
import engine.tm.settings.WorldSettings;
import engine.tm.shadows.ShadowBox;
import engine.tm.shadows.ShadowMapMasterRenderer;
import engine.tm.toolbox.Maths;
import engine.utils.Util;

/**
 * A simple renderer that renders terrains.
 * 
 * @author Karl
 *
 */
public class TerrainRendererLowPoly {
	
	private static final String VERTEX_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/lowPolyTerrainVertex.glsl";
	private static final String FRAGMENT_FILE = WorldSettings.RESOURCES_SUBDIR + "/shaders/lowPolyTerrainFragment.glsl";

	private final TerrainShaderLowPoly shader;
	private final boolean hasIndices;

	/**
	 * @param shader
	 *            - The shader program used for rendering this terrain.
	 * @param usesIndices
	 *            - Indicates whether the terrain will be rendered with an index
	 *            buffer or not.
	 */
	public TerrainRendererLowPoly(Matrix4f projectionMatrix, boolean usesIndices) {
		shader = new TerrainShaderLowPoly(VERTEX_FILE, FRAGMENT_FILE);
		shader.start();
		shader.projectionMatrix.loadMatrix(projectionMatrix);
		shader.shadowDistance.loadFloat(ShadowBox.SHADOW_DISTANCE);
		shader.shadowMapSize.loadFloat(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
		shader.connectTextureUnits();
		shader.stop();
		hasIndices = usesIndices;
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
	public void render(IScene scene, LightDirectional light, Vector4f clipPlane, Matrix4f toShadowMapSpace) {
		
		if (toShadowMapSpace == null) {
			toShadowMapSpace = new Matrix4f();
			toShadowMapSpace.identity();
		}

		List<ITerrain> terrains = ((SceneLowPoly) scene).getTerrains();
		ICamera camera = scene.getCamera();
		for (ITerrain terrain : terrains) {
			prepare(terrain, camera, light, clipPlane, toShadowMapSpace);
			if (hasIndices) {
				GL11.glDrawElements(GL11.GL_TRIANGLES, ((TerrainLowPoly) terrain).getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			} else {
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, ((TerrainLowPoly) terrain).getVertexCount());
			}
			finish(terrain);			
		}
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
	private void prepare(ITerrain terrain, ICamera camera, LightDirectional light, Vector4f clipPlane, Matrix4f toShadowMapSpace) {
		((TerrainLowPoly) terrain).getVao().bind();
		shader.start();
		shader.clipPlane.loadVec4(clipPlane);
		shader.lightBias.loadVec2(light.getLightBias());
		shader.lightDirection.loadVec3(light.getDirection());
		shader.lightColor.loadVec3(light.getColor().getVector());
		loadModelMatrix(terrain);
		shader.viewMatrix.loadMatrix(camera.getViewMatrix());
		shader.toShadowMapSpace.loadMatrix(toShadowMapSpace);
	}

	private void loadModelMatrix(ITerrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
			new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.transformationMatrix.loadMatrix(transformationMatrix);
	}

	/**
	 * End the rendering process by unbinding the VAO and stopping the shader
	 * program.
	 * 
	 * @param terrain
	 */
	private void finish(ITerrain terrain) {
		((TerrainLowPoly) terrain).getVao().unbind();
		shader.stop();
	}

}
