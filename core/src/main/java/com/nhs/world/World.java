package com.nhs.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class World {

    private final int tileSize;
    private final int width;
    private final int height;

    private final int[][] tiles;

    private final Texture[] dirtTextures = new Texture[3];
    private final TextureRegion[] dirtTiles = new TextureRegion[3];

    // ===== EDGES =====
    private final Texture grassTTexture;
    private final TextureRegion grassT;

    private final Texture grassBTexture;
    private final TextureRegion grassB;

    private final Texture grassLTexture;
    private final TextureRegion grassL;

    private final Texture grassRTexture;
    private final TextureRegion grassR;

    // ===== CORNERS =====
    private final Texture grassTRTexture;
    private final TextureRegion grassTR;

    private final Texture grassTLTexture;
    private final TextureRegion grassTL;

    private final Texture grassBRTexture;
    private final TextureRegion grassBR;

    private final Texture grassBLTexture;
    private final TextureRegion grassBL;

    public World(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;

        this.tiles = new int[width][height];

        // ===== DIRT =====
        dirtTextures[0] = new Texture("dirt/dirt_1.png");
        dirtTextures[1] = new Texture("dirt/dirt_2.png");
        dirtTextures[2] = new Texture("dirt/dirt_3.png");

        for (int i = 0; i < 3; i++) {
            dirtTiles[i] = new TextureRegion(dirtTextures[i]);
        }

        // ===== EDGES =====
        grassTTexture = new Texture("grass_t.png");
        grassT = new TextureRegion(grassTTexture);

        grassBTexture = new Texture("grass_b.png");
        grassB = new TextureRegion(grassBTexture);

        grassLTexture = new Texture("grass_l.png");
        grassL = new TextureRegion(grassLTexture);

        grassRTexture = new Texture("grass_r.png");
        grassR = new TextureRegion(grassRTexture);

        // ===== CORNERS =====
        grassTRTexture = new Texture("grass_corner_t_r.png");
        grassTR = new TextureRegion(grassTRTexture);

        grassTLTexture = new Texture("grass_corner_t_l.png");
        grassTL = new TextureRegion(grassTLTexture);

        grassBRTexture = new Texture("grass_corner_b_r.png");
        grassBR = new TextureRegion(grassBRTexture);

        grassBLTexture = new Texture("grass_corner_b_l.png");
        grassBL = new TextureRegion(grassBLTexture);

        generateWorld();
    }

    private void generateWorld() {
        int baseHeight = 40;
        int currentHeight = baseHeight;

        for (int x = 0; x < width; x++) {
            int variation = (int) (Math.random() * 3) - 1;
            currentHeight += variation;

            if (currentHeight < 30) currentHeight = 30;
            if (currentHeight > 50) currentHeight = 50;

            for (int y = 0; y < height; y++) {
                if (y < currentHeight) {
                    tiles[x][y] = 1;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {

        // ===== DIRT =====
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] != 1) continue;

                float drawX = x * tileSize;
                float drawY = y * tileSize;

                int variation = getTileVariation(x, y);
                batch.draw(dirtTiles[variation], drawX, drawY, tileSize, tileSize);
            }
        }

        // ===== GRASS =====
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] != 1) continue;

                float drawX = x * tileSize;
                float drawY = y * tileSize;

                drawGrass(batch, x, y, drawX, drawY);
            }
        }
    }

    private void drawGrass(SpriteBatch batch, int x, int y, float drawX, float drawY) {

        final float thickness = 6f;
        final float overlap = thickness / 2f;

        boolean top = !hasBlock(x, y + 1);
        boolean bottom = !hasBlock(x, y - 1);
        boolean left = !hasBlock(x - 1, y);
        boolean right = !hasBlock(x + 1, y);

        // ===== EDGES =====
        if (top) {
            batch.draw(grassT, drawX - overlap, drawY + tileSize - overlap, tileSize + overlap * 2f, thickness);
        }

        if (bottom) {
            batch.draw(grassB, drawX - overlap, drawY - overlap, tileSize + overlap * 2f, thickness);
        }

        if (left) {
            batch.draw(grassL, drawX - overlap, drawY - overlap, thickness, tileSize + overlap * 2f);
        }

        if (right) {
            batch.draw(grassR, drawX + tileSize - overlap, drawY - overlap, thickness, tileSize + overlap * 2f);
        }

        // ===== CORNERS (render depois) =====
        if (top && right) {
            batch.draw(grassTR, drawX + tileSize - overlap, drawY + tileSize - overlap, thickness, thickness);
        }

        if (top && left) {
            batch.draw(grassTL, drawX - overlap, drawY + tileSize - overlap, thickness, thickness);
        }

        if (bottom && right) {
            batch.draw(grassBR, drawX + tileSize - overlap, drawY - overlap, thickness, thickness);
        }

        if (bottom && left) {
            batch.draw(grassBL, drawX - overlap, drawY - overlap, thickness, thickness);
        }
    }

    private boolean hasBlock(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return false;
        return tiles[x][y] == 1;
    }

    private int getTileVariation(int x, int y) {
        int seed = x * 73428767 ^ y * 91227153;
        seed = (seed << 13) ^ seed;
        int result = seed * (seed * seed * 15731 + 789221) + 1376312589;
        return Math.abs(result) % 3;
    }

    public int[][] getTiles() {
        return tiles;
    }

    public int getWidthPixels() {
        return width * tileSize;
    }

    public int getHeightPixels() {
        return height * tileSize;
    }

    public void dispose() {
        for (Texture t : dirtTextures) {
            t.dispose();
        }

        grassTTexture.dispose();
        grassBTexture.dispose();
        grassLTexture.dispose();
        grassRTexture.dispose();

        grassTRTexture.dispose();
        grassTLTexture.dispose();
        grassBRTexture.dispose();
        grassBLTexture.dispose();
    }
}
