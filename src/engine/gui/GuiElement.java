package engine.gui;

import org.joml.Vector2f;
import org.joml.Vector3f;
import engine.graph.Mesh;
import engine.graph.Texture;
import engine.items.Box2D;
import engine.tm.gui.GuiTexture;

public class GuiElement {

	private GuiTexture texture;
	private Box2D aabb;
	private boolean selected;
	private boolean mouseOver;
	private boolean inventory;
	private boolean importDialog;
	private boolean quitPopup;
	private boolean isClickable;
	private Mesh mesh = null;
	private boolean isCancelButton;
	private boolean isConfirmButton;
	private boolean isSplashBackground;
	private String title;

	public GuiElement(Texture texture, Vector3f position, Vector2f scale) {
		super();
		this.texture = new GuiTexture(texture.getId(), new Vector2f(position.x, position.y), scale);
		setAABB();
		selected = false;
		mouseOver = false;
		inventory = false;
		importDialog = false;
		quitPopup = false;
		isClickable = false;
	}

	public GuiTexture getGuiTexture() {
		return texture;
	}

	public int getTexture() {
		return texture.getTexture();
	}
	
	public GuiElement setMouseOver(boolean value) {
		mouseOver = value;
		return this;
	}

	public boolean isMouseOver() {
		return mouseOver;
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
	
	public GuiElement setInventory(boolean value) {
		inventory = value;
		return this;
	}

	public boolean isInventory() {
		return inventory;
	}

	public GuiElement setClickable(boolean value) {
		this.isClickable = value;
		return this;
	}

	public boolean isClickable() {
		return isClickable;
	}

	public GuiElement setImportDialog(boolean value) {
		importDialog = value;
		return this;
	}

	public boolean isImportDialog() {
		return importDialog;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public Mesh getMesh() {
		return mesh;
	}

	public GuiElement setQuitPopup(boolean value) {
		this.quitPopup = value;
		return this;
	}

	public boolean isQuitPopup() {
		return quitPopup;
	}

	public boolean isCancelButton() {
		return isCancelButton;
	}

	public boolean isConfirmButton() {
		return isConfirmButton;
	}

	public boolean isSplashBackground() {
		return isSplashBackground;
	}

	public GuiElement setCancelButton(boolean value) {
		isCancelButton = value;
		return this;
	}

	public GuiElement setConfirmButton(boolean value) {
		isConfirmButton = value;
		return this;
	}

	public GuiElement setSplashBackground(boolean value) {
		isSplashBackground = value;
		return this;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
}
