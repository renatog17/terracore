package com.nhs.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nhs.ecs.Entity;
import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.*;
import com.nhs.ecs.systems.CollisionSystem;
import com.nhs.ecs.systems.PhysicsSystem;
import com.nhs.ecs.systems.PlayerInputSystem;
import com.nhs.ecs.systems.RenderSystem;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;

    private Texture enemyTexture;
    private Texture itemTexture;
    private Texture dirt;
    private Texture grass;

    private Texture playerIdleTex;
    private Texture playerWalk1Tex;
    private Texture playerWalk2Tex;

    private TextureRegion[] playerFrames = new TextureRegion[3];

    private OrthographicCamera camera;
    private Viewport viewport;

    private EntityManager em;
    private PlayerInputSystem playerInputSystem;
    private RenderSystem renderSystem;
    private PhysicsSystem physicsSystem;
    private CollisionSystem collisionSystem;

    private int playerId;

    private final int TILE_SIZE = 32;
    private final int WORLD_WIDTH = 100;
    private final int WORLD_HEIGHT = 100;
    private int[][] world;

    @Override
    public void create() {

        batch = new SpriteBatch();

        // ===== PLAYER SPRITES =====
        playerIdleTex = new Texture("player_default_teste.png");
        playerWalk1Tex = new Texture("player_walk1.png");
        playerWalk2Tex = new Texture("player_walk2.png");

        playerFrames[0] = new TextureRegion(playerIdleTex);
        playerFrames[1] = new TextureRegion(playerWalk1Tex);
        playerFrames[2] = new TextureRegion(playerWalk2Tex);

        // ===== OUTRAS TEXTURAS =====
        enemyTexture = createTexture(Color.BLUE);
        itemTexture = createTexture(Color.YELLOW);
        dirt = createTexture(new Color(0.55f, 0.27f, 0.07f, 1f));
        grass = createTexture(new Color(0.3f, 0.8f, 0.3f, 1f));

        // ===== CAMERA =====
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(800, 600, camera);
        viewport.apply();

        // ===== MUNDO =====
        world = new int[WORLD_WIDTH][WORLD_HEIGHT];
        generateWorld();

        // ===== ECS =====
        em = new EntityManager();

        playerInputSystem = new PlayerInputSystem(em);
        renderSystem = new RenderSystem(em, batch, camera);

        physicsSystem = new PhysicsSystem(em);
        collisionSystem = new CollisionSystem(em, world, TILE_SIZE);

        createEntities();
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

        Entity player = em.createEntity();
        playerId = player.id;

        em.addComponent(player, new SizeComponent(1.5f * TILE_SIZE, 3f * TILE_SIZE));

        float x = 10 * TILE_SIZE;
        float y = 41 * TILE_SIZE;

        em.addComponent(player, new PositionComponent(x, y));
        em.addComponent(player, new VelocityComponent());
        em.addComponent(player, new PlayerComponent());
        em.addComponent(player, new AnimationComponent(playerFrames, 0.2f));
        em.addComponent(player, new GravityComponent());
        em.addComponent(player, new DirectionComponent());
        em.addComponent(player, new GroundedComponent());

        Entity enemy = em.createEntity();
        em.addComponent(enemy, new PositionComponent(300, 350));
        em.addComponent(enemy, new VelocityComponent());
        em.addComponent(enemy, new TextureComponent(enemyTexture));
        em.addComponent(enemy, new EnemyComponent());

        Entity item = em.createEntity();
        em.addComponent(item, new PositionComponent(200, 350));
        em.addComponent(item, new TextureComponent(itemTexture));
        em.addComponent(item, new ItemComponent("Coin"));
    }

    private void generateWorld() {

        int baseHeight = 40;
        int currentHeight = baseHeight;

        for (int x = 0; x < WORLD_WIDTH; x++) {

            int variation = (int)(Math.random() * 3) - 1;
            currentHeight += variation;

            if (currentHeight < 30) currentHeight = 30;
            if (currentHeight > 50) currentHeight = 50;

            for (int y = 0; y < WORLD_HEIGHT; y++) {

                if (y < currentHeight) {
                    world[x][y] = 1;
                } else if (y == currentHeight) {
                    world[x][y] = 2;
                }
            }
        }
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        // ===== ECS PIPELINE CORRETO =====

        playerInputSystem.update();
        physicsSystem.update(delta);
        collisionSystem.update(delta);
        renderSystem.render();    // input

        // ===== CAMERA =====
        PositionComponent playerPos = em.getComponent(playerId, PositionComponent.class);
        if (playerPos == null) return;

        float worldWidthPixels = WORLD_WIDTH * TILE_SIZE;
        float worldHeightPixels = WORLD_HEIGHT * TILE_SIZE;

        float halfW = viewport.getWorldWidth() / 2f;
        float halfH = viewport.getWorldHeight() / 2f;

        float camX = Math.max(halfW, Math.min(playerPos.x, worldWidthPixels - halfW));
        float camY = Math.max(halfH, Math.min(playerPos.y, worldHeightPixels - halfH));

        camera.position.set(camX, camY, 0);
        camera.update();

        // ===== CLEAR =====
        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ===== WORLD =====
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {

                float drawX = x * TILE_SIZE;
                float drawY = y * TILE_SIZE;

                int tile = world[x][y];

                if (tile == 1) batch.draw(dirt, drawX, drawY);
                else if (tile == 2) batch.draw(grass, drawX, drawY);
            }
        }

        batch.end();

        // ===== ENTITIES =====
        renderSystem.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();

        playerIdleTex.dispose();
        playerWalk1Tex.dispose();
        playerWalk2Tex.dispose();

        enemyTexture.dispose();
        itemTexture.dispose();
        dirt.dispose();
        grass.dispose();
    }
}
