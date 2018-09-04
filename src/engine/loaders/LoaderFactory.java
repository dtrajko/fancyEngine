package engine.loaders;

import engine.tm.loaders.Loader;

public class LoaderFactory {

	private static Loader loader = null;

	public static Loader getLoader() {
		if (loader == null) {
			loader = new Loader();
		}
		return loader;
	}
}
