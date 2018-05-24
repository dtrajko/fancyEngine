package engine.items;

import engine.graph.Camera;

public class Box3D {

    public float x;
    public float y;
    public float z;
    public float sizeX;
    public float sizeY;
    public float sizeZ;

    public Box3D(float x, float y, float z, float sizeX, float sizeY, float sizeZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public boolean contains(float x2, float y2, float z2) {
    	float boxOffset = 0.4f;
    	boolean contains =	x2 >= x - (2 * sizeX - boxOffset) && y2 >= y - (2 * sizeY - boxOffset) && z2 >= z - (2 * sizeZ - boxOffset) &&
    						x2 <= x - boxOffset && y2 <= y + Camera.HEIGHT - boxOffset && z2 <= z - boxOffset;
    	return contains;
    }
}
