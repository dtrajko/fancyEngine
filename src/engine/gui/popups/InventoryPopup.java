package engine.gui.popups;

import java.util.HashMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import config.Config;
import engine.IScene;
import engine.Scene;
import engine.Window;
import engine.graph.Mesh;
import engine.graph.Input;
import engine.graph.Texture;
import engine.gui.GuiElement;
import engine.gui.GuiManager;
import engine.gui.fonts.TextMaster;

public class InventoryPopup {

	private boolean enabled = false;
	private TextMaster textMaster;
	private GuiElement nextBlock;

	public InventoryPopup() {
	}

	public void init(GuiManager guiManager, Window window, HashMap<String, Mesh> meshTypesMap) {

		textMaster = new TextMaster();
		textMaster.init();

		try {

	    	// inventory
	    	Texture texturePanelInventory = new Texture(Config.RESOURCES_DIR +  "/textures/window.png");
	    	GuiElement guiPanelInventory = new GuiElement(texturePanelInventory, new Vector3f(0.0f, -0.01f, 1), new Vector2f(0.3304f, 0.790f));
	    	guiPanelInventory.setInventory(true);
	    	guiManager.addGuiElement(guiPanelInventory);

	    	Texture textureBtnStairs = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_stairs.png");
	    	GuiElement guiButtonStairs = new GuiElement(textureBtnStairs, new Vector3f(-0.21f, 0.56f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonStairs.setInventory(true).setClickable(true);
	    	guiButtonStairs.setMesh(meshTypesMap.get("STAIRS"));
	    	guiManager.addGuiElement(guiButtonStairs);

	    	Texture textureBtnStairsCorner = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_stairs_corner.png");
	    	GuiElement guiButtonStairsCorner = new GuiElement(textureBtnStairsCorner, new Vector3f(0.0f, 0.56f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonStairsCorner.setInventory(true).setClickable(true);
	    	guiButtonStairsCorner.setMesh(meshTypesMap.get("STAIRS_CORNER"));
	    	guiManager.addGuiElement(guiButtonStairsCorner);

	    	Texture textureBtnStairsCornerInner = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_stairs_corner_inner.png");
	    	GuiElement guiButtonStairsCornerInner = new GuiElement(textureBtnStairsCornerInner, new Vector3f(0.21f, 0.56f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonStairsCornerInner.setInventory(true).setClickable(true);
	    	guiButtonStairsCornerInner.setMesh(meshTypesMap.get("STAIRS_CORNER_INNER"));
	    	guiManager.addGuiElement(guiButtonStairsCornerInner);

	    	Texture textureBtnOakWood = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_oakwood.png");
	    	GuiElement guiButtonOakWood = new GuiElement(textureBtnOakWood, new Vector3f(-0.21f, 0.18f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonOakWood.setInventory(true).setClickable(true);
	    	guiButtonOakWood.setMesh(meshTypesMap.get("OAKWOOD"));
	    	guiManager.addGuiElement(guiButtonOakWood);

	    	Texture textureBtnWood = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_wood.png");
	    	GuiElement guiButtonWood = new GuiElement(textureBtnWood, new Vector3f(0.0f, 0.18f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonWood.setInventory(true).setClickable(true);
	    	guiButtonWood.setMesh(meshTypesMap.get("WOOD"));
	    	guiManager.addGuiElement(guiButtonWood);

	    	Texture textureBtnCobble = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_cobble.png");
	    	GuiElement guiButtonCobble = new GuiElement(textureBtnCobble, new Vector3f(0.21f, 0.18f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonCobble.setInventory(true).setClickable(true);
	    	guiButtonCobble.setMesh(meshTypesMap.get("COBBLE"));
	    	guiManager.addGuiElement(guiButtonCobble);

	    	Texture textureBtnGrass = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_grass.png");
	    	GuiElement guiButtonGrass = new GuiElement(textureBtnGrass, new Vector3f(-0.21f, -0.2f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonGrass.setInventory(true).setClickable(true);
	    	guiButtonGrass.setMesh(meshTypesMap.get("GRASS"));
	    	guiManager.addGuiElement(guiButtonGrass);

	    	Texture textureBtnGround = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_ground.png");
	    	GuiElement guiButtonGround = new GuiElement(textureBtnGround, new Vector3f(0f, -0.2f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonGround.setInventory(true).setClickable(true);
	    	guiButtonGround.setMesh(meshTypesMap.get("GROUND"));
	    	guiManager.addGuiElement(guiButtonGround);

	    	Texture textureBtnWater = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_water.png");
	    	GuiElement guiButtonWater = new GuiElement(textureBtnWater, new Vector3f(0.21f, -0.2f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonWater.setInventory(true).setClickable(true);
	    	guiButtonWater.setMesh(meshTypesMap.get("WATER"));
	    	guiManager.addGuiElement(guiButtonWater);

	    	Texture textureBtnGlass = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_glass.png");
	    	GuiElement guiButtonGlass = new GuiElement(textureBtnGlass, new Vector3f(-0.21f, -0.58f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonGlass.setInventory(true).setClickable(true);
	    	guiButtonGlass.setMesh(meshTypesMap.get("GLASS"));
	    	guiManager.addGuiElement(guiButtonGlass);

	    	Texture textureBtnTreetop = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_treetop.png");
	    	GuiElement guiButtonTreetop = new GuiElement(textureBtnTreetop, new Vector3f(0.21f, -0.58f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonTreetop.setInventory(true).setClickable(true);
	    	guiButtonTreetop.setMesh(meshTypesMap.get("TREETOP"));
	    	guiManager.addGuiElement(guiButtonTreetop);

	    	Texture textureBtnLava = new Texture(Config.RESOURCES_DIR +  "/textures/button_cube_lava.png");
	    	GuiElement guiButtonLava = new GuiElement(textureBtnLava, new Vector3f(0.0f, -0.58f, 1), new Vector2f(0.1f, 0.18f));
	    	guiButtonLava.setInventory(true).setClickable(true);
	    	guiButtonLava.setMesh(meshTypesMap.get("LAVA"));
	    	guiManager.addGuiElement(guiButtonLava);

		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	public void input(GuiManager guiManager, Input input, Window window, IScene scene) {
    	Vector2f mouseNDC = guiManager.getNormalisedDeviceCoordinates(
        		(float) input.getMousePosition().x,
        		(float) input.getMousePosition().y, window);
    		for (GuiElement gb : guiManager.getGuiElements()) {
    			gb.setMouseOver(false);
    		}
    		nextBlock = guiManager.selectGuiItem(mouseNDC);
        	if (nextBlock instanceof GuiElement && nextBlock.isInventory()) {
        		nextBlock.setMouseOver(true);
        	}
        if (input.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_1) || 
        	input.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_2) ||
        	input.isMouseButtonReleased(GLFW.GLFW_MOUSE_BUTTON_3)) {
        	if (nextBlock instanceof GuiElement && nextBlock.isInventory()) {
        		guiManager.toggleInventoryDialog(window);
        	}
        }
	}

    public GuiElement getNextBlock() {
    	return nextBlock;
    }

    public TextMaster getTextMaster() {
    	return textMaster;
    }

	public void render() {
		textMaster.render();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
	}
}
