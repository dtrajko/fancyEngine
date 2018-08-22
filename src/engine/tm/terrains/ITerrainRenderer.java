package engine.tm.terrains;

import org.joml.Vector4f;
import engine.graph.ICamera;
import engine.tm.terrains.ITerrain;
import engine.tm.utils.Light;

public interface ITerrainRenderer {

	void render(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane);
	void cleanUp();
}
