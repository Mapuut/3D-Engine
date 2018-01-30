package renderEngine;

import models.RawModel;
import org.lwjgl.BufferUtils;
import toolbox.PNGDecoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;


public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();


    public void cleanUp() {
        for(int vao:vaos) {
            glDeleteVertexArrays(vao);
        }

        for(int vbo:vbos) {
            glDeleteBuffers(vbo);
        }

        for(int texture:textures) {
            glDeleteTextures(texture);
        }
    }


    static byte pattern[] = {
            0, 32,  8, 40,  2, 34, 10, 42,   /* 8x8 Bayer ordered dithering  */
            48, 16, 56, 24, 50, 18, 58, 26,  /* pattern.  Each input pixel   */
            12, 44,  4, 36, 14, 46,  6, 38,  /* is scaled to the 0..63 range */
            60, 28, 52, 20, 62, 30, 54, 22,  /* before looking in this table */
            3, 35, 11, 43,  1, 33,  9, 41,   /* to determine the action.     */
            51, 19, 59, 27, 49, 17, 57, 25,
            15, 47,  7, 39, 13, 45,  5, 37,
            63, 31, 55, 23, 61, 29, 53, 21 };

    static byte pattern2[] = {
            (byte)0,(byte)255,(byte)0,(byte)255,
            0,0,0,(byte)255,
            (byte)0,(byte)255,(byte)0,(byte)255,
            0,0,0,(byte)255
    };


    public static int loadDithering() {

        ByteBuffer buf = ByteBuffer.allocateDirect(pattern.length);
        buf.put(pattern);
        buf.flip();

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);


        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, 8, 8, 0, GL_RED, GL_UNSIGNED_BYTE, buf);
        return textureID;
    }

    public static boolean genMipMap = false;
    public int loadTexture(String filename){
        InputStream imageData = Loader.class.getClassLoader().getResourceAsStream(filename);

        try {
            PNGDecoder decoder = new PNGDecoder(imageData);

            ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
            decoder.decode(buf, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
            buf.flip();


            int textureID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureID);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

            if (genMipMap){
                glGenerateMipmap(GL_TEXTURE_2D);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
                //glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -0.4f);
            }

            return textureID;

        }catch (IOException e){
            e.printStackTrace();
        }

        return -1;


//        byte[] bytes = null;
//        try {
//            bytes = IOUtils.readFully(imageData, Integer.MAX_VALUE, true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (bytes == null) {
//            System.out.println(filename + "bytes are null");
//        }
//        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
//        buffer.put(bytes);
//        buffer.flip();
//
//
//        IntBuffer width = BufferUtils.createIntBuffer(1);
//        IntBuffer height = BufferUtils.createIntBuffer(1);
//        IntBuffer comp = BufferUtils.createIntBuffer(1);
//
//        ByteBuffer data = stbi_load_from_memory(buffer, width, height, comp, 4);
//        int textureID = glGenTextures();
//
//        glBindTexture(GL_TEXTURE_2D, textureID);
//
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
//        stbi_image_free(data);
//        return textureID;
    }





    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    private int createVAO() {
        int vaoID = glGenVertexArrays();
        vaos.add(vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber,int size, float[] data) {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attributeNumber, size, GL_FLOAT,false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }


    private void bindIndicesBuffer(int[] indices) {
        int vboID = glGenBuffers();
        vbos.add(vboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}
