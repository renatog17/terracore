package com.nhs.ecs.systems;

import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.GravityComponent;
import com.nhs.ecs.components.PositionComponent;
import com.nhs.ecs.components.VelocityComponent;

public class PhysicsSystem {

    private final EntityManager em;

    public PhysicsSystem(EntityManager em) {
        this.em = em;
    }

    public void update(float delta) {
        for (int id : em.getAllEntities()) {
            PositionComponent pos = em.getComponent(id, PositionComponent.class);
            VelocityComponent vel = em.getComponent(id, VelocityComponent.class);

            if (pos == null || vel == null) continue;

            GravityComponent gravity = em.getComponent(id, GravityComponent.class);
            if (gravity != null) {
                vel.vy += gravity.gravity * delta;
            }

            pos.x += vel.vx * delta;
            pos.y += vel.vy * delta;
        }
    }
}
