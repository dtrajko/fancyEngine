package engine.tm.lens;

import config.Config;
import engine.Window;
import engine.tm.textures.Texture;
import engine.utils.MyFile;

public class FlareFactory {

	public static FlareManager createLensFlare(Window window) {

		//loading textures for lens flare
		Texture texture1 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex1.png").normalMipMap().create();
		Texture texture2 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex2.png").normalMipMap().create();
		Texture texture3 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex3.png").normalMipMap().create();
		Texture texture4 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex4.png").normalMipMap().create();
		Texture texture5 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex5.png").normalMipMap().create();
		Texture texture6 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex6.png").normalMipMap().create();
		Texture texture7 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex7.png").normalMipMap().create();
		Texture texture8 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex8.png").normalMipMap().create();
		Texture texture9 = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/tex9.png").normalMipMap().create();

		//set up lens flare
		FlareManager lensFlare = new FlareManager(window, 0.16f, 
			new FlareTexture(texture6, 1f),
			new FlareTexture(texture4, 0.46f),
			new FlareTexture(texture2, 0.2f),
			new FlareTexture(texture7, 0.1f),
			new FlareTexture(texture1, 0.04f),
			new FlareTexture(texture3, 0.12f),
			new FlareTexture(texture9, 0.24f),
			new FlareTexture(texture5, 0.14f),
			new FlareTexture(texture1, 0.024f),
			new FlareTexture(texture7, 0.4f),
			new FlareTexture(texture9, 0.2f),
			new FlareTexture(texture3, 0.14f),
			new FlareTexture(texture5, 0.6f),
			new FlareTexture(texture4, 0.8f),
			new FlareTexture(texture8, 1.2f));

		return lensFlare;
	}	
}
