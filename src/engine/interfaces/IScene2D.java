package engine.interfaces;

import java.util.List;
import org.joml.Matrix4f;

import engine.Window;
import engine.graph.Camera;
import game2D.collision.AABB;
import game2D.entities.Entity;
import game2D.world.Tile;

public interface IScene2D {

	int getWidth();
	int getHeight();
	int getScale();
	int getViewWidth();
	int getViewHeight();
	Tile getTile(int x, int y);
	int getBackgroundTile();
	Matrix4f getWorldMatrix();
	List<Entity> getEntities();
	AABB getTileBoundingBox(int i, int j);
	ITileType[] getTileTypes();
	void resetScene(Window window, Camera camera, IGameLogic game);
}
