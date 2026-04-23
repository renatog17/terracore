package com.nhs.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nhs.ecs.Entity;
import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.*;
import com.nhs.ecs.systems.MovementSystem;
import com.nhs.ecs.systems.PlayerInputSystem;
import com.nhs.ecs.systems.RenderSystem;

public class Main extends ApplicationAdapter {

    // ===== RENDER =====
    private SpriteBatch batch;

    // ===== TEXTURAS =====
    private Texture playerTexture;
    private Texture enemyTexture;
    private Texture itemTexture;
    private Texture dirt;
    private Texture grass;

    // ===== CAMERA =====
    private OrthographicCamera camera;
    private Viewport viewport;

    // ===== ECS =====
    private EntityManager em;
    private PlayerInputSystem playerInputSystem;
    private MovementSystem movementSystem;
    private RenderSystem renderSystem;

    private int playerId;

    // ===== MUNDO =====
    private final int TILE_SIZE = 32;
    private final int WORLD_WIDTH = 100;
    private final int WORLD_HEIGHT = 100;
    private int[][] world;

    @Override
    public void create() {

        batch = new SpriteBatch();

        // ===== TEXTURAS =====
        playerTexture = createTexture(Color.RED);
        enemyTexture = createTexture(Color.BLUE);
        itemTexture = createTexture(Color.YELLOW);
        dirt = createTexture(new Color(0.55f, 0.27f, 0.07f, 1f));
        grass = createTexture(new Color(0.3f, 0.8f, 0.3f, 1f));

        // ===== CAMERA =====
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(800, 600, camera);
        viewport.apply();

        // ===== ECS =====
        em = new EntityManager();

        playerInputSystem = new PlayerInputSystem(em);
        movementSystem = new MovementSystem(em);
        renderSystem = new RenderSystem(em, batch, camera);

        // ===== ENTIDADES =====
        createEntities();

        // ===== MUNDO =====
        world = new int[WORLD_WIDTH][WORLD_HEIGHT];
        generateWorld();


    }

    private Texture createTexture(Color color) {
        Pixmap map = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        map.setColor(color);
        map.fill();
        Texture tex = new Texture(map);
        map.dispose();
        return tex;
    }

    private void createEntities() {

        // PLAYER
        Entity player = em.createEntity();
        playerId = player.id;

        em.addComponent(player, new PositionComponent(100, 300));
        em.addComponent(player, new VelocityComponent());
        em.addComponent(player, new TextureComponent(playerTexture));
        em.addComponent(player, new PlayerComponent());

        // ENEMY
        Entity enemy = em.createEntity();
        em.addComponent(enemy, new PositionComponent(300, 300));
        em.addComponent(enemy, new VelocityComponent());
        em.addComponent(enemy, new TextureComponent(enemyTexture));
        em.addComponent(enemy, new EnemyComponent());

        // ITEM
        Entity item = em.createEntity();
        em.addComponent(item, new PositionComponent(200, 350));
        em.addComponent(item, new TextureComponent(itemTexture));
        em.addComponent(item, new ItemComponent("Coin"));
    }

    private void generateWorld() {
        int groundHeight = 40;

        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {

                if (y < groundHeight) world[x][y] = 1;
                if (y == groundHeight) world[x][y] = 2;
            }
        }
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // ===== UPDATE ECS =====
        playerInputSystem.update();
        movementSystem.update(delta);

        // ===== PEGAR POSIÇÃO DO PLAYER =====
        PositionComponent playerPos = em.getComponent(playerId, PositionComponent.class);
        if (playerPos == null) return;

        float playerX = playerPos.x;
        float playerY = playerPos.y;

        // ===== CAMERA CLAMP =====
        float worldWidthPixels = WORLD_WIDTH * TILE_SIZE;
        float worldHeightPixels = WORLD_HEIGHT * TILE_SIZE;

        float halfW = viewport.getWorldWidth() / 2;
        float halfH = viewport.getWorldHeight() / 2;

        float camX = Math.max(halfW, Math.min(playerX, worldWidthPixels - halfW));
        float camY = Math.max(halfH, Math.min(playerY, worldHeightPixels - halfH));

        camera.position.set(camX, camY, 0);
        camera.update();

        // ===== CLEAR =====
        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        // ===== RENDER MUNDO =====
        batch.begin();

        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {

                float drawX = x * TILE_SIZE;
                float drawY = y * TILE_SIZE;

                if (world[x][y] == 1) batch.draw(dirt, drawX, drawY);
                if (world[x][y] == 2) batch.draw(grass, drawX, drawY);
            }
        }

        batch.end();

        // ===== RENDER ENTIDADES (ECS) =====
        renderSystem.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        playerTexture.dispose();
        enemyTexture.dispose();
        itemTexture.dispose();
        dirt.dispose();
        grass.dispose();
    }
}
