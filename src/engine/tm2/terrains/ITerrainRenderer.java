package engine.tm2.terrains;

import org.joml.Vector4f;
import engine.graph.ICamera;
import engine.tm2.terrains.ITerrain;
import engine.tm2.utils.Light;

public interface ITerrainRenderer {

	void render(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane);
	void cleanUp();
}
