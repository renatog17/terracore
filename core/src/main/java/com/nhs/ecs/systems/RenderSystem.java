package com.nhs.ecs.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.AnimationComponent;
import com.nhs.ecs.components.DirectionComponent;
import com.nhs.ecs.components.PositionComponent;
import com.nhs.ecs.components.SizeComponent;
import com.nhs.ecs.components.TextureComponent;
import com.nhs.ecs.components.VelocityComponent;

public class RenderSystem {

    private EntityManager em;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private static final float DEFAULT_SIZE = 32f;

    public RenderSystem(EntityManager em, SpriteBatch batch, OrthographicCamera camera) {
        this.em = em;
        this.batch = batch;
        this.camera = camera;
    }

    public void render() {

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float delta = Gdx.graphics.getDeltaTime();

        for (int id : em.getAllEntities()) {

            PositionComponent pos = em.getComponent(id, PositionComponent.class);
            if (pos == null) continue;

            SizeComponent size = em.getComponent(id, SizeComponent.class);

            float hitboxW = (size != null) ? size.width : DEFAULT_SIZE;
            float hitboxH = (size != null) ? size.height : DEFAULT_SIZE;

            // ===== ANIMAÇÃO =====
            if (em.hasComponent(id, AnimationComponent.class)) {

                AnimationComponent anim = em.getComponent(id, AnimationComponent.class);
                VelocityComponent vel = em.getComponent(id, VelocityComponent.class);

                anim.stateTime += delta;

                TextureRegion frame;

                boolean isInAir = vel != null && Math.abs(vel.vy) > 0.1f;
                boolean isMovingHorizontally = vel != null && Math.abs(vel.vx) > 0.1f;

                if (isInAir) {
                    // SPRITE DE PULO (posição 3)
                    frame = anim.frames[3];
                } else if (isMovingHorizontally) {
                    // ANIMAÇÃO DE CORRIDA (frames 1 e 2)
                    int frameIndex = 1 + (int)(anim.stateTime / anim.frameTime) % 2;
                    frame = anim.frames[frameIndex];
                } else {
                    // IDLE (frame 0)
                    frame = anim.frames[0];
                }

                float spriteW = frame.getRegionWidth();
                float spriteH = frame.getRegionHeight();

                float scale = hitboxH / spriteH;

                float drawW = spriteW * scale;
                float drawH = spriteH * scale;

                float drawX = pos.x + (hitboxW - drawW) / 2f;
                float drawY = pos.y;

                // ===== DIREÇÃO =====
                DirectionComponent dir = em.getComponent(id, DirectionComponent.class);
                boolean flipX = dir != null && !dir.facingRight;

                if (flipX) {
                    batch.draw(
                        frame,
                        drawX + drawW,
                        drawY,
                        -drawW,
                        drawH
                    );
                } else {
                    batch.draw(
                        frame,
                        drawX,
                        drawY,
                        drawW,
                        drawH
                    );
                }

                continue;
            }

            // ===== TEXTURA NORMAL =====
            if (em.hasComponent(id, TextureComponent.class)) {
                TextureComponent tex = em.getComponent(id, TextureComponent.class);
                batch.draw(tex.texture, pos.x, pos.y, hitboxW, hitboxH);
            }
        }

        batch.end();
    }
}
