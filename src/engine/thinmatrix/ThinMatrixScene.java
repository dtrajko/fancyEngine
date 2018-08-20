package engine.thinmatrix;

import engine.IGameLogic;
import engine.IScene;
import engine.SceneLight;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.particles.IParticleEmitter;
import engine.items.SkyBox;

public class ThinMatrixScene implements IScene {

	@Override
	public void init(Window window, ICamera camera) {
		// TODO Auto-generated method stub
		
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
