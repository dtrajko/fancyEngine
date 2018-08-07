package game2D.frogger;

public interface ITileType {

	byte getId();
	boolean isSolid();
	String getTexture();
	boolean isNextLevel();
	boolean isPreviousLevel();

}
