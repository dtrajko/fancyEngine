package engine.gui.font;

import engine.Window;
import engine.loaders.LoaderFactory;

public class FontFactory {

	private static FontType candara = null;
	private static FontType calibri = null;

	public static FontType getFont(String fontName, Window window) {
		FontType font = null;
		switch(fontName) {
			case "candara":
				if (candara == null) {
					candara = new FontType(LoaderFactory.getRawModelLoader().loadTexture(fontName, 0), fontName, window);
				}
				font = candara;	
				break;
			case "calibri":
				if (calibri == null) {
					calibri = new FontType(LoaderFactory.getRawModelLoader().loadTexture(fontName, 0), fontName, window);
				}
				font = calibri;	
				break;
		}
		return font;
	}
}
