package engine.thinmatrix.rendering;

import java.util.List;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import engine.graph.ICamera;
import engine.thinmatrix.terrains.ITerrain;
import engine.thinmatrix.utils.Light;

public interface ITerrainRenderer {

	void render(ITerrain terrain, ICamera camera, Light light, Vector4f clipPlane);
	void cleanUp();
	void render(List<ITerrain> terrains, Matrix4f toShadowSpace);
}
