package com.nhs.ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.PlayerComponent;
import com.nhs.ecs.components.VelocityComponent;

public class PlayerInputSystem {

    private EntityManager em;
    private float speed = 200;

    public PlayerInputSystem(EntityManager em) {
        this.em = em;
    }

    public void update() {
        for (int id : em.getAllEntities()) {

            if (em.hasComponent(id, PlayerComponent.class)) {

                VelocityComponent vel = em.getComponent(id, VelocityComponent.class);

                vel.vx = 0;
                vel.vy = 0;

                if (Gdx.input.isKeyPressed(Input.Keys.A)) vel.vx = -speed;
                if (Gdx.input.isKeyPressed(Input.Keys.D)) vel.vx = speed;
                if (Gdx.input.isKeyPressed(Input.Keys.W)) vel.vy = speed;
                if (Gdx.input.isKeyPressed(Input.Keys.S)) vel.vy = -speed;
            }
        }
    }
}
