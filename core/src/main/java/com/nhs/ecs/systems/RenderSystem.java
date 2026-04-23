package com.nhs.ecs.systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.PositionComponent;
import com.nhs.ecs.components.TextureComponent;

public class RenderSystem {

    private EntityManager em;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    public RenderSystem(EntityManager em, SpriteBatch batch, OrthographicCamera camera) {
        this.em = em;
        this.batch = batch;
        this.camera = camera;
    }

    public void render() {

        // garante que usa a câmera correta
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        for (int id : em.getAllEntities()) {

            if (em.hasComponent(id, PositionComponent.class) &&
                em.hasComponent(id, TextureComponent.class)) {

                PositionComponent pos = em.getComponent(id, PositionComponent.class);
                TextureComponent tex = em.getComponent(id, TextureComponent.class);

                if (pos == null || tex == null) continue;

                batch.draw(tex.texture, pos.x, pos.y);
            }
        }

        batch.end();
    }
}
