package engine.interfaces;

import java.util.List;
import java.util.Map;
import org.joml.Vector3f;

import engine.Window;
import engine.graph.Input;
import engine.tm.entities.Entity;
import engine.tm.entities.Light;
import engine.tm.gui.GuiTexture;
import engine.tm.lensFlare.FlareManager;
import engine.tm.loaders.Loader;
import engine.tm.models.TexturedModel;
import engine.tm.particles.FireMaster;
import engine.tm.skybox.ISkyBox;

public interface IScene {

	void update(float interval, Input input);
	void resetScene(Window window, ICamera camera, IGameLogic game);
	void save();
	void cleanUp();
	ICamera getCamera();
	Map<TexturedModel, List<Entity>> getEntityList();
	Loader getLoader();
	ITerrain getCurrentTerrain(float x, float z);
	IPlayer getPlayer();
	Vector3f getLightDirection();
	ISkyBox getSkybox();
	ISun getSun();
	FlareManager getFlareManager();
	List<GuiTexture> getGuiElements();
	FireMaster getFireMaster();
	void removeEntity(Entity entity);
	List<Light> getLights();
}