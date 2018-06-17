package engine.items;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;

import engine.graph.Mesh;

public class GameItemStairs {

    private List<GameItem> items;
    private final Vector3f position;
    private float scale;

    public GameItemStairs() {
    	position = new Vector3f(0, 0, 0);
    	scale = 0.5f;
    	items = new ArrayList<GameItem>();
    }

    public GameItemStairs(Mesh mesh) {
        position = new Vector3f(0, 0, 0);
        scale = 0.5f;
        items = new ArrayList<GameItem>();

        GameItem gameItem01 = new GameItem(mesh);
        gameItem01.setScale(scale);
        gameItem01.setPosition(
        		position.x - scale,
        		position.y - scale,
        		position.z - scale);
        items.add(gameItem01);

        GameItem gameItem02 = new GameItem(mesh);
        gameItem02.setScale(scale);
        gameItem02.setPosition(
        		position.x + scale,
        		position.y - scale,
        		position.z - scale);
        items.add(gameItem02);

        GameItem gameItem03 = new GameItem(mesh);
        gameItem03.setScale(scale);
        gameItem03.setPosition(
        		position.x - scale,
        		position.y - scale,
        		position.z + scale);
        items.add(gameItem03);

        GameItem gameItem04 = new GameItem(mesh);
        gameItem04.setScale(scale);
        gameItem04.setPosition(
        		position.x + scale,
        		position.y - scale,
        		position.z + scale);
        items.add(gameItem04);

        GameItem gameItem05 = new GameItem(mesh);
        gameItem05.setScale(scale);
        gameItem05.setPosition(
        		position.x - scale,
        		position.y + scale,
        		position.z + scale);
        items.add(gameItem05);

        GameItem gameItem06 = new GameItem(mesh);
        gameItem06.setScale(scale);
        gameItem06.setPosition(
        		position.x + scale,
        		position.y + scale,
        		position.z + scale);
        items.add(gameItem06);
    }
    
    public void setPosition(float x, float y, float z) {
        for (GameItem item : items) {
        	item.setPosition(item.getPosition().x + x, item.getPosition().y + y, item.getPosition().z + z);
        } 
    }

    public List<GameItem> getItems() {
    	return items;
    }
}
