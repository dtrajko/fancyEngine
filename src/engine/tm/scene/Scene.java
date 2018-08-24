package engine.tm.scene;

import engine.IGameLogic;
import engine.IScene;
import engine.SceneLight;
import engine.Window;
import engine.graph.ICamera;
import engine.graph.TriangleMesh;
import engine.graph.particles.IParticleEmitter;
import engine.tm.models.RawModel;
import engine.tm.models.TexturedModel;
import engine.tm.render.Loader;
import engine.tm.textures.ModelTexture;

public class Scene implements IScene {

	private Loader loader;
	private RawModel model;
	private ModelTexture texture;
	private TexturedModel texturedModel;

	@Override
	public void init(Window window, ICamera camera) {
		loader = new Loader();
		model = loader.loadToVAO(TriangleMesh.positions, TriangleMesh.textCoords, TriangleMesh.indices);
		texture = new ModelTexture(loader.loadTexture("frame"));
		texturedModel = new TexturedModel(model, texture);
	}

	public TexturedModel getTexturedModel() {
		return texturedModel;
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
