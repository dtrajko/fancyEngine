package engine;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import engine.graph.HeightMapMesh;
import engine.graph.Mesh;
import engine.items.Box2D;
import engine.items.Box3D;

public class GameItem {

	private boolean selected;
	private Mesh[] meshes;
    private final Vector3f position;
    private float scale;
    private final Quaternionf rotation;
    private int textPos;
    private Box3D boundingBox = null;

    public GameItem() {
        selected = false;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
    }

    public GameItem(Mesh mesh) {
        this();
        this.meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final void setRotation(Quaternionf q) {
        this.rotation.set(q);
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }

    public Mesh getMesh() {
        return meshes[0];
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getTextPos() {
        return textPos;
    }

    public void setBoundingBox() {
		float topLeftX = position.x + scale;
		float topLeftY = position.y + scale;
		float topLeftZ = position.z + scale;
		boundingBox = new Box3D(topLeftX, topLeftY, topLeftZ, scale, scale, scale);    		
    }
    
    public Box3D getBoundingBox() {
    	if (boundingBox == null) {
    		setBoundingBox();
    	}
    	return boundingBox;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }
}
