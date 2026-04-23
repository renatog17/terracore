package com.nhs.ecs.components;

import com.badlogic.gdx.graphics.Texture;
import com.nhs.ecs.Component;

public class TextureComponent implements Component {
    public Texture texture;

    public TextureComponent(Texture texture) {
        this.texture = texture;
    }
}
