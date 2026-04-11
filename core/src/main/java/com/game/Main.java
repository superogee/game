package com.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import java.util.ArrayList;

public class Main extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    FrameBuffer frameBuffer;
    Viewport viewport;

    Map gameMap;
    Entity player;
    ArrayList<Enemy> enemies = new ArrayList<>();
    Tile hoveredTile;

    boolean isGameOver = false;

    float screenWidth = 320.0f;
    float screenHeight = 180.0f;
    float hexRadius = 12.0f;
    float yStretch = 0.7f;

    Color colorGrassLight = new Color(0.8f, 0.9f, 0.8f, 1.0f);
    Color colorGrassDark = new Color(0.7f, 0.85f, 0.7f, 1.0f);
    Color colorWall = new Color(0.7f, 0.7f, 0.75f, 1.0f);
    Color colorShadow = new Color(0.0f, 0.0f, 0.05f, 1.0f);
    Color colorGrassOutline = new Color(0.4f, 0.6f, 0.4f, 1.0f);
    Color colorWallOutline = new Color(0.3f, 0.3f, 0.35f, 1.0f);
    Color colorHighlight = new Color(0.6f, 0.9f, 0.9f, 1.0f);

    float offsetX, offsetY;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        viewport = new FitViewport(screenWidth, screenHeight);
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)screenWidth, (int)screenHeight, false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        gameMap = new Map(10, 10);
        player = new Entity(4, 4, new Color(0.9f, 0.4f, 0.4f, 1.0f), 12);
        enemies.add(new Enemy(2, 2));
        enemies.add(new Enemy(8, 5));

        setupCamera();
    }

    void setupCamera() {
        float w = (float)Math.sqrt(3) * hexRadius;
        float h = 2 * hexRadius * yStretch;
        offsetX = (screenWidth - (gameMap.width * w + w / 2.0f)) / 2.0f;
        offsetY = (screenHeight - ((gameMap.height - 1) * (h * 0.75f) + h)) / 2.0f;
    }

    void syncInput() {
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        hoveredTile = null;
        float minD = Float.MAX_VALUE;

        float w = (float)Math.sqrt(3) * hexRadius;
        float h = 2 * hexRadius;

        for (int r = 0; r < gameMap.height; r++) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile t = gameMap.getTile(q, r);
                if (t == null || !t.isWalkable) continue;

                float tx = offsetX + q * w + (r % 2) * (w / 2.0f) + w / 2.0f;
                float ty = offsetY + (r * h * 0.75f * yStretch) + (h * yStretch / 2.0f);
                float d = Vector2.dst2(mouse.x, mouse.y, tx, ty);

                if (d < (hexRadius * hexRadius) && d < minD) {
                    minD = d;
                    hoveredTile = t;
                }
            }
        }
    }

    void gameTick() {
        if (isGameOver) return;

        if (Gdx.input.justTouched() && hoveredTile != null) {
            if (gameMap.getDistance(player.q, player.r, hoveredTile.q, hoveredTile.r) == 1) {
                Enemy target = null;
                for (Enemy e : enemies) if (e.q == hoveredTile.q && e.r == hoveredTile.r) target = e;

                if (target != null) {
                    target.hp -= 4;
                    if (target.hp <= 0) enemies.remove(target);
                } else {
                    player.q = hoveredTile.q;
                    player.r = hoveredTile.r;
                }

                for (Enemy e : new ArrayList<>(enemies)) e.update(player, gameMap, enemies);
                if (player.hp <= 0) isGameOver = true;
            }
        }
    }

    @Override
    public void render() {
        syncInput();
        gameTick();

        float delta = Gdx.graphics.getDeltaTime();
        frameBuffer.begin();
        ScreenUtils.clear(colorShadow);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        float w = (float)Math.sqrt(3) * hexRadius;
        float h = 2 * hexRadius;
        float viewRadius = hexRadius * 5.5f;

        float px = offsetX + player.q * w + (player.r % 2) * (w / 2.0f) + w / 2.0f;
        float py = offsetY + (player.r * h * 0.75f * yStretch) + (h * yStretch / 2.0f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int r = gameMap.height - 1; r >= 0; r--) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile t = gameMap.getTile(q, r);
                float tx = offsetX + q * w + (r % 2) * (w / 2.0f) + w / 2.0f;
                float ty = offsetY + (r * h * 0.75f * yStretch) + (h * yStretch / 2.0f);
                float dist = Vector2.dst(px, py, tx, ty);

                if (dist < viewRadius) { t.isVisible = true; t.isExplored = true; }
                else t.isVisible = false;

                if (!t.isExplored) continue;

                float light = t.isVisible ? Math.max(0.1f, 1.0f - dist / viewRadius) : 0.1f;

                if (t.isWalkable && t == hoveredTile && t.isVisible && !isGameOver) {
                    t.lift = MathUtils.lerp(t.lift, 4.0f, delta * 10.0f);
                } else {
                    t.lift = MathUtils.lerp(t.lift, 0.0f, delta * 10.0f);
                }

                Color base = t.isWalkable ? ((q + r) % 2 == 0 ? colorGrassLight : colorGrassDark) : colorWall;
                Color tileFilledColor = new Color(base).lerp(colorShadow, 1.0f - light);

                if (t == hoveredTile && t.isWalkable && !isGameOver && t.isVisible) {
                    tileFilledColor.lerp(colorHighlight, 0.4f * (t.lift / 4.0f));
                }

                shapeRenderer.setColor(tileFilledColor);
                drawHexFilled(tx, ty + t.lift);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int r = gameMap.height - 1; r >= 0; r--) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile t = gameMap.getTile(q, r);
                if (!t.isExplored) continue;

                float tx = offsetX + q * w + (r % 2) * (w / 2.0f) + w / 2.0f;
                float ty = offsetY + (r * h * 0.75f * yStretch) + (h * yStretch / 2.0f);
                float dist = Vector2.dst(px, py, tx, ty);
                float light = t.isVisible ? Math.max(0.1f, 1.0f - dist / viewRadius) : 0.1f;

                Color outlineBase = t.isWalkable ? colorGrassOutline : colorWallOutline;
                Color finalOutlineColor = new Color(outlineBase).lerp(colorShadow, 1.0f - light);

                if (t == hoveredTile && t.isWalkable && !isGameOver && t.isVisible) {
                    finalOutlineColor.lerp(colorHighlight, 0.8f * (t.lift / 4.0f));
                }

                shapeRenderer.setColor(finalOutlineColor);
                drawHexLine(tx, ty + t.lift);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawUnit(player, px, py);
        for (Enemy e : enemies) {
            Tile t = gameMap.getTile(e.q, e.r);
            if (t.isVisible) {
                float ex = offsetX + e.q * w + (e.r % 2) * (w / 2.0f) + w / 2.0f;
                float ey = offsetY + (e.r * h * 0.75f * yStretch) + (h * yStretch / 2.0f);
                drawUnit(e, ex, ey);
            }
        }

        if (isGameOver) {
            shapeRenderer.setColor(0.5f, 0.0f, 0.0f, 0.4f);
            shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        }
        shapeRenderer.end();

        frameBuffer.end();
        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, screenWidth, screenHeight, 0, 0, (int)screenWidth, (int)screenHeight, false, true);
        batch.end();
    }

    void drawUnit(Entity e, float x, float y) {
        Tile t = gameMap.getTile(e.q, e.r);
        float h = (t != null) ? t.lift : 0;
        shapeRenderer.setColor(e.color);
        shapeRenderer.circle(x, y + 5.0f + h, hexRadius * 0.4f);

        float hpBar = (float)e.hp / e.maxHp;
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - 6.0f, y + 12.0f + h, 12.0f, 2.0f);
        shapeRenderer.setColor(hpBar > 0.4f ? Color.GREEN : Color.RED);
        shapeRenderer.rect(x - 6.0f, y + 12.0f + h, 12.0f * hpBar, 2.0f);
    }

    void drawHexFilled(float x, float y) {
        float[] v = getHexPoints(x, y);
        for (int i = 0; i < 6; i++) {
            shapeRenderer.triangle(x, y, v[i * 2], v[i * 2 + 1], v[(i * 2 + 2) % 12], v[(i * 2 + 3) % 12]);
        }
    }

    void drawHexLine(float x, float y) {
        float[] v = getHexPoints(x, y);
        for (int i = 0; i < 6; i++) {
            shapeRenderer.line(v[i * 2], v[i * 2 + 1], v[(i * 2 + 2) % 12], v[(i * 2 + 3) % 12]);
        }
    }

    float[] getHexPoints(float x, float y) {
        float[] v = new float[12];
        for (int i = 0; i < 6; i++) {
            float ang = 60 * i + 30;
            v[i * 2] = x + hexRadius * MathUtils.cosDeg(ang);
            v[i * 2 + 1] = y + hexRadius * MathUtils.sinDeg(ang) * yStretch;
        }
        return v;
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void dispose() { shapeRenderer.dispose(); batch.dispose(); frameBuffer.dispose(); }
}
