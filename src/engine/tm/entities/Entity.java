package engine.tm.entities;

import org.joml.Vector3f;
import engine.items.Box3D;
import engine.tm.models.TexturedModel;

public class Entity {

	private TexturedModel texturedModel;
	private Vector3f position;
	private float rotX, rotY, rotZ;
	private float scale;
	private Box3D boundingBox = null;
	private boolean solid;
	private int textureIndex = 0;

	public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super();
		this.texturedModel = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.solid = false;
	}

	public Entity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this(model, position, rotX, rotY, rotZ, scale);
		this.textureIndex = textureIndex;
	}

	public Entity(int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public boolean isSolid() {
		return solid;
	}

	public Entity setSolid(boolean solid) {
		this.solid = solid;
		return this;
	}

	public boolean isUsingNormalMap() {
		int normalMapID = texturedModel.getTexture().getNormalMap();
		if (normalMapID != -1) {
			return true;
		} else {
			return false;
		}
	}

	public float getTextureOffsetX() {
		int column = textureIndex % texturedModel.getTexture().getNumberOfRows();
		float offsetX = (float) column / (float) texturedModel.getTexture().getNumberOfRows();
		return offsetX;
	}

	public float getTextureOffsetY() {
		int row = textureIndex / texturedModel.getTexture().getNumberOfRows();
		float offsetY = (float) row / (float) texturedModel.getTexture().getNumberOfRows();
		return offsetY;
	}

	public void increasePosition(float dx, float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}

	public void decreasePosition(float dx, float dy, float dz) {
		this.position.x -= dx;
		this.position.y -= dy;
		this.position.z -= dz;
	}

	public void increaseRotation(float dx, float dy, float dz) {
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}

	public TexturedModel getTexturedModel() {
		return texturedModel;
	}

	public void setTexturedModel(TexturedModel model) {
		this.texturedModel = model;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getRotX() {
		return rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
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
