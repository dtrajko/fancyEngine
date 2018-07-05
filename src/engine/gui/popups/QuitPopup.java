package engine.gui.popups;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import config.Config;
import engine.Window;
import engine.graph.MouseInput;
import engine.graph.Texture;
import engine.gui.GuiElement;
import engine.gui.GuiManager;
import engine.gui.fonts.FontFactory;
import engine.gui.fonts.FontType;
import engine.gui.fonts.GUIText;
import engine.gui.fonts.TextMaster;

public class QuitPopup {
	
	boolean quitDialogOn = false;
	TextMaster textMaster;
	
	public QuitPopup() {
		textMaster = new TextMaster();
	}

	public void init(GuiManager guiManager, Window window) {
		
		textMaster.init();

    	Texture txQuitPopup;
		try {
			txQuitPopup = new Texture(Config.RESOURCES_DIR +  "/textures/small_popup.png");
			GuiElement guiQuitPopup = new GuiElement(txQuitPopup, new Vector3f(0.0f, 0.0f, 1), new Vector2f(0.24f, 0.22f));
			guiQuitPopup.setQuitPopup(true);
			guiManager.addGuiElement(guiQuitPopup);
		} catch (Exception e) {
			e.printStackTrace();
		}

    	Texture txButton;
		try {
			txButton = new Texture(Config.RESOURCES_DIR +  "/textures/button.png");

			GuiElement guiButtonCancel = new GuiElement(txButton, new Vector3f(-0.113f, -0.132f, 1), new Vector2f(0.108f, 0.0508f));
			guiButtonCancel.setQuitPopup(true).setClickable(true).setCancelButton(true);
			guiManager.addGuiElement(guiButtonCancel);
			
			GuiElement guiButtonConfirm = new GuiElement(txButton, new Vector3f(0.113f, -0.132f, 1), new Vector2f(0.108f, 0.0508f));
			guiButtonConfirm.setQuitPopup(true).setClickable(true).setConfirmButton(true);
			guiManager.addGuiElement(guiButtonConfirm);

		} catch (Exception e) {
			e.printStackTrace();
		}

		FontType font = FontFactory.getFont("candara", window);

		GUIText guiQuitGame = new GUIText("Quit Game?", 1.8f, font, new Vector2f(0.445f, 0.44f), 1f, false);
		guiQuitGame.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiQuitGame);

		GUIText guiCancel = new GUIText("Cancel", 1.2f, font, new Vector2f(0.424f, 0.551f), 1f, false);
		guiCancel.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiCancel);
		
		GUIText guiConfirm = new GUIText("Confirm", 1.2f, font, new Vector2f(0.53f, 0.551f), 1f, false);
		guiConfirm.setColor(1.0f, 1.0f, 1.0f);
		textMaster.setGuiText(0, guiConfirm);
	}

	public boolean input(GuiManager guiManager, MouseInput mouseInput, Window window, boolean updateEnabled) {
        return updateEnabled;
	}
	
    public TextMaster getTextMaster() {
    	return textMaster;
    }

	public void render() {
		textMaster.render();
	}
}
