package engine.tm.entities;

import org.joml.Vector3f;

import engine.IScene;
import engine.graph.Input;

public interface IPlayer {

	void move(float interval, Input input, IScene scene);
	Vector3f getPosition();
	float getRotX();
	float getRotY();
	float getRotZ();
	void update();

}
