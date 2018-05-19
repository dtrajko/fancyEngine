package game2D.assets;

import game2D.render.Model;

public class Assets {
	
	private static Model model = null;
	
	public static Model getModel() {
		if (model == null) Assets.initAsset();
		return model;
	}

	public static void initAsset() {
		model = new Model(Sprite.getVertices(), Sprite.getTexCoords(), Sprite.getIndices());
	}

	public static void deleteAsset() {
		model = null;
	}
}
