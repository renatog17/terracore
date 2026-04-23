package com.nhs.ecs.systems;

import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.PositionComponent;
import com.nhs.ecs.components.VelocityComponent;

public class MovementSystem {

    private EntityManager em;

    public MovementSystem(EntityManager em) {
        this.em = em;
    }

    public void update(float delta) {
        for (int id : em.getAllEntities()) {

            if (em.hasComponent(id, PositionComponent.class) &&
                em.hasComponent(id, VelocityComponent.class)) {

                PositionComponent pos = em.getComponent(id, PositionComponent.class);
                VelocityComponent vel = em.getComponent(id, VelocityComponent.class);

                if (pos == null || vel == null) continue;

                pos.x += vel.vx * delta;
                pos.y += vel.vy * delta;
            }
        }
    }
}
