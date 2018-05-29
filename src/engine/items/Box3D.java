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

    public boolean contains(float x2, float y2, float z2) {
    	boolean contains =
        	x2 > x && x2 < x + scale &&
        	y2 > y + Camera.HEIGHT && y2 < y + scale + Camera.HEIGHT &&
        	z2 > z && z2 < z + scale;        	
        return contains;
    }
}
