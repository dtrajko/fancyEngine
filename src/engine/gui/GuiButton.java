package engine.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;

import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.Box2D;

public class GuiButton {

	private GuiTexture texture;
	private Box2D aabb;
	private boolean selected;
	private boolean inventory;
	private Mesh mesh = null;

	public GuiButton(Texture texture, Vector3f position, Vector2f scale) {
		super();
		this.texture = new GuiTexture(texture.getId(), position, scale);
		setAABB();
		selected = false;
		inventory = false;
	}

	public GuiTexture getGuiTexture() {
		return texture;
	}

	public int getTexture() {
		return texture.getTexture();
	}

    public void setAABB() {
		float x = texture.getPosition().x - texture.getScale().x;
		float y = texture.getPosition().y - texture.getScale().y;
		float width = texture.getScale().x * 2;
		float height = texture.getScale().y * 2;
		aabb = new Box2D(x, y, width, height);
    }

    public Box2D getAABB() {
    	if (aabb == null) {
    		setAABB();
    	}
    	return aabb;
    }

	public void setSelected(boolean value) {
		selected = value;
	}

	public boolean getSelected() {
		return selected;
	}
	
	public void setInventory(boolean value) {
		inventory = value;
	}

	public boolean isInventory() {
		return inventory;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
}
