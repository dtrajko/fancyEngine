package engine.tm.water;

public class WaterTile {
     
    public static final float TILE_SIZE = 400;

    private float x, y, z;

    public WaterTile(float centerX, float y, float centerZ){
        this.x = centerX;
        this.z = centerZ;
        this.y = y;
    }

    public float getY() {
        return y;
    }
 
    public float getX() {
        return x;
    }
 
    public float getZ() {
        return z;
    } 
}
