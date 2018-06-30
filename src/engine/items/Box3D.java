package engine.items;

import engine.graph.Camera;

public class Box3D {

    public float x;
    public float y;
    public float z;
    public float scale;

    public Box3D(float x, float y, float z, float scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
    }

    public boolean contains(float x2, float y2, float z2, boolean camera) {
    	float offsetXYZ = camera ? 0.02f : 0.0f;
    	float offsetY = camera ? Camera.HEIGHT - offsetXYZ : 0;
    	boolean contains =
        	x2 > x - offsetXYZ && x2 <= x + scale + offsetXYZ &&
        	y2 > y - offsetXYZ && y2 <= y + scale + offsetXYZ + offsetY &&
        	z2 > z - offsetXYZ && z2 <= z + scale + offsetXYZ;
        return contains;
    }
}
