package renderEngine;


import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TerrainRenderer {

    private TerrainShader shader;
    private int ditherAddress = Loader.loadDithering();

    private int tex0;
    private int tex1;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

        tex0 = glGetUniformLocation(shader.programID, "modelTexture");
        tex1 = glGetUniformLocation(shader.programID, "modelTexture2");

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        glUniform1i(tex0, 0);
        glUniform1i(tex1, 1);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, ditherAddress);
        shader.stop();

    }

    public void render(List<Terrain> terrains){

        for (Terrain terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);

            glDrawElements(GL_TRIANGLES, terrain.getModel().getVertexCount(), GL_UNSIGNED_INT,0);

            unbindTextureModel();
        }
    }


    private void prepareTerrain(Terrain terrain) {
        RawModel rawModel = terrain.getModel();

        glBindVertexArray(rawModel.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        ModelTexture texture = terrain.getTexture();
        shader.loadShineVariables(texture.getShineDumper(), texture.getReflectivity());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.getID());

    }

    private void unbindTextureModel() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
