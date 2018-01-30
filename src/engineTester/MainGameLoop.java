package engineTester;


import Debug.Exceptions.OldComputerException;
import Input.Keyboard;
import Input.Mouse;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.Version;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import models.RawModel;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import renderEngine.window.Window;
import shaders.SunShader;
import terrains.Terrain;
import textures.ModelTexture;
import toolbox.Maths;

import java.nio.IntBuffer;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;


public class MainGameLoop {
    private static Window window;

    private static final int TARGET_UPS = 120;


    public static void main(String poop[]){

        
        try {
            window = new Window();
        } catch (OldComputerException e) {
            e.printStackTrace();
            System.exit(1);
        }

        loop();

    }




    //Loop where magic happens.
    private static void loop() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        if (Window.DEBUG) {
            System.out.println("LWJGL " + Version.getVersion() + "!");
            System.out.println("OpenGL " + glGetString(GL_VERSION));
        }


        // Game loop.
        long lastTime = System.nanoTime();
        final long updateTime = 1000000000 / TARGET_UPS;
        double updates = 0;

        boolean render = false;

        Loader loader = new Loader();


        RawModel model = OBJLoader.loadObjectModel("models/plane.obj", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("MenTransparent.png"));

        TexturedModel texturedModel = new TexturedModel(model, texture);

        texture.setReflectivity(1);
        texture.setShineDumper(10f);
        Random random = new Random();



        final int amount = 200;
        final int treesAmount = 200;
        final int terrainWidth = 5;
        final int terrainHeight = 5;
        Entity[] entities = new Entity[amount];
        Entity[] trees = new Entity[treesAmount];
        Terrain[] terrains = new Terrain[terrainWidth * terrainHeight];

        final int x_axis = terrainWidth * (int)Terrain.SIZE;
        final int y_axis = 1;
        final int z_axis = terrainHeight * (int)Terrain.SIZE;
        for (int i = 0; i < amount; i++) {
            entities[i] = new Entity(texturedModel, new Vector3f(random.nextInt(x_axis), 0.5f, random.nextInt(z_axis)), 0, random.nextInt(360), 0, (float)20);
            entities[i].position2 = new Vector3f(random.nextInt(x_axis), 0.5f, random.nextInt(z_axis));
            glBindVertexArray(entities[i].getModel().getRawModel().getVaoID());


            //entities[i].getModel().getTexture().setTransparent(true);
            entities[i].getModel().getTexture().setReflectivity(0.2f);
            entities[i].getModel().getTexture().setShineDumper(10);
        }

        TexturedModel treeTexturedModel = new TexturedModel(OBJLoader.loadObjectModel("models/tree.obj", loader),  new ModelTexture(loader.loadTexture("tree.png")));
        for (int i = 0; i < treesAmount; i+= 2) {
            trees[i] = new Entity(treeTexturedModel, new Vector3f(random.nextInt(x_axis), 0f, random.nextInt(z_axis)), 0, 0, 0, (float)2);
            trees[i].getModel().getTexture().setReflectivity(0.2f);
            trees[i].getModel().getTexture().setShineDumper(10);
        }
        treeTexturedModel = new TexturedModel(OBJLoader.loadObjectModel("models/lowPolyTree.obj", loader),  new ModelTexture(loader.loadTexture("lowPolyTree.png")));
        for (int i = 1; i < treesAmount; i+= 2) {
            trees[i] = new Entity(treeTexturedModel, new Vector3f(random.nextInt(x_axis), 0f, random.nextInt(z_axis)), 0, 0, 0, (float)0.3f + (float)random.nextGaussian() / 10);
            trees[i].getModel().getTexture().setReflectivity(0.2f);
            trees[i].getModel().getTexture().setShineDumper(10);
        }

        Loader.genMipMap = true;
        for (int y = 0; y < terrainHeight; y++) {
            for (int x = 0; x < terrainWidth; x++) {
                terrains[y * terrainWidth + x] = new Terrain(x, y, "heightmap.png", loader, new ModelTexture(loader.loadTexture("grass.png")));
                terrains[y * terrainWidth + x].getTexture().setReflectivity(0.05f);
                terrains[y * terrainWidth + x].getTexture().setShineDumper(10f);
            }
        }
        Loader.genMipMap = false;



        Light light = new Light(new Vector3f(1000,500,1000), new Vector3f(1,1,1));
        Vector3f lightCenter = new Vector3f(0,0,0);
        Vector3f lightPos = new Vector3f(1000,500,1000);
        double angle = 0;
        //OBJLoader.reverseNormals = true;
        Entity sun = new Entity( new TexturedModel(OBJLoader.loadObjectModel("models/sphereSmooth.obj", loader), new ModelTexture(loader.loadTexture("YellowPixel2.png"))), light.getPosition(), 0, 0, 0, (float)50);
        sun.getModel().getTexture().setReflectivity(0.5f);
        sun.getModel().getTexture().setShineDumper(1);
        //OBJLoader.reverseNormals = false;
        Camera camera = new Camera();



        glfwSetCursorPos(window.window_ID, Window.WIDTH / 2, Window.HEIGHT / 2);


        MasterRenderer renderer = new MasterRenderer();

        glfwSetCursorPos(window.window_ID, Window.WIDTH / 2, Window.HEIGHT / 2);
        Mouse.mouseX = Window.WIDTH / 2;
        Mouse.mouseY = Window.HEIGHT / 2;
        Mouse.reset();


        SunShader sunShader = new SunShader();
        sunShader.start();
        sunShader.loadProjectionMatrix(renderer.projectionMatrix);
        sunShader.stop();
        while (!glfwWindowShouldClose(window.window_ID)) {
            long currentTime = System.nanoTime();
            updates += (double)(currentTime - lastTime) / updateTime;
            lastTime = currentTime;

            while (updates >= 1) {
                glfwPollEvents();

                if (Keyboard.isKeyDown(GLFW_KEY_ESCAPE)) glfwSetWindowShouldClose(window.window_ID, true);
                Keyboard.update();
                window.update();
                Mouse.update();

                camera.pitch += Mouse.mouseMovedY() / 10f;
                camera.yaw += Mouse.mouseMovedX() / 10f;


                glfwSetCursorPos(window.window_ID, Window.WIDTH / 2, Window.HEIGHT / 2);
                Mouse.mouseX = Window.WIDTH / 2;
                Mouse.mouseY = Window.HEIGHT / 2;
                Mouse.reset();


                angle += 0.00f;


                light.setPosition(Maths.rotateVector(lightPos, lightCenter, angle));
                sun.setPosition(light.getPosition());

                for (int i = 0; i < amount; i++) {
                    entities[i].increaseRotation(0.0f, 0.5f, 0.0f);
                    entities[i].timer--;
                    if (entities[i].timer <= 0) {
                        entities[i].position2 = new Vector3f(random.nextInt(x_axis), 0.5f, random.nextInt(z_axis));
                        double dist_x = entities[i].position2.getX() - entities[i].getPosition().getX();
                        double dist_z = entities[i].position2.getZ() - entities[i].getPosition().getZ();


                        entities[i].timer = (int)(Math.sqrt(dist_x * dist_x + dist_z * dist_z) * 100);
                    } else {
                        entities[i].increasePosition((entities[i].position2.getX() - entities[i].getPosition().getX()) / entities[i].timer, 0, (entities[i].position2.getZ() - entities[i].getPosition().getZ()) / entities[i].timer);
                    }
                }

                camera.move();

                updates -= 1;
                render = true;
            }

            if(render) {
                render = false;


                glfwGetFramebufferSize(window.window_ID, width, height);
                width.rewind();
                height.rewind();

                int cur_width = width.get();
                int cur_height = height.get();
                if (Window.WIDTH != cur_width || Window.HEIGHT != cur_height) {
                    Window.WIDTH = cur_width;
                    Window.HEIGHT = cur_height;
                    glViewport(0, 0, cur_width, cur_height);
                }

                width.flip();
                height.flip();


                window.render();




                for (int i = 0; i < amount; i++) {
                    renderer.processEntity(entities[i]);
                }

                for (int i = 0; i < treesAmount; i++) {
                    renderer.processEntity(trees[i]);
                }

                for (int i = 0; i < terrainHeight * terrainWidth; i++) {
                    renderer.processTerrain(terrains[i]);
                }


                renderer.render(light, camera);


                //////////////////////////////////////////////////////////////
                //////////////////////////////SUN/////////////////////////////
                //////////////////////////////////////////////////////////////

                sunShader.start();
                sunShader.loadLight(light);
                sunShader.loadViewMatrix(camera);

                RawModel rawModel = sun.getModel().getRawModel();

                glBindVertexArray(rawModel.getVaoID());
                glEnableVertexAttribArray(0);
                glEnableVertexAttribArray(1);
                glEnableVertexAttribArray(2);

                ModelTexture textureSun = sun.getModel().getTexture();



                sunShader.loadShineVariables(textureSun.getShineDumper(), textureSun.getReflectivity());
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, textureSun.getID());




                Matrix4f transformationMatrix = Maths.createTransformationMatrix(sun.getPosition(), sun.getRotX(), sun.getRotY(), sun.getRotZ(), sun.getScale());
                sunShader.loadTransformationMatrix(transformationMatrix);



                glDrawElements(GL_TRIANGLES, rawModel.getVertexCount(), GL_UNSIGNED_INT,0);


                sunShader.stop();
                //////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////

                glfwSwapBuffers(window.window_ID);
            }



//            long sleepTime = (currentTime + updateTime - System.nanoTime());
//
//            if (sleepTime >= 1000){
//                LockSupport.parkNanos(sleepTime);
//            }

        }

        renderer.cleanUp();

        loader.cleanUp();
    }

    private static IntBuffer width = MemoryUtil.memAllocInt(1);
    private static IntBuffer height = MemoryUtil.memAllocInt(1);
}
