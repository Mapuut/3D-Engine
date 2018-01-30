package renderEngine;


import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.window.Window;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClearColor;

public class MasterRenderer {


    private static final float FOV = 90f;
    private static final float NEAR_PLANE = 1f;
    private static final float FAR_PLANE = 2000f;

    private static final Vector3f skyColour= new Vector3f(0.5f, 0.5f, 0.5f);

    public Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer entityRenderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer() {
        enableCulling();
        createProjectionMatrix(shader);
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public void render(Light sun, Camera camera) {
        prepare();
        shader.start();
        shader.loadSkyColour(skyColour);
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);

        entityRenderer.render(entities);

        shader.stop();
        entities.clear();

        terrainShader.start();
        terrainShader.loadSkyColour(skyColour);
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);

        terrainRenderer.render(terrains);

        terrainShader.stop();
        terrains.clear();

    }

    public static void enableCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public static void disableCulling() {
        glDisable(GL_CULL_FACE);
    }

     public void processTerrain(Terrain terrain){
        terrains.add(terrain);
     }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);

        if(batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }


    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }


    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(skyColour.getX(), skyColour.getY(), skyColour.getZ(), 1.0f);
    }

    public void createProjectionMatrix(StaticShader shader){
        float aspectRatio = (float) Window.WIDTH / (float) Window.HEIGHT;

        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV/2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;
        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;






//        float aspectRatio = (float) Window.WIDTH / (float) Window.HEIGHT;
//        float halfWidth = 1.0f;
//        float halfHeight = halfWidth/aspectRatio;
//
//        float right = halfWidth;
//        float left = -halfWidth;
//
//        float top = halfHeight;
//        float bottom = -halfHeight;
//
//        projectionMatrix = new Matrix4f();
//
//        projectionMatrix.setIdentity();
//
//        projectionMatrix.m00 = 2*NEAR_PLANE/(right - left);
//        projectionMatrix.m11 = 2*NEAR_PLANE/(top - bottom);
//        projectionMatrix.m22 = -(FAR_PLANE +NEAR_PLANE)/(FAR_PLANE - NEAR_PLANE);
//        projectionMatrix.m23 = -1;
//        projectionMatrix.m32 = -2*FAR_PLANE*NEAR_PLANE/(FAR_PLANE - NEAR_PLANE);
//        projectionMatrix.m20 = (right+left)/(right -left);
//        projectionMatrix.m21 = (top + bottom)/(top-bottom);
//        projectionMatrix.m33 = 0;



        float halfWidth = Window.WIDTH / 2;
        float halfHeight = Window.HEIGHT / 2;
        //projectionMatrix = orthogonal(- halfWidth, halfWidth, - halfHeight, halfHeight, -1, 10000);
        //projectionMatrix = perspective(- 1f, 1f, - 1f / ((float) Window.WIDTH / (float) Window.HEIGHT), 1f / ((float) Window.WIDTH / (float) Window.HEIGHT), 1f, 1000);


        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    // creates a Matrix4f that generates a perspective projection

    public static Matrix4f perspective(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix4f matrix = new Matrix4f();

        matrix.setIdentity();

        matrix.m00 = 2*near/(right - left);
        matrix.m11 = 2*near/(top - bottom);
        matrix.m22 = -(far +near)/(far - near);
        matrix.m23 = -1;
        matrix.m32 = -2*far*near/(far - near);
        matrix.m20 = (right+left)/(right -left);
        matrix.m21 = (top + bottom)/(top-bottom);
        matrix.m33 = 0;

        return matrix;
    }


    // creates a Matrix4f that generates an orthogonal projection
    public static Matrix4f orthogonal(float left,float right,float bottom,float top,float near,float far)
    {
        Matrix4f matrix = new Matrix4f();

        matrix.setIdentity();

        matrix.m00 = 2/(right - left);
        matrix.m11 = 2/(top - bottom);
        matrix.m22 = -2/(far - near);
        matrix.m32 = (far+near)/(far - near);
        matrix.m30 = (right+left)/(right -left);
        matrix.m31 = (top + bottom)/(top-bottom);

        return matrix;
    }
}
