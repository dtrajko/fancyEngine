package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.graph.InstancedMesh;
import engine.graph.Mesh;
import engine.graph.particles.IParticleEmitter;
import engine.graph.weather.Fog;
import engine.items.SkyBox;

public class Scene {

	private Map<Mesh, List<GameItem>> meshMap;
	private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;

    private SkyBox skyBox;
    private SceneLight sceneLight;
    private Fog fog;
    private boolean renderShadows;
    private IParticleEmitter[] particleEmitters;

    public Scene() {
        meshMap = new HashMap<Mesh, List<GameItem>>();
        instancedMeshMap = new HashMap();
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getGameInstancedMeshes() {
        return instancedMeshMap;
    }

    public void setGameItems(GameItem[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems[i];
            Mesh mesh = gameItem.getMesh();
            List<GameItem> list = meshMap.get(mesh);
            if ( list == null ) {
                list = new ArrayList<>();
                meshMap.put(mesh, list);
            }
            list.add(gameItem);
        }
    }

    public void setGameItems(List<GameItem> gameItems) {
        // Create a map of meshes to speed up rendering
        for (GameItem gameItem : gameItems) {
            Mesh[] meshes = gameItem.getMeshes();
            for (Mesh mesh : meshes) {
                boolean instancedMesh = mesh instanceof InstancedMesh;
                List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
                if (list == null) {
                    list = new ArrayList<>();
                    if (instancedMesh) {
                        instancedMeshMap.put((InstancedMesh)mesh, list);
                    } else {
                        meshMap.put(mesh, list);
                    }
                }
                list.add(gameItem);
            }
        }
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public Fog getFog() {
        return fog;
    }

    public void setRenderShadows(boolean renderShadows) {
    	this.renderShadows = renderShadows;
    }

    public boolean isRenderShadows() {
        return renderShadows;
    }

}
