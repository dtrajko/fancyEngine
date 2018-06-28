package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;

import engine.graph.InstancedMesh;
import engine.graph.Mesh;
import engine.graph.particles.IParticleEmitter;
import engine.graph.weather.Fog;
import engine.items.GameItem;
import engine.items.SkyBox;

public class Scene {

    private final Map<Mesh, List<GameItem>> meshMap;
    private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;
    private SkyBox skyBox;
    private SceneLight sceneLight;
    private Fog fog;
    private boolean renderShadows;
    private IParticleEmitter[] particleEmitters;

    public Scene() {
        meshMap = new HashMap();
        instancedMeshMap = new HashMap();
        fog = Fog.NOFOG;
        renderShadows = true;
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getGameInstancedMeshes() {
        return instancedMeshMap;
    }

    public boolean isRenderShadows() {
        return renderShadows;
    }

    /**
     * The purpose of this method is to sort meshes by transparency - non-transparent first, transparent last
     * 
     * @param meshTypesMap
     */
    public void initMeshMaps(HashMap<String, Mesh> meshTypesMap) {
    	List<GameItem> gameItems = new ArrayList<GameItem>();
    	GameItem gameItem;
        for (Mesh mesh : meshTypesMap.values()) {
        	gameItem = new GameItem(mesh);
        	gameItems.add(gameItem);
        }
        sortByTransparency(gameItems);
        setGameItems(gameItems);
    }

    public void setGameItemsArray(GameItem[] gameItems) {
        // Create a map of meshes to speed up rendering
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for (int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems[i];
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

	public List<GameItem> sortByTransparency(List<GameItem> gameItems) {
		Collections.sort(gameItems, new Comparator<GameItem>() {
			@Override
			public int compare(GameItem one, GameItem two) {
				if (one.getMesh().getMaterial().getTransparency() > two.getMesh().getMaterial().getTransparency()) return -1;
				else if (one.getMesh().getMaterial().getTransparency() < two.getMesh().getMaterial().getTransparency()) return 1;
				else return 0;
			}
		});
		return gameItems;
	}

	public void appendGameItem(GameItem gameItem) {
        Mesh mesh = gameItem.getMesh();
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

	public void removeGameItem(GameItem gameItem) {
		Mesh mesh = gameItem.getMesh();
        boolean instancedMesh = mesh instanceof InstancedMesh;
        List<GameItem> list = instancedMesh ? instancedMeshMap.get(mesh) : meshMap.get(mesh);
        if (list == null) {
        	list = new ArrayList<GameItem>();
        }
        list.remove(gameItem);
    }

    public void cleanup() {
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
        for (Mesh mesh : instancedMeshMap.keySet()) {
            mesh.cleanUp();
        }
        if (particleEmitters != null) {
            for (IParticleEmitter particleEmitter : particleEmitters) {
                particleEmitter.cleanup();
            }
        }
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
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

    /**
     * @return the fog
     */
    public Fog getFog() {
        return fog;
    }

    /**
     * @param fog the fog to set
     */
    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        this.particleEmitters = particleEmitters;
    }

	public boolean inCollision(Vector3f newPos, boolean camera) {
		boolean inCollision = false;
        Map<Mesh, List<GameItem>> mapMeshes = getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
    		for (GameItem gameItem : mapMeshes.get(mesh)) {
    			if (gameItem.getBoundingBox().contains(newPos.x, newPos.y, newPos.z, camera)) {
    				inCollision = true;
    				break;
    			}
    		}        	
        }
        Map<InstancedMesh, List<GameItem>> mapInstancedMeshes = getGameInstancedMeshes();
        for (Mesh mesh : mapInstancedMeshes.keySet()) {
    		for (GameItem gameItem : mapInstancedMeshes.get(mesh)) {
    			if (gameItem.getBoundingBox().contains(newPos.x, newPos.y, newPos.z, camera)) {
    				inCollision = true;
    				break;
    			}
    		}        	
        }
		return inCollision;
	}
}
