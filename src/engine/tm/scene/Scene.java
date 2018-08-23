package engine.tm.scene;

import engine.IGameLogic;
import engine.IScene;
import engine.SceneLight;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.QuadMesh;
import engine.graph.particles.IParticleEmitter;
import engine.tm.models.RawModel;
import engine.tm.render.Loader;

public class Scene implements IScene {

	private Loader loader;
	private RawModel model;

	@Override
	public void init(Window window, ICamera camera) {
		loader = new Loader();

		// OpenGL expects vertices to be defined counter clockwise by default
		float[] vertices = {
			// Left bottom triangle
			-0.5f,  0.5f, 0f, // V0
			-0.5f, -0.5f, 0f, // V1
			 0.5f, -0.5f, 0f, // V2
			 // Right top triangle
			 0.5f, -0.5f, 0f, // V2
			 0.5f,  0.5f, 0f, // V3
			-0.5f,  0.5f, 0f, // V0 
		};

		model = loader.loadToVAO(vertices);
	}

	public RawModel getModel() {
		return model;
	}

	@Override
	public void update(float interval) {
		// TODO Auto-generated method stub
	}

	@Override
	public void resetScene(Window window, ICamera camera, IGameLogic game) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setRenderShadows(boolean shadowsEnabled) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isRenderShadows() {
		// TODO Auto-generated method stub
		return false;
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
		loader.cleanUp();
	}

}
