package engine.tm.lens;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import engine.Window;
import engine.tm.ThinMatrixCamera;

public class FlareManager {

	private static final Vector2f CENTER_SCREEN = new Vector2f(0f, 0f);//center changed

	private final FlareTexture[] flareTextures;
	private final float spacing;

	private FlareRenderer renderer;

	public FlareManager(Window window, float spacing, FlareTexture... textures) {
		this.spacing = spacing;
		this.flareTextures = textures;
		this.renderer = new FlareRenderer(window);
	}

	public void render(Window window, ThinMatrixCamera camera, Vector3f sunWorldPos) {
		Vector2f sunCoords = convertToScreenSpace(sunWorldPos, camera.getViewMatrix(), camera.getProjectionMatrix());
		if(sunCoords == null){
			return;
		}
		Vector2f sunToCenter = CENTER_SCREEN.sub(sunCoords);
		float brightness = 1 - (sunToCenter.length() / 1.4f);//number doubled
		if(brightness > 0){
			calcFlarePositions(sunToCenter, sunCoords);
			renderer.render(window, sunCoords, flareTextures, brightness);
		}
	}

	private void calcFlarePositions(Vector2f sunToCenter, Vector2f sunCoords){
		for(int i=0;i<flareTextures.length;i++){
			Vector2f direction = new Vector2f(sunToCenter);
			direction.normalize(i * spacing);
			Vector2f flarePos = sunCoords.add(direction);
			flareTextures[i].setScreenPos(flarePos);
		}
	}

	private Vector2f convertToScreenSpace(Vector3f worldPos, Matrix4f viewMat, Matrix4f projectionMat) {
		Vector4f coords = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f);
		coords = viewMat.transform(coords);		
		coords = projectionMat.transform(coords);
		if (coords.w <= 0) {
			return null;
		}
		//no need for conversion below
		return new Vector2f(coords.x / coords.w, coords.y / coords.w);
	}

	public void cleanUp() {
		renderer.cleanUp();
	}

}
