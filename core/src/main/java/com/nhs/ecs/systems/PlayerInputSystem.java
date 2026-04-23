package com.nhs.ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.DirectionComponent;
import com.nhs.ecs.components.GroundedComponent;
import com.nhs.ecs.components.PlayerComponent;
import com.nhs.ecs.components.VelocityComponent;

public class PlayerInputSystem {

    private final EntityManager em;
    private float speed = 200f;
    private float jumpForce = 350f;

    private boolean jumpWasPressed = false;

    public PlayerInputSystem(EntityManager em) {
        this.em = em;
    }

    public void update() {
        boolean jumpNow = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        boolean jumpJustPressed = jumpNow && !jumpWasPressed;
        jumpWasPressed = jumpNow;

        boolean left = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.D);

        for (int id : em.getAllEntities()) {
            if (!em.hasComponent(id, PlayerComponent.class)) continue;

            VelocityComponent vel = em.getComponent(id, VelocityComponent.class);
            DirectionComponent dir = em.getComponent(id, DirectionComponent.class);
            GroundedComponent grounded = em.getComponent(id, GroundedComponent.class);

            if (vel == null) continue;

            vel.vx = 0;

            if (left) {
                vel.vx = -speed;
                if (dir != null) dir.facingRight = false;
            }

            if (right) {
                vel.vx = speed;
                if (dir != null) dir.facingRight = true;
            }

            if (grounded != null && grounded.grounded && jumpJustPressed) {
                vel.vy = jumpForce;
                grounded.grounded = false;
            }
        }
    }
}
