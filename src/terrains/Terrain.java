package terrains;


import models.RawModel;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import textures.ModelTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Terrain {

    public static final float SIZE = 400;
    private static final int VERTEX_COUNT = 256;

    public float[][] heights = new float[VERTEX_COUNT][VERTEX_COUNT];

    private float x;
    private float z;
    private static RawModel model;
    private static ModelTexture texture;

    public Terrain(int gridX, int gridZ, Loader loader, ModelTexture texture) {
        this.texture = texture;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader);
    }

    public Terrain(int gridX, int gridZ, String heightMap, Loader loader, ModelTexture texture) {
        this.texture = texture;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader, heightMap);
    }

    private RawModel generateTerrain(Loader loader, String heightMap){

        BufferedImage image = null;
        try {
            image = ImageIO.read(Terrain.class.getClassLoader().getResourceAsStream(heightMap));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[] pixels = image.getRGB(0,0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer*3+1] = (pixels[i * VERTEX_COUNT + j] & 0xff) - 128;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;

                Vector3f normal = calculateNormal(j, i, VERTEX_COUNT, pixels);

                normals[vertexPointer*3] = normal.getX();
                normals[vertexPointer*3+1] = normal.getY();
                normals[vertexPointer*3+2] = normal.getZ();
                textureCoords[vertexPointer*2] = (float)j;
                textureCoords[vertexPointer*2+1] = (float)i;
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormal(int x , int y, int witdh, int[] pixels) {
        float heightL = getHeight(x - 1 + y * witdh, pixels);
        float heightR = getHeight(x + 1 + y * witdh, pixels);
        float heightD = getHeight(x + (y - 1) * witdh, pixels);
        float heightU = getHeight(x + (y + 1) * witdh, pixels);

        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();
        return normal;
    }

    private float getHeight(int index, int[] pixels) {
        if(index < 0 || index >= pixels.length) return 0;
        return (pixels[index] & 0xff) - 128;
    }




    private RawModel generateTerrain(Loader loader){
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer*3+1] = 0;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                normals[vertexPointer*3] = 0;
                normals[vertexPointer*3+1] = 1;
                normals[vertexPointer*3+2] = 0;
                textureCoords[vertexPointer*2] = (float)j;
                textureCoords[vertexPointer*2+1] = (float)i;
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public RawModel getModel() {
        return model;
    }

    public void setModel(RawModel model) {
        this.model = model;
    }

    public ModelTexture getTexture() {
        return texture;
    }

    public void setTexture(ModelTexture texture) {
        this.texture = texture;
    }
}
