package engine.items;

public class Box2D {

    public float x;
    public float y;
    public float width;
    public float height;

    public Box2D(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(float x2, float y2) {
        return x2 >= x
                && y2 >= y
                && x2 < x + width
                && y2 < y + height;
    }
}
