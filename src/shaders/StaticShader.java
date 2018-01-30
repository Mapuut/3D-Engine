package shaders;


import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;


public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "shaders/fragmentShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    private int location_lightPosition;
    private int location_lightColour;

    private int location_shineDumper;
    private int location_reflectivity;

    private int location_skyColour;



    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");

        location_lightPosition = getUniformLocation("lightPosition");
        location_lightColour = getUniformLocation("lightColour");

        location_shineDumper = getUniformLocation("shineDamper");
        location_reflectivity = getUniformLocation("reflectivity");

        location_skyColour = getUniformLocation("skyColour");
    }

    public void loadSkyColour(Vector3f color) {
        super.loadVector(location_skyColour, color);
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDumper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }


    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColour, light.getColour());
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");

    }
}