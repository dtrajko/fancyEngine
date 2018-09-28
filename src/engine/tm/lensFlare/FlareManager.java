package engine.tm.lensFlare;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import engine.IScene;
import engine.graph.ICamera;
import engine.tm.entities.Camera;
import engine.tm.scene.Scene;
import engine.tm.sunRenderer.Sun;

public class FlareManager {

	private static final Vector2f CENTER_SCREEN = new Vector2f(0f, 0f); // center changed

	private final FlareTexture[] flareTextures;
	private final float spacing;

	private FlareRenderer renderer;

	public FlareManager(float spacing, FlareTexture... textures) {
		this.spacing = spacing;
		this.flareTextures = textures;
		this.renderer = new FlareRenderer();
	}

	public void render(IScene scene) {
		ICamera camera = scene.getCamera();
		Sun sun = (Sun) scene.getSun();
		Vector3f sunWorldPos = sun.getWorldPosition(camera.getPosition());
		Vector2f sunCoords = convertToScreenSpace(sunWorldPos, camera.getViewMatrix(), ((Camera) camera).getProjectionMatrix());
		if(sunCoords == null){
			return;
		}
		Vector2f sunToCenter = new Vector2f();
		CENTER_SCREEN.sub(sunCoords, sunToCenter);
		float brightness = 1 - (sunToCenter.length() / 1.0f); // number doubled
		if(brightness > 0) {
			calcFlarePositions(sunToCenter, sunCoords);
			renderer.render(sunCoords, flareTextures, brightness);
		}
	}

	private void calcFlarePositions(Vector2f sunToCenter, Vector2f sunCoords){
		for(int i = 0; i < flareTextures.length; i++){
			Vector2f direction = new Vector2f(sunToCenter);
			direction.normalize(i * spacing);
			Vector2f flarePos = new Vector2f();
			sunCoords.add(direction, flarePos);
			flareTextures[i].setScreenPos(flarePos);
		}
	}

	private Vector2f convertToScreenSpace(Vector3f worldPos, Matrix4f viewMat, Matrix4f projectionMat) {
		Vector4f coords = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f);
		viewMat.transform(coords, coords);
		projectionMat.transform(coords, coords);
		if (coords.w <= 0) {
			return null;
		}
		// no need for conversion below
		return new Vector2f(coords.x / coords.w, coords.y / coords.w);
	}

	public void cleanUp() {
		renderer.cleanUp();
	}

}
