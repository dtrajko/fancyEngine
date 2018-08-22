package engine;

import engine.IGameLogic;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.particles.IParticleEmitter;
import engine.items.SkyBox;

public interface IScene {

	void init(Window window, ICamera camera);
	void update(float interval);
	void resetScene(Window window, ICamera camera, IGameLogic game);
	void setRenderShadows(boolean shadowsEnabled);
	boolean isRenderShadows();
	SceneLight getSceneLight();
	IParticleEmitter[] getParticleEmitters();
	void save();
	void cleanup();
}
