package engine.gui.fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;
import engine.tm.gui.fonts.TextMeshData;
import engine.tm.loaders.Loader;

public class TextMaster {

	private static Loader loader;
	private static FontRenderer renderer;
	private Map<FontType, List<GUIText>> guiTextMap = new HashMap<FontType, List<GUIText>>();
	private List<GUIText> guiTexts;

	public TextMaster() {
		renderer = new FontRenderer();
	}

	public void init() {
		loader = new Loader();
		guiTexts = new ArrayList<GUIText>();
	}

	public void loadText(GUIText text) {
		FontType font = text.getFont();
		TextMeshData textMeshData = font.loadText(text);
		int vao = loader.loadToVAO(textMeshData.getVertexPositions(), textMeshData.getTextureCoords());
		text.setMeshInfo(vao, textMeshData.getVertexCount());
		List<GUIText> textBatch = guiTextMap.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			guiTextMap.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public void removeText(GUIText text) {
		List<GUIText> textBatch = guiTextMap.get(text.getFont());
		if (textBatch != null) {
			textBatch.remove(text);
			if (textBatch.isEmpty()) {
				guiTextMap.remove(text.getFont());
			}			
		}
	}

	public void emptyTextMap() {
		guiTextMap.clear();
	}

	public void render() {
		renderer.render(guiTextMap);
	}

	public List<GUIText> getGuiTexts() {
		return guiTexts;
	}

	public Map<FontType, List<GUIText>> getGuiTextsMap() {
		return guiTextMap;
	}

	public GUIText getGuiText(int index) {
		return guiTexts.get(index);
	}

	public void setGuiText(int index, GUIText newGuiText) {
		guiTexts.add(index, newGuiText);
		loadText(newGuiText);
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}
}
