package engine.tm;

import config.Config;
import engine.IGameLogic;
import engine.IScene;
import engine.SceneLight;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.particles.IParticleEmitter;
import engine.items.SkyBox;
import engine.tm.lens.FlareFactory;
import engine.tm.lens.FlareManager;
import engine.tm.settings.WorldSettings;
import engine.tm.sun.Sun;
import engine.tm.utils.Light;
import engine.tm.textures.Texture;

public class ThinMatrixScene implements IScene {

	@Override
	public void init(Window window, ICamera camera) {
		// initialize sun and lens flare and set sun direction
		Light light = new Light(WorldSettings.LIGHT_DIR, WorldSettings.LIGHT_COL, WorldSettings.LIGHT_BIAS);
		FlareManager lensFlare = FlareFactory.createLensFlare(window);
		Texture sunTexture = Texture.newTexture(Config.RESOURCES_DIR + "/ThinMatrix/lensFlare/sun.png").normalMipMap().create();
		Sun sun = new Sun(sunTexture, 40, light, lensFlare);
		sun.setDirection(WorldSettings.LIGHT_DIR);
	}

	@Override
	public void update(float interval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRenderShadows() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRenderShadows(boolean shadowsEnabled) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SkyBox getSkyBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SceneLight getSceneLight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IParticleEmitter[] getParticleEmitters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetScene(Window window, ICamera camera, IGameLogic game) {
		// TODO Auto-generated method stub
		
	}
}
