package engine.tm.gui.fonts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import engine.GameEngine;
import engine.tm.gui.fonts.FontType;
import engine.tm.gui.fonts.GUIText;
import engine.tm.gui.fonts.TextMeshData;
import engine.tm.loaders.Loader;

public class TextMaster {

	private Loader loader;
	private FontRenderer renderer;
	private Map<FontType, List<GUIText>> textMap;
	private FontType font;
	private TextMeshData data;
	private int vaoID;
	private List<GUIText> textBatch;
	private Map<GUIText, GUITextVaoCache> cachedTextVaos;
	private double lastTextUpdate;
	private static int cache_hits;
	private static int cache_misses;

	public TextMaster(Loader loader) {
		this.loader = loader;
		renderer = new FontRenderer();
		lastTextUpdate = GameEngine.getTimer().getLastLoopTime();
		cachedTextVaos = new HashMap<GUIText, GUITextVaoCache>();
		cache_hits = cache_misses = 0;
		initMap();
	}

	public void prepare() {
		clearTextMap();
		initMap();
	}

	public void initMap() {
		textMap = new HashMap<FontType, List<GUIText>>();
	}

	public void loadText(GUIText guiText) {

		font = guiText.getFont();
		data = font.loadText(guiText);
		GUITextVaoCache guiTextVaoCache = cachedTextVaos.get(guiText);

		if (isCacheHit(guiTextVaoCache, data)) {
			data = guiTextVaoCache.getTextMeshData();
			vaoID = guiTextVaoCache.getVaoID();
			cache_hits++;
		} else {
			vaoID = loader.loadToVAO(data.getVertexPositions(), data.getTextureCoords()); // memory leak
			GUITextVaoCache guiTextVaoCacheNew = new GUITextVaoCache(guiText, data, vaoID);
			cachedTextVaos.put(guiText, guiTextVaoCacheNew);
			lastTextUpdate = GameEngine.getTimer().getLastLoopTime();
			cache_misses++;
		}

		guiText.setMeshInfo(vaoID, data.getVertexCount());
		textBatch = textMap.get(font);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			textMap.put(font, textBatch);
		}
		textBatch.add(guiText);
	}

	private boolean isCacheHit(GUITextVaoCache guiTextVaoCached, TextMeshData data) {
		if (guiTextVaoCached == null) return false;
		if (GameEngine.getTimer().getLastLoopTime() - lastTextUpdate > 1) return false;
		if (data.getVertexPositions().length == guiTextVaoCached.getTextMeshData().getVertexPositions().length ||
			data.getTextureCoords().length == guiTextVaoCached.getTextMeshData().getTextureCoords().length) {
			return true;
		}
		return false;
	}

	public void removeText(GUIText guiText) {
		if (guiText == null) return;
		textBatch = textMap.get(guiText.getFont());
		if (textBatch == null) return;
		textBatch.remove(guiText);
		if (textBatch.isEmpty()) {
			textMap.remove(guiText.getFont());
			textBatch.clear();
		}
	}

	public void clearTextMap() {
		textMap.clear();
		textMap = null;
	}

	public void render() {
		renderer.render(textMap);
	}

	public void cleanUp() {
		renderer.cleanUp();
	}

	public int getTextEntriesCount() {
		int count = 0;
		for (FontType fontType : textMap.keySet()) {
			count += textMap.get(fontType).size();
		}
		return count;
	}
}
