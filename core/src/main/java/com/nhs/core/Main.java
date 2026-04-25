package com.nhs.core;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nhs.ecs.Entity;
import com.nhs.ecs.EntityManager;
import com.nhs.ecs.components.AnimationComponent;
import com.nhs.ecs.components.DirectionComponent;
import com.nhs.ecs.components.EnemyComponent;
import com.nhs.ecs.components.GroundedComponent;
import com.nhs.ecs.components.GravityComponent;
import com.nhs.ecs.components.ItemComponent;
import com.nhs.ecs.components.PlayerComponent;
import com.nhs.ecs.components.PositionComponent;
import com.nhs.ecs.components.SizeComponent;
import com.nhs.ecs.components.TextureComponent;
import com.nhs.ecs.components.VelocityComponent;
import com.nhs.ecs.systems.CollisionSystem;
import com.nhs.ecs.systems.PhysicsSystem;
import com.nhs.ecs.systems.PlayerInputSystem;
import com.nhs.ecs.systems.RenderSystem;
import com.nhs.world.World;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;

    private Texture enemyTexture;
    private Texture itemTexture;

    private Texture playerIdleTex;
    private Texture playerWalk1Tex;
    private Texture playerWalk2Tex;
    private Texture playerJumpTex;

    private TextureRegion[] playerFrames = new TextureRegion[4];

    private OrthographicCamera camera;
    private Viewport viewport;

    private EntityManager em;
    private PlayerInputSystem playerInputSystem;
    private RenderSystem renderSystem;
    private PhysicsSystem physicsSystem;
    private CollisionSystem collisionSystem;

    private World world;

    private int playerId;

    private final int TILE_SIZE = 20;
    private final int WORLD_WIDTH = 100;
    private final int WORLD_HEIGHT = 100;

    @Override
    public void create() {

        batch = new SpriteBatch();

        // ===== PLAYER SPRITES =====
        playerIdleTex = new Texture("player_default.png");
        playerWalk1Tex = new Texture("player_walk1.png");
        playerWalk2Tex = new Texture("player_walk2.png");
        playerJumpTex = new Texture("player_jump.png");

        playerFrames[0] = new TextureRegion(playerIdleTex);
        playerFrames[1] = new TextureRegion(playerWalk1Tex);
        playerFrames[2] = new TextureRegion(playerWalk2Tex);
        playerFrames[3] = new TextureRegion(playerJumpTex);

        // ===== OUTRAS TEXTURAS =====
        enemyTexture = createSolidTexture(0f, 0f, 1f, 1f);
        itemTexture = createSolidTexture(1f, 1f, 0f, 1f);

        // ===== CAMERA =====
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(800, 600, camera);
        viewport.apply();

        // ===== WORLD =====
        world = new World(WORLD_WIDTH, WORLD_HEIGHT, TILE_SIZE);

        // ===== ECS =====
        em = new EntityManager();

        playerInputSystem = new PlayerInputSystem(em);
        renderSystem = new RenderSystem(em, batch, camera);
        physicsSystem = new PhysicsSystem(em);
        collisionSystem = new CollisionSystem(em, world.getTiles(), TILE_SIZE);

        createEntities();
    }

    private Texture createSolidTexture(float r, float g, float b, float a) {
        Pixmap map = new Pixmap(TILE_SIZE, TILE_SIZE, Pixmap.Format.RGBA8888);
        map.setColor(r, g, b, a);
        map.fill();
        Texture tex = new Texture(map);
        map.dispose();
        return tex;
    }

    private void createEntities() {

        Entity player = em.createEntity();
        playerId = player.id;

        em.addComponent(player, new SizeComponent(1.5f * TILE_SIZE, 3f * TILE_SIZE));

        float x = 30 * TILE_SIZE;
        float y = 51 * TILE_SIZE;

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

    @Override
    public void render() {

        float delta = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);

        // ===== ECS =====
        playerInputSystem.update();
        physicsSystem.update(delta);
        collisionSystem.update(delta);

        // ===== CAMERA =====
        PositionComponent playerPos = em.getComponent(playerId, PositionComponent.class);
        if (playerPos == null) return;

        float halfW = viewport.getWorldWidth() / 2f;
        float halfH = viewport.getWorldHeight() / 2f;

        float camX = Math.max(halfW, Math.min(playerPos.x, world.getWidthPixels() - halfW));
        float camY = Math.max(halfH, Math.min(playerPos.y, world.getHeightPixels() - halfH));

        camera.position.set(camX, camY, 0);
        camera.update();

        // ===== CLEAR =====
        Gdx.gl.glClearColor(0.5f, 0.7f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        // ===== WORLD =====
        batch.begin();
        world.render(batch);
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
        playerJumpTex.dispose();

        enemyTexture.dispose();
        itemTexture.dispose();

        world.dispose();
    }
}
