package textures;


public class ModelTexture {

    private int textureID;

    private float shineDumper = 12;
    private float reflectivity = 0.5f;

    private boolean transparent = false;

    public ModelTexture(int id) {
        this.textureID = id;
    }

    public int getID() {
        return this.textureID;
    }

    public float getShineDumper() {
        return shineDumper;
    }

    public void setShineDumper(float shineDumper) {
        this.shineDumper = shineDumper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }
}
