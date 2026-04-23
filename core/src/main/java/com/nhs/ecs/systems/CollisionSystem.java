package com.nhs.ecs.systems;

import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.GroundedComponent;
import com.nhs.ecs.components.PositionComponent;
import com.nhs.ecs.components.SizeComponent;
import com.nhs.ecs.components.VelocityComponent;

public class CollisionSystem {

    private final EntityManager em;
    private final int[][] world;
    private final int tileSize;

    public CollisionSystem(EntityManager em, int[][] world, int tileSize) {
        this.em = em;
        this.world = world;
        this.tileSize = tileSize;
    }

    public void update(float delta) {
        for (int id : em.getAllEntities()) {
            PositionComponent pos = em.getComponent(id, PositionComponent.class);
            VelocityComponent vel = em.getComponent(id, VelocityComponent.class);
            SizeComponent size = em.getComponent(id, SizeComponent.class);
            GroundedComponent grounded = em.getComponent(id, GroundedComponent.class);

            if (pos == null || vel == null || grounded == null) continue;

            float width = (size != null) ? size.width : tileSize;
            float height = (size != null) ? size.height : tileSize;

            grounded.grounded = false;

            float prevX = pos.x - (vel.vx * delta);
            float prevY = pos.y - (vel.vy * delta);

            resolveX(pos, vel, width, height, prevY);
            resolveY(pos, vel, width, height, grounded, prevX);
        }
    }

    private void resolveX(PositionComponent pos, VelocityComponent vel, float width, float height, float yForTest) {
        if (!isColliding(pos.x, yForTest, width, height)) {
            return;
        }

        if (vel.vx > 0) {
            int tileX = (int) ((pos.x + width - 1f) / tileSize);
            pos.x = tileX * tileSize - width;
        } else if (vel.vx < 0) {
            int tileX = (int) (pos.x / tileSize);
            pos.x = (tileX + 1) * tileSize;
        }

        vel.vx = 0;
    }

    private void resolveY(PositionComponent pos, VelocityComponent vel, float width, float height, GroundedComponent grounded, float xForTest) {
        if (!isColliding(xForTest, pos.y, width, height)) {
            return;
        }

        if (vel.vy > 0) {
            int tileY = (int) ((pos.y + height - 1f) / tileSize);
            pos.y = tileY * tileSize - height;
            vel.vy = 0;
        } else if (vel.vy < 0) {
            int tileY = (int) (pos.y / tileSize);
            pos.y = (tileY + 1) * tileSize;
            vel.vy = 0;
            grounded.grounded = true;
        }
    }

    private boolean isColliding(float x, float y, float width, float height) {
        int startX = (int) (x / tileSize);
        int endX = (int) ((x + width - 1f) / tileSize);

        int startY = (int) (y / tileSize);
        int endY = (int) ((y + height - 1f) / tileSize);

        for (int tx = startX; tx <= endX; tx++) {
            for (int ty = startY; ty <= endY; ty++) {

                if (tx < 0 || ty < 0 || tx >= world.length || ty >= world[0].length) {
                    return true;
                }

                if (world[tx][ty] != 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
