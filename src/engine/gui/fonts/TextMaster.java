package engine.gui.fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.gui.fonts.FontType;
import engine.gui.fonts.GUIText;
import engine.gui.fonts.TextMeshData;
import engine.loaders.RawModelLoader;

public class TextMaster {

	private static RawModelLoader loader;
	private static Map<FontType, List<GUIText>> guiTextsMap = new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	private static List<GUIText> guiTexts;

	public static void init() {
		loader = new RawModelLoader();
		guiTexts = new ArrayList<GUIText>();
	}

	public static void render() {
		renderer.render(guiTextsMap);
	}
	
	public static List<GUIText> getGuiTexts() {
		return guiTexts;
	}

	public static Map<FontType, List<GUIText>> getGuiTextsMap() {
		return guiTextsMap;
	}

	public static GUIText getGuiText(int index) {
		return guiTexts.get(index);
	}

	public static void setGuiText(int index, GUIText newGuiText) {
		guiTexts.add(index, newGuiText);
	}

	public static void loadText(GUIText text) {

		FontType font = text.getFont();
		TextMeshData textMeshData = font.loadText(text);
		int vao = loader.loadToVAO(textMeshData.getVertexPositions(), textMeshData.getTextureCoords());
		text.setMeshInfo(vao, textMeshData.getVertexCount());
		List<GUIText> textBatch = guiTextsMap.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			guiTextsMap.put(font, textBatch);
		}
		textBatch.add(text);
	}

	public static void removeText(GUIText text) {
		List<GUIText> textBatch = guiTextsMap.get(text.getFont());
		if (textBatch != null) {
			textBatch.remove(text);
			if (textBatch.isEmpty()) {
				guiTextsMap.remove(text.getFont());
			}			
		}
	}

	public static void cleanUp() {
		renderer.cleanUp();
	}
}
