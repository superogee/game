package com.game;

import java.util.ArrayList;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Main extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    FrameBuffer frameBuffer;
    OrthographicCamera camera;
    Viewport viewport;

    Entity player;
    Color playerColor = new Color(0.8f, 0.2f, 0.2f, 1f);
    Map gameMap;
    Tile hoveredTile = null;
    ArrayList<Enemy> enemies = new ArrayList<>();

    final int width = 320;
    final int height = 180;

    float hexSize = 12f;
    float perspectiveScale = 0.75f;

    Color lightGreen = new Color(0.75f, 0.85f, 0.75f, 1f);
    Color darkGreen = new Color(0.60f, 0.75f, 0.60f, 1f);
    Color colorOutline = new Color(0.15f, 0.25f, 0.15f, 1f);
    Color fogColor = new Color(0.6f, 0.65f, 0.75f, 1f);
    Color darknessColor = new Color(0.05f, 0.05f, 0.1f, 1f);
    Color wallColor = new Color(0.3f, 0.3f, 0.35f, 1f);
    Color wallOutline = new Color(0.1f, 0.1f, 0.15f, 1f);

    Texture fogTexture;
    float mapStartX, mapStartY;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        viewport = new FitViewport(width, height);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        gameMap = new Map(10, 10);
        calculateMapCenter();
        createFogTexture();

        player = new Entity(4, 4);

        player = new Entity(4, 4);
        enemies.add(new Enemy(2, 2, "a"));
        enemies.add(new Enemy(7, 3, "b"));
    }

    void calculateMapCenter() {
        float hexWidth = (float) (Math.sqrt(3) * hexSize);
        float hexHeight = 2 * hexSize * perspectiveScale;
        float rowHeight = (hexHeight * 0.75f);

        float totalMapWidth = gameMap.width * hexWidth + (hexWidth * 0.5f);
        float totalMapHeight = (gameMap.height - 1) * rowHeight + hexHeight;

        mapStartX = (width - totalMapWidth) / 2f;
        mapStartY = (height - totalMapHeight) / 2f;
    }

    void createFogTexture() {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        float centerX = width / 2f;
        float centerY = height / 2f;

        float startFogRadius = height * 0.35f;
        float fullFogRadius = height * 0.60f;
        float startDarknessRadius = height * 0.55f;
        float fullDarknessRadius = height * 0.85f;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float dist = Vector2.dst(x, y, centerX, centerY);

                float fogAlpha = 0;
                if (dist > startFogRadius) {
                    fogAlpha = (dist - startFogRadius) / (fullFogRadius - startFogRadius);
                    fogAlpha += MathUtils.random(-0.1f, 0.1f);
                }
                fogAlpha = MathUtils.clamp(fogAlpha, 0f, 1f);

                float darknessFactor = 0;
                if (dist > startDarknessRadius) {
                    darknessFactor = (dist - startDarknessRadius) / (fullDarknessRadius - startDarknessRadius);
                }
                darknessFactor = MathUtils.clamp(darknessFactor, 0f, 1f);

                Color pixelColor = new Color(fogColor).lerp(darknessColor, darknessFactor);

                pixmap.setColor(pixelColor.r, pixelColor.g, pixelColor.b, fogAlpha);
                pixmap.drawPixel(x, y);
            }
        }

        fogTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    void updateMouse() {
        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));

        hoveredTile = null;
        float minDst = Float.MAX_VALUE;

        float hexWidth = (float) (Math.sqrt(3) * hexSize);
        float hexHeight = 2 * hexSize;

        for (int r = 0; r < gameMap.height; r++) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                if (tile == null || !tile.isSteppable) continue;
                float x = q * hexWidth + (r % 2) * (hexWidth / 2f);
                float y = r * (hexHeight * 0.75f);
                float centerX = mapStartX + x + (hexWidth / 2f);
                float centerY = mapStartY + (y * perspectiveScale) + (hexHeight * perspectiveScale / 2f);
                float dx = worldCoords.x - centerX;
                float dy = (worldCoords.y - centerY) / perspectiveScale;
                float dst = dx * dx + dy * dy;

                if (dst < (hexSize * hexSize) && dst < minDst) {
                    minDst = dst;
                    hoveredTile = tile;
                }
            }
        }
    }

    void handleInput() {
        if (Gdx.input.justTouched() && hoveredTile != null && hoveredTile.isSteppable) {
            if (isNeighbor(player.q, player.r, hoveredTile.q, hoveredTile.r)) {

                boolean enemyPresent = false;
                for (Enemy e : enemies) {
                    if (e.q == hoveredTile.q && e.r == hoveredTile.r) {
                        enemyPresent = true;
                        break;
                    }
                }

                if (!enemyPresent) {
                    player.q = hoveredTile.q;
                    player.r = hoveredTile.r;

                    updateEnemies();
                }
            }
        }
    }

    boolean isNeighbor(int q1, int r1, int q2, int r2) {
        int[][] neighbors;
        if (r1 % 2 == 0) {
            neighbors = new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}};
        } else {
            neighbors = new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {0, 1}};
        }

        for (int[] offset : neighbors) {
            if (q1 + offset[0] == q2 && r1 + offset[1] == r2) return true;
        }
        return false;
    }

    @Override
    public void render() {
        updateMouse();
        handleInput();

        float delta = Gdx.graphics.getDeltaTime();
        float hoverSpeed = 40f;
        float maxHover = 4f;

        frameBuffer.begin();
        ScreenUtils.clear(darknessColor);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        float hexWidth = (float) (Math.sqrt(3) * hexSize);
        float hexHeight = 2 * hexSize;
        float pX = player.q * hexWidth + (player.r % 2) * (hexWidth / 2f);
        float pY = player.r * (hexHeight * 0.75f);
        float playerCenterX = mapStartX + pX + (hexWidth / 2f);
        float playerCenterY = mapStartY + (pY * perspectiveScale) + (hexHeight * perspectiveScale / 2f);

        float lightRadius = hexSize * 5.5f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int r = gameMap.height - 1; r >= 0; r--) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                if (tile == null) continue;

                float tX = q * hexWidth + (r % 2) * (hexWidth / 2f);
                float tY = r * (hexHeight * 0.75f);
                float tileCenterX = mapStartX + tX + (hexWidth / 2f);
                float tileCenterY = mapStartY + (tY * perspectiveScale) + (hexHeight * perspectiveScale / 2f);

                float dist = Vector2.dst(playerCenterX, playerCenterY, tileCenterX, tileCenterY);

                float lightIntensity = 0f;
                if (dist <= lightRadius) {
                    tile.isRevealed = true;
                    lightIntensity = 1f - (dist / lightRadius);
                }

                if (!tile.isRevealed) continue;

                float finalBrightness = Math.max(0.15f, lightIntensity);

                if (tile.isSteppable) {
                    if (tile == hoveredTile && dist <= lightRadius) {
                        tile.hoverOffset += hoverSpeed * delta;
                        if (tile.hoverOffset > maxHover) tile.hoverOffset = maxHover;
                    } else {
                        tile.hoverOffset -= hoverSpeed * delta;
                        if (tile.hoverOffset < 0) tile.hoverOffset = 0;
                    }
                } else {
                    tile.hoverOffset = 0;
                }

                Color baseColor = new Color();
                if (tile.isSteppable) {
                    baseColor.set(((q + r) % 2 == 0) ? lightGreen : darkGreen);
                } else {
                    baseColor.set(wallColor);
                }

                baseColor.lerp(darknessColor, 1f - finalBrightness);
                shapeRenderer.setColor(baseColor);

                drawHex(q, r, true, tile.hoverOffset);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(1);
        for (int r = gameMap.height - 1; r >= 0; r--) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                if (tile == null || !tile.isRevealed) continue;

                float tX = q * hexWidth + (r % 2) * (hexWidth / 2f);
                float tY = r * (hexHeight * 0.75f);
                float tileCenterX = mapStartX + tX + (hexWidth / 2f);
                float tileCenterY = mapStartY + (tY * perspectiveScale) + (hexHeight * perspectiveScale / 2f);
                float dist = Vector2.dst(playerCenterX, playerCenterY, tileCenterX, tileCenterY);

                float finalBrightness = Math.max(0.15f, (dist <= lightRadius) ? 1f - (dist / lightRadius) : 0f);

                Color outline = new Color(tile.isSteppable ? colorOutline : wallOutline);
                outline.lerp(darknessColor, 1f - finalBrightness);
                shapeRenderer.setColor(outline);

                drawHex(q, r, false, tile.hoverOffset);
            }
        }

        if (hoveredTile != null && hoveredTile.isSteppable) {
            float htX = hoveredTile.q * hexWidth + (hoveredTile.r % 2) * (hexWidth / 2f);
            float htY = hoveredTile.r * (hexHeight * 0.75f);
            float htcX = mapStartX + htX + (hexWidth / 2f);
            float htcY = mapStartY + (htY * perspectiveScale) + (hexHeight * perspectiveScale / 2f);
            if (Vector2.dst(playerCenterX, playerCenterY, htcX, htcY) <= lightRadius) {
                shapeRenderer.setColor(Color.WHITE);
                Gdx.gl.glLineWidth(2);
                drawHex(hoveredTile.q, hoveredTile.r, false, hoveredTile.hoverOffset);
                Gdx.gl.glLineWidth(1);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(playerColor);
        Tile playerTile = gameMap.getTile(player.q, player.r);
        float playerYOffset = (playerTile != null) ? playerTile.hoverOffset : 0f;
        drawEntity(player);
        for (Enemy e : enemies) {
            Tile tile = gameMap.getTile(e.q, e.r);
            if (tile != null && tile.isRevealed) {
                drawEntity(e);
            }
        }
        shapeRenderer.end();

        frameBuffer.end();
        ScreenUtils.clear(0, 0, 0, 1);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, width, height, 0, 0, width, height, false, true);
        batch.end();
    }

    void drawHex(int col, int row, boolean fill, float yOffset) {
        float hexWidth = (float) (Math.sqrt(3) * hexSize);
        float hexHeight = 2 * hexSize;
        float x = col * hexWidth + (row % 2) * (hexWidth / 2f);
        float y = row * (hexHeight * 0.75f);
        float centerX = mapStartX + x + (hexWidth / 2f);
        float centerY = mapStartY + (y * perspectiveScale) + (hexHeight * perspectiveScale / 2f) + yOffset;
        float[] vx = new float[6];
        float[] vy = new float[6];

        for (int i = 0; i < 6; i++) {
            float angleDeg = 60 * i + 30;
            vx[i] = centerX + hexSize * MathUtils.cosDeg(angleDeg);
            vy[i] = centerY + hexSize * MathUtils.sinDeg(angleDeg) * perspectiveScale;
        }

        if (fill) {
            for (int i = 0; i < 6; i++) {
                shapeRenderer.triangle(centerX, centerY, vx[i], vy[i], vx[(i + 1) % 6], vy[(i + 1) % 6]);
            }
        } else {
            for (int i = 0; i < 6; i++) {
                shapeRenderer.line(vx[i], vy[i], vx[(i + 1) % 6], vy[(i + 1) % 6]);
            }
        }
    }

    void drawEntity(Entity entity) {
        Tile t = gameMap.getTile(entity.q, entity.r);
        float yOff = (t != null) ? t.hoverOffset : 0;

        float hexWidth = (float) (Math.sqrt(3) * hexSize);
        float hexHeight = 2 * hexSize;
        float centerX = mapStartX + entity.q * hexWidth + (entity.r % 2) * (hexWidth / 2f) + (hexWidth / 2f);
        float centerY = mapStartY + (entity.r * hexHeight * 0.75f * perspectiveScale) + (hexHeight * perspectiveScale / 2f) + yOff;

        shapeRenderer.setColor(entity.color);
        shapeRenderer.circle(centerX, centerY + 6, hexSize * 0.4f);
    }

    void updateEnemies() {
        for (Enemy e : enemies) {
            e.takeTurn(player, gameMap, enemies);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        frameBuffer.dispose();
        fogTexture.dispose();
    }
}
