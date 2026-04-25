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
    private final TextureRegion grassT;
    private final TextureRegion grassB;
    private final TextureRegion grassL;
    private final TextureRegion grassR;

    // ===== OUTER CORNERS =====
    private final TextureRegion grassTR;
    private final TextureRegion grassTL;
    private final TextureRegion grassBR;
    private final TextureRegion grassBL;

    // ===== INNER CORNERS =====
    private final TextureRegion grassInnerTR;
    private final TextureRegion grassInnerTL;
    private final TextureRegion grassInnerBR;
    private final TextureRegion grassInnerBL;

    private final Texture[] allTextures; // facilitar dispose

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

        // ===== LOAD =====
        Texture t, b, l, r;
        Texture tr, tl, br, bl;
        Texture itr, itl, ibr, ibl;

        t = new Texture("grass_t.png");
        b = new Texture("grass_b.png");
        l = new Texture("grass_l.png");
        r = new Texture("grass_r.png");

        tr = new Texture("grass_corner_t_r.png");
        tl = new Texture("grass_corner_t_l.png");
        br = new Texture("grass_corner_b_r.png");
        bl = new Texture("grass_corner_b_l.png");

        itr = new Texture("grass_inner_t_r.png");
        itl = new Texture("grass_inner_t_l.png");
        ibr = new Texture("grass_inner_b_r.png");
        ibl = new Texture("grass_inner_b_l.png");

        grassT = new TextureRegion(t);
        grassB = new TextureRegion(b);
        grassL = new TextureRegion(l);
        grassR = new TextureRegion(r);

        grassTR = new TextureRegion(tr);
        grassTL = new TextureRegion(tl);
        grassBR = new TextureRegion(br);
        grassBL = new TextureRegion(bl);

        grassInnerTR = new TextureRegion(itr);
        grassInnerTL = new TextureRegion(itl);
        grassInnerBR = new TextureRegion(ibr);
        grassInnerBL = new TextureRegion(ibl);

        allTextures = new Texture[] {
            t,b,l,r,tr,tl,br,bl,itr,itl,ibr,ibl
        };

        generateWorld();
    }

    private void generateWorld() {
        int baseHeight = 40;
        int currentHeight = baseHeight;

        for (int x = 0; x < width; x++) {
            int variation = (int)(Math.random() * 3) - 1;
            currentHeight += variation;

            currentHeight = Math.max(30, Math.min(50, currentHeight));

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

                int v = getTileVariation(x, y);
                batch.draw(dirtTiles[v], drawX, drawY, tileSize, tileSize);
            }
        }

        // ===== GRASS =====
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] != 1) continue;

                drawGrass(batch, x, y);
            }
        }
    }

    private void drawGrass(SpriteBatch batch, int x, int y) {

        float drawX = x * tileSize;
        float drawY = y * tileSize;

        final float thickness = 6f;
        final float o = thickness / 2f;

        boolean top = !hasBlock(x, y + 1);
        boolean bottom = !hasBlock(x, y - 1);
        boolean left = !hasBlock(x - 1, y);
        boolean right = !hasBlock(x + 1, y);

        // ===== EDGES (corrigido: sem extrapolar) =====
        if (top) {
            batch.draw(grassT, drawX, drawY + tileSize - o, tileSize, thickness);
        }

        if (bottom) {
            batch.draw(grassB, drawX, drawY - o, tileSize, thickness);
        }

        if (left) {
            batch.draw(grassL, drawX - o, drawY, thickness, tileSize);
        }

        if (right) {
            batch.draw(grassR, drawX + tileSize - o, drawY, thickness, tileSize);
        }

        // ===== OUTER CORNERS =====
        if (top && right) {
            batch.draw(grassTR, drawX + tileSize - o, drawY + tileSize - o, thickness, thickness);
        }

        if (top && left) {
            batch.draw(grassTL, drawX - o, drawY + tileSize - o, thickness, thickness);
        }

        if (bottom && right) {
            batch.draw(grassBR, drawX + tileSize - o, drawY - o, thickness, thickness);
        }

        if (bottom && left) {
            batch.draw(grassBL, drawX - o, drawY - o, thickness, thickness);
        }

        // ===== INNER CORNERS =====
        if (!top && !left && !hasBlock(x - 1, y + 1)) {
            batch.draw(grassInnerTL, drawX - o, drawY + tileSize - o, thickness, thickness);
        }

        if (!top && !right && !hasBlock(x + 1, y + 1)) {
            batch.draw(grassInnerTR, drawX + tileSize - o, drawY + tileSize - o, thickness, thickness);
        }

        if (!bottom && !left && !hasBlock(x - 1, y - 1)) {
            batch.draw(grassInnerBL, drawX - o, drawY - o, thickness, thickness);
        }

        if (!bottom && !right && !hasBlock(x + 1, y - 1)) {
            batch.draw(grassInnerBR, drawX + tileSize - o, drawY - o, thickness, thickness);
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
        for (Texture t : dirtTextures) t.dispose();
        for (Texture t : allTextures) t.dispose();
    }
}
