package models;

import textures.ModelTexture;

/**
 * Created by Fauser on 25.07.17.
 */
public class TexturedModel {

    private RawModel rawModel;
    private ModelTexture texture;

    public TexturedModel(RawModel model, ModelTexture texture) {

        this.rawModel = model;
        this.texture = texture;

    }

    public RawModel getRawModel() {
        return rawModel;
    }


    public ModelTexture getTexture() {
        return texture;
    }

}
