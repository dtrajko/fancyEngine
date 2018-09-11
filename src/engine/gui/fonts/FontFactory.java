package engine.gui.fonts;

import config.Config;
import engine.Window;
import engine.loaders.LoaderFactory;
import engine.tm.gui.fonts.FontType;

public class FontFactory {

	private static FontType candara = null;
	private static FontType calibri = null;
	private static FontType kenney = null;

	public static FontType getFont(String fontName, Window window) {
		FontType font = null;
		switch(fontName) {
			case "candara":
				if (candara == null) {
					candara = new FontType(LoaderFactory.getLoader().loadFontTexture(fontName, 0), Config.FONT_DIR + "/" + fontName + ".fnt");
				}
				font = candara;	
				break;
			case "calibri":
				if (calibri == null) {
					calibri = new FontType(LoaderFactory.getLoader().loadFontTexture(fontName, 0), Config.FONT_DIR + "/" + fontName + ".fnt");
				}
				font = calibri;	
				break;
			case "kenney":
				if (kenney == null) {
					kenney = new FontType(LoaderFactory.getLoader().loadFontTexture(fontName, 0), Config.FONT_DIR + "/" + fontName + ".fnt");
				}
				font = kenney;	
				break;
		}
		return font;
	}
}
