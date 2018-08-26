package engine.tm.textures;

public class ModelTexture {

	private int textureID;
	
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private boolean isTransparent = false;
	private boolean useFakeLighting = false;

	public ModelTexture(int id) {
		this.textureID = id;
	}
	
	public int getID() {
		return this.textureID;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public ModelTexture setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
		return this;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public ModelTexture setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
		return this;
	}

	public boolean isTransparent() {
		return isTransparent;
	}

	public ModelTexture setTransparent(boolean isTransparent) {
		this.isTransparent = isTransparent;
		return this;
	}

	public boolean useFakeLighting() {
		return useFakeLighting;
	}

	public ModelTexture setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
		return this;
	}

}
