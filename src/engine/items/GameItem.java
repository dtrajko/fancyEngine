package engine.items;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import engine.graph.Mesh;

public class GameItem {

    private Mesh[] meshes;
    private final Vector3f position;
    private float scale;
    private final Quaternionf rotation;
    private int textPos;

    private boolean selected;
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

    public int getTextPos() {
        return textPos;
    }

    public final void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public final void setScale(float scale) {
        this.scale = scale;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final void setRotation(Quaternionf q) {
        this.rotation.set(q);
    }

    public Mesh getMesh() {
        return meshes[0];
    }
    
    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public void setMesh(Mesh mesh) {
        this.meshes = new Mesh[]{mesh};
    }
    
    public void cleanup() {
        int numMeshes = this.meshes != null ? this.meshes.length : 0;
        for(int i=0; i<numMeshes; i++) {
            this.meshes[i].cleanUp();
        }
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public void setSelected(boolean selected) {
    	this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
    }

    public void setBoundingBox() {
		float topLeftX = position.x - scale;
		float topLeftY = position.y - scale;
		float topLeftZ = position.z - scale;
		boundingBox = new Box3D(topLeftX, topLeftY, topLeftZ, scale * 2);
    }
    
    public Box3D getBoundingBox() {
    	if (boundingBox == null) {
    		setBoundingBox();
    	}
    	return boundingBox;
    }
}
