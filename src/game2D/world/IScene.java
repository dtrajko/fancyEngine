package game2D.world;

import java.util.List;

import org.joml.Matrix4f;

import game2D.collision.AABB;
import game2D.entities.Entity;

public interface IScene {

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
}
