package engine.items;

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
        return 
        	x2 >= x - sizeX * 2 && y2 >= y - sizeY * 2 && z2 >= z - sizeZ * 2 &&
        	x2 < x + sizeX / 2 && y2 < y + sizeY / 2 && z2 < z + sizeZ / 2;
    }
}
