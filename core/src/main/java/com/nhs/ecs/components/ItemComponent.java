package com.nhs.ecs.components;

import com.nhs.ecs.Component;

public class ItemComponent implements Component {
    public String name;

    public ItemComponent(String name) {
        this.name = name;
    }
}
