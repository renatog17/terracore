package com.nhs.ecs.components;

import com.nhs.ecs.Component;

public class PositionComponent implements Component {
    public float x, y;

    public PositionComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
