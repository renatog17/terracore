package com.nhs.ecs.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nhs.ecs.Component;

public class AnimationComponent implements Component {

    public TextureRegion[] frames;
    public float frameTime = 0.2f;

    public float stateTime = 0f;
    public int currentFrame = 0;

    public AnimationComponent(TextureRegion[] frames, float frameTime) {
        this.frames = frames;
        this.frameTime = frameTime;
    }
}
