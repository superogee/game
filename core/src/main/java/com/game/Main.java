package com.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main extends ApplicationAdapter {
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
    private static final int MAX_FLOORS = 5;
    private static final int PLAYER_START_Q = 4;
    private static final int PLAYER_START_R = 4;

    private static final float WORLD_WIDTH = 320.0f;
    private static final float WORLD_HEIGHT = 180.0f;
    private static final float UI_WIDTH = 960.0f;
    private static final float UI_HEIGHT = 540.0f;
    private static final float HEX_RADIUS = 12.0f;
    private static final float HEX_Y_STRETCH = 0.7f;

    private static final float OUTER_MARGIN = 18.0f;
    private static final float PANEL_GAP = 14.0f;
    private static final float SIDEBAR_WIDTH = 198.0f;
    private static final float SIDEBAR_INSET = 14.0f;
    private static final float CARD_GAP = 12.0f;

    private final Color colorBackdrop = new Color(0.88f, 0.86f, 0.83f, 1.0f);
    private final Color colorGrassLight = new Color(0.73f, 0.80f, 0.73f, 1.0f);
    private final Color colorGrassDark = new Color(0.66f, 0.73f, 0.70f, 1.0f);
    private final Color colorWall = new Color(0.47f, 0.49f, 0.56f, 1.0f);
    private final Color colorFog = new Color(0.42f, 0.46f, 0.53f, 1.0f);
    private final Color colorGrassOutline = new Color(0.45f, 0.55f, 0.48f, 1.0f);
    private final Color colorWallOutline = new Color(0.27f, 0.29f, 0.35f, 1.0f);
    private final Color colorHighlight = new Color(0.90f, 0.95f, 0.87f, 1.0f);
    private final Color colorPanel = new Color(0.95f, 0.92f, 0.87f, 1.0f);
    private final Color colorPanelSoft = new Color(0.88f, 0.84f, 0.79f, 1.0f);
    private final Color colorPanelShadow = new Color(0.29f, 0.25f, 0.28f, 0.18f);
    private final Color colorFrame = new Color(0.27f, 0.29f, 0.33f, 1.0f);
    private final Color colorFrameAccent = new Color(0.63f, 0.56f, 0.46f, 1.0f);
    private final Color colorHp = new Color(0.78f, 0.42f, 0.47f, 1.0f);
    private final Color colorXp = new Color(0.83f, 0.65f, 0.34f, 1.0f);
    private final Color colorText = new Color(0.21f, 0.23f, 0.27f, 1.0f);
    private final Color colorSubtleText = new Color(0.36f, 0.38f, 0.43f, 1.0f);
    private final Color colorDim = new Color(0.17f, 0.18f, 0.22f, 0.56f);
    private final Color colorButton = new Color(0.82f, 0.78f, 0.71f, 1.0f);
    private final Color colorButtonHover = new Color(0.87f, 0.83f, 0.77f, 1.0f);
    private final Color colorSliderTrack = new Color(0.82f, 0.79f, 0.75f, 1.0f);
    private final Color colorSliderFill = new Color(0.73f, 0.61f, 0.45f, 1.0f);

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private BitmapFont headingFont;
    private FrameBuffer frameBuffer;
    private Viewport worldViewport;
    private Viewport uiViewport;
    private GameAssets assets;

    private Map gameMap;
    private Player player;
    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private Tile hoveredTile;
    private Tile exitTile;
    private GameState state = GameState.TITLE;

    private int floor = 1;
    private float worldTime;
    private String statusMessage = "";
    private float statusTimer;

    private float offsetX;
    private float offsetY;
    private float worldFrameX;
    private float worldFrameY;
    private float worldFrameWidth;
    private float worldFrameHeight;
    private float sidebarPanelX;
    private float sidebarPanelY;
    private float sidebarPanelWidth;
    private float sidebarPanelHeight;

    private final UiBox vitalsCard = new UiBox();
    private final UiBox runCard = new UiBox();
    private final UiBox objectiveCard = new UiBox();
    private final UiBox controlsCard = new UiBox();

    private final UiBox titleCard = new UiBox();
    private final UiBox pauseCard = new UiBox();
    private final UiBox endingCard = new UiBox();
    private final UiButton titlePlayButton = new UiButton();
    private final UiButton titleExitButton = new UiButton();
    private final UiSlider titleVolumeSlider = new UiSlider();
    private final UiButton pauseResumeButton = new UiButton();
    private final UiButton pauseExitButton = new UiButton();
    private final UiSlider pauseVolumeSlider = new UiSlider();
    private final UiButton endingPlayButton = new UiButton();
    private final UiButton endingExitButton = new UiButton();
    private UiSlider draggedSlider;
    private final Vector2 uiPointer = new Vector2();

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = loadFont("fonts/inter-regular.otf", 11);
        headingFont = loadFont("fonts/inter-semibold.otf", 16);

        assets = new GameAssets();

        worldViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        uiViewport = new FitViewport(UI_WIDTH, UI_HEIGHT);
        worldViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) WORLD_WIDTH, (int) WORLD_HEIGHT, false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        updateLayout(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        startNewRun();
        state = GameState.TITLE;
        setStatus("Set the volume, then step through the gate.");
    }

    private BitmapFont loadFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.hinting = FreeTypeFontGenerator.Hinting.Full;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.spaceX = 0;

        BitmapFont generatedFont = generator.generateFont(parameter);
        generator.dispose();
        return generatedFont;
    }

    private void startNewRun() {
        player = new Player(PLAYER_START_Q, PLAYER_START_R);
        floor = 1;
        worldTime = 0.0f;
        loadFloor();
        setStatus("Five floors stand between you and the gate.");
        state = GameState.PLAYING;
        hoveredTile = null;
    }

    private void loadFloor() {
        gameMap = new Map(MAP_WIDTH, MAP_HEIGHT, PLAYER_START_Q, PLAYER_START_R);
        player.moveTo(PLAYER_START_Q, PLAYER_START_R);
        enemies.clear();
        hoveredTile = null;
        exitTile = null;

        setupCamera();
        populateFloor();
    }

    private void populateFloor() {
        List<Tile> reachable = gameMap.getReachableTiles(player.q, player.r);
        exitTile = getFarthestReachableTile(reachable);
        if (exitTile != null) {
            exitTile.hasExit = true;
        }

        int enemyCount = Math.min(2 + floor, Math.max(2, reachable.size() / 5));
        for (int i = 0; i < enemyCount; i++) {
            Tile spawnTile = getRandomFreeTile(reachable, 3);
            if (spawnTile == null) {
                break;
            }
            enemies.add(new Enemy(spawnTile.q, spawnTile.r, floor));
        }

        int potionCount = 1 + floor / 3;
        for (int i = 0; i < potionCount; i++) {
            Tile potionTile = getRandomFreeTile(reachable, 2);
            if (potionTile == null) {
                break;
            }
            potionTile.hasPotion = true;
        }

        int treasureCount = 2 + floor / 2;
        for (int i = 0; i < treasureCount; i++) {
            Tile treasureTile = getRandomFreeTile(reachable, 2);
            if (treasureTile == null) {
                break;
            }
            treasureTile.hasTreasure = true;
        }
    }

    private Tile getFarthestReachableTile(List<Tile> tiles) {
        Tile farthest = null;
        int bestDistance = -1;

        for (Tile tile : tiles) {
            if (!tile.isWalkable || (tile.q == player.q && tile.r == player.r)) {
                continue;
            }

            int distance = gameMap.getDistance(player.q, player.r, tile.q, tile.r);
            if (distance > bestDistance) {
                bestDistance = distance;
                farthest = tile;
            }
        }

        return farthest;
    }

    private Tile getRandomFreeTile(List<Tile> tiles, int minDistanceFromPlayer) {
        ArrayList<Tile> candidates = new ArrayList<>();

        for (Tile tile : tiles) {
            if (!tile.isWalkable || (tile.q == player.q && tile.r == player.r)) {
                continue;
            }

            if (gameMap.getDistance(player.q, player.r, tile.q, tile.r) < minDistanceFromPlayer) {
                continue;
            }

            if (tile.hasExit || tile.hasPotion || tile.hasTreasure || getEnemyAt(tile.q, tile.r) != null) {
                continue;
            }

            candidates.add(tile);
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(MathUtils.random(candidates.size() - 1));
    }

    private void setupCamera() {
        float hexWidth = getHexWidth();
        float hexHeight = getHexHeight() * HEX_Y_STRETCH;
        offsetX = (WORLD_WIDTH - (gameMap.width * hexWidth + hexWidth / 2.0f)) / 2.0f;
        offsetY = (WORLD_HEIGHT - ((gameMap.height - 1) * (hexHeight * 0.75f) + hexHeight)) / 2.0f;
    }

    private void updateLayout(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);

        sidebarPanelWidth = SIDEBAR_WIDTH;
        sidebarPanelHeight = UI_HEIGHT - OUTER_MARGIN * 2.0f;
        sidebarPanelX = UI_WIDTH - OUTER_MARGIN - sidebarPanelWidth;
        sidebarPanelY = OUTER_MARGIN;

        float worldAreaX = OUTER_MARGIN;
        float worldAreaY = OUTER_MARGIN;
        float worldAreaWidth = sidebarPanelX - PANEL_GAP - worldAreaX;
        float worldAreaHeight = UI_HEIGHT - OUTER_MARGIN * 2.0f;
        float aspect = WORLD_WIDTH / WORLD_HEIGHT;

        worldFrameWidth = Math.min(worldAreaWidth, worldAreaHeight * aspect);
        worldFrameHeight = worldFrameWidth / aspect;
        worldFrameX = worldAreaX + (worldAreaWidth - worldFrameWidth) * 0.5f;
        worldFrameY = worldAreaY + (worldAreaHeight - worldFrameHeight) * 0.5f;

        layoutSidebarCards();
        layoutMenus();
        updateWorldViewportBounds();
        syncMenuVolume();
    }

    private void layoutSidebarCards() {
        float cardX = sidebarPanelX + SIDEBAR_INSET;
        float cardWidth = sidebarPanelWidth - SIDEBAR_INSET * 2.0f;
        float cursorY = sidebarPanelY + sidebarPanelHeight - 88.0f;

        vitalsCard.set(cardX, cursorY - 86.0f, cardWidth, 86.0f);
        cursorY = vitalsCard.y - CARD_GAP;

        runCard.set(cardX, cursorY - 108.0f, cardWidth, 108.0f);
        cursorY = runCard.y - CARD_GAP;

        objectiveCard.set(cardX, cursorY - 88.0f, cardWidth, 88.0f);

        float controlsY = sidebarPanelY + 20.0f;
        controlsCard.set(cardX, controlsY, cardWidth, objectiveCard.y - CARD_GAP - controlsY);
    }

    private void layoutMenus() {
        titleCard.set(274.0f, 126.0f, 376.0f, 244.0f);
        titlePlayButton.set(titleCard.x + 24.0f, titleCard.y + 32.0f, 136.0f, 34.0f, "Play");
        titleExitButton.set(titleCard.x + titleCard.width - 160.0f, titleCard.y + 32.0f, 136.0f, 34.0f, "Exit");
        titleVolumeSlider.set(titleCard.x + 24.0f, titleCard.y + 108.0f, titleCard.width - 102.0f, 8.0f, 0.72f);

        pauseCard.set(316.0f, 154.0f, 324.0f, 196.0f);
        pauseResumeButton.set(pauseCard.x + 24.0f, pauseCard.y + 34.0f, 128.0f, 32.0f, "Resume");
        pauseExitButton.set(pauseCard.x + pauseCard.width - 152.0f, pauseCard.y + 34.0f, 128.0f, 32.0f, "Exit");
        pauseVolumeSlider.set(pauseCard.x + 24.0f, pauseCard.y + 96.0f, pauseCard.width - 102.0f, 8.0f, 0.72f);

        endingCard.set(286.0f, 146.0f, 352.0f, 212.0f);
        endingPlayButton.set(endingCard.x + 24.0f, endingCard.y + 30.0f, 136.0f, 32.0f, "Play Again");
        endingExitButton.set(endingCard.x + endingCard.width - 160.0f, endingCard.y + 30.0f, 136.0f, 32.0f, "Exit");
    }

    private void updateWorldViewportBounds() {
        float scaleX = uiViewport.getScreenWidth() / UI_WIDTH;
        float scaleY = uiViewport.getScreenHeight() / UI_HEIGHT;
        int screenX = uiViewport.getScreenX() + Math.round(worldFrameX * scaleX);
        int screenY = uiViewport.getScreenY() + Math.round(worldFrameY * scaleY);
        int screenW = Math.round(worldFrameWidth * scaleX);
        int screenH = Math.round(worldFrameHeight * scaleY);
        worldViewport.setScreenBounds(screenX, screenY, screenW, screenH);
        worldViewport.getCamera().update();
    }

    private void syncMenuVolume() {
        if (assets == null) {
            return;
        }

        float volume = assets.getMasterVolume();
        titleVolumeSlider.value = volume;
        pauseVolumeSlider.value = volume;
    }

    private void setStatus(String message) {
        statusMessage = message;
        statusTimer = 4.0f;
    }

    private void syncUiPointer() {
        uiPointer.set(Gdx.input.getX(), Gdx.input.getY());
        uiViewport.unproject(uiPointer);
    }

    private void syncWorldHover() {
        if (state != GameState.PLAYING) {
            hoveredTile = null;
            return;
        }

        Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        worldViewport.unproject(mouse);
        hoveredTile = null;
        float minDistance = Float.MAX_VALUE;

        for (int r = 0; r < gameMap.height; r++) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                if (tile == null || !tile.isWalkable) {
                    continue;
                }

                float tileX = getTileCenterX(q, r);
                float tileY = getTileCenterY(q, r);
                if (isPointInsideHex(tileX, tileY, mouse.x, mouse.y)) {
                    hoveredTile = tile;
                    return;
                }

                float distance = Vector2.dst2(mouse.x, mouse.y, tileX, tileY);
                if (distance < minDistance) {
                    minDistance = distance;
                    hoveredTile = tile;
                }
            }
        }

        float snapRadius = HEX_RADIUS * 1.35f;
        if (hoveredTile != null && minDistance > snapRadius * snapRadius) {
            hoveredTile = null;
        }
    }

    private boolean isPointInsideHex(float centerX, float centerY, float pointX, float pointY) {
        float[] vertices = getHexPoints(centerX, centerY);
        return Intersector.isPointInPolygon(vertices, 0, vertices.length, pointX, pointY);
    }

    private void updateFrame(float delta) {
        worldTime += delta;
        if (statusTimer > 0.0f) {
            statusTimer = Math.max(0.0f, statusTimer - delta);
        }

        syncUiPointer();
        syncDraggedSlider();

        if (handleEscapeToggle()) {
            return;
        }

        switch (state) {
            case TITLE -> handleTitleInput();
            case PAUSED -> handlePauseInput();
            case WON, LOST -> handleEndingInput();
            case PLAYING -> {
                syncWorldHover();
                handlePlayingInput();
            }
            default -> {
            }
        }
    }

    private boolean handleEscapeToggle() {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            return false;
        }

        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            hoveredTile = null;
            draggedSlider = null;
            assets.playUiClick();
            return true;
        }

        if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            draggedSlider = null;
            assets.playUiClick();
            return true;
        }

        return false;
    }

    private void handleTitleInput() {
        if (beginSliderDrag(titleVolumeSlider)) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || buttonPressed(titlePlayButton)) {
            assets.playUiClick();
            startNewRun();
            return;
        }

        if (buttonPressed(titleExitButton)) {
            assets.playUiClick();
            Gdx.app.exit();
        }
    }

    private void handlePauseInput() {
        if (beginSliderDrag(pauseVolumeSlider)) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || buttonPressed(pauseResumeButton)) {
            assets.playUiClick();
            state = GameState.PLAYING;
            return;
        }

        if (buttonPressed(pauseExitButton)) {
            assets.playUiClick();
            Gdx.app.exit();
        }
    }

    private void handleEndingInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || buttonPressed(endingPlayButton)) {
            assets.playUiClick();
            startNewRun();
            return;
        }

        if (buttonPressed(endingExitButton)) {
            assets.playUiClick();
            Gdx.app.exit();
        }
    }

    private void handlePlayingInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            assets.playUiClick();
            startNewRun();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            setStatus("You wait and listen to the ruin.");
            endPlayerTurn();
            return;
        }

        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || hoveredTile == null) {
            return;
        }

        if (hoveredTile.q == player.q && hoveredTile.r == player.r) {
            setStatus("You hold your ground.");
            endPlayerTurn();
            return;
        }

        if (gameMap.getDistance(player.q, player.r, hoveredTile.q, hoveredTile.r) != 1) {
            setStatus("That tile is too far away.");
            return;
        }

        Enemy target = getEnemyAt(hoveredTile.q, hoveredTile.r);
        boolean floorAdvanced;

        if (target != null) {
            floorAdvanced = handleAttack(target);
        } else {
            player.moveTo(hoveredTile.q, hoveredTile.r);
            floorAdvanced = handleLanding(gameMap.getTile(player.q, player.r));
        }

        if (floorAdvanced || state != GameState.PLAYING) {
            return;
        }

        if (enemies.isEmpty()) {
            if (isPlayerOnExit()) {
                advanceFloor();
                return;
            }
            setStatus(floor == MAX_FLOORS ? "Final floor clear. The gate is open." : "Floor clear. The gate is now open.");
        }

        endPlayerTurn();
    }

    private boolean handleAttack(Enemy target) {
        assets.playAttack();
        target.takeDamage(player.attackDamage);
        if (target.hp > 0) {
            setStatus("Your strike lands.");
            return false;
        }

        enemies.remove(target);
        player.score += 12 + floor * 3;
        int levelsGained = player.gainXp(target.xpReward);
        assets.playPickup();

        if (levelsGained > 0) {
            setStatus("Level " + player.level + ". You feel stronger.");
        } else if (enemies.isEmpty()) {
            if (isPlayerOnExit()) {
                advanceFloor();
                return true;
            }
            assets.playGateOpen();
            setStatus(floor == MAX_FLOORS ? "The last foe falls. The final gate opens." : "The last foe falls. The gate opens.");
        } else {
            setStatus("Enemy defeated.");
        }

        return false;
    }

    private boolean handleLanding(Tile tile) {
        if (tile == null) {
            return false;
        }

        boolean pickedSomething = false;

        if (tile.hasTreasure) {
            tile.hasTreasure = false;
            player.score += 20 + floor * 5;
            setStatus("Relic found. Your score rises.");
            pickedSomething = true;
        }

        if (tile.hasPotion) {
            tile.hasPotion = false;
            int healed = player.heal(4 + floor / 2);
            if (healed > 0) {
                setStatus("You recover " + healed + " HP.");
            } else {
                setStatus("The potion fizzles. Health was already full.");
            }
            pickedSomething = true;
        }

        if (pickedSomething) {
            assets.playPickup();
        }

        if (tile.hasExit) {
            if (enemies.isEmpty()) {
                advanceFloor();
                return true;
            }
            setStatus("The gate stays sealed while foes remain.");
        }

        return false;
    }

    private void advanceFloor() {
        if (floor >= MAX_FLOORS) {
            completeRun();
            return;
        }

        floor++;
        int recovered = player.heal(2 + floor / 2);
        loadFloor();
        setStatus("Floor " + floor + ". Catch your breath: +" + recovered + " HP.");
    }

    private void completeRun() {
        player.score += 50;
        state = GameState.WON;
        statusTimer = 0.0f;
        hoveredTile = null;
        assets.playVictory();
    }

    private void endPlayerTurn() {
        runEnemyTurn();
        if (player.hp <= 0 && state == GameState.PLAYING) {
            state = GameState.LOST;
            hoveredTile = null;
            setStatus("The run is over. Press R or click to begin again.");
            assets.playDefeat();
        }
    }

    private void runEnemyTurn() {
        if (state != GameState.PLAYING || enemies.isEmpty()) {
            return;
        }

        HashSet<String> blockedCells = new HashSet<>();
        for (Enemy enemy : enemies) {
            blockedCells.add(cellKey(enemy.q, enemy.r));
        }

        int totalDamage = 0;
        for (Enemy enemy : new ArrayList<>(enemies)) {
            String currentKey = cellKey(enemy.q, enemy.r);
            blockedCells.remove(currentKey);

            if (enemy.canAttack(player, gameMap)) {
                totalDamage += player.takeDamage(enemy.contactDamage);
                blockedCells.add(currentKey);
                if (player.hp <= 0) {
                    assets.playHurt();
                    return;
                }
                continue;
            }

            Tile nextStep = enemy.findNextStep(player, gameMap, blockedCells);
            if (nextStep != null && !blockedCells.contains(cellKey(nextStep.q, nextStep.r))) {
                enemy.moveTo(nextStep.q, nextStep.r);
            }

            blockedCells.add(cellKey(enemy.q, enemy.r));
        }

        if (totalDamage > 0) {
            assets.playHurt();
            setStatus("Enemies deal " + totalDamage + " damage.");
        }
    }

    private Enemy getEnemyAt(int q, int r) {
        for (Enemy enemy : enemies) {
            if (enemy.q == q && enemy.r == r) {
                return enemy;
            }
        }
        return null;
    }

    private boolean isPlayerOnExit() {
        return exitTile != null && player.q == exitTile.q && player.r == exitTile.r;
    }

    private String cellKey(int q, int r) {
        return q + ":" + r;
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        updateFrame(delta);

        float playerX = getTileCenterX(player.q, player.r);
        float playerY = getTileCenterY(player.q, player.r);
        float viewRadius = HEX_RADIUS * 5.8f;

        frameBuffer.begin();
        ScreenUtils.clear(colorBackdrop);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glViewport(0, 0, (int) WORLD_WIDTH, (int) WORLD_HEIGHT);
        shapeRenderer.setProjectionMatrix(worldViewport.getCamera().combined);
        batch.setProjectionMatrix(worldViewport.getCamera().combined);

        updateVisibility(playerX, playerY, viewRadius);
        renderWorld(playerX, playerY, viewRadius, delta);
        frameBuffer.end();

        ScreenUtils.clear(colorBackdrop);
        uiViewport.apply();
        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        drawInterfaceChrome();

        batch.begin();
        batch.draw(
            frameBuffer.getColorBufferTexture(),
            worldFrameX,
            worldFrameY,
            worldFrameWidth,
            worldFrameHeight,
            0,
            0,
            (int) WORLD_WIDTH,
            (int) WORLD_HEIGHT,
            false,
            true
        );
        drawSidebar();
        batch.end();

        drawStateOverlay();
    }

    private void updateVisibility(float playerX, float playerY, float viewRadius) {
        for (int r = 0; r < gameMap.height; r++) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                float tileX = getTileCenterX(q, r);
                float tileY = getTileCenterY(q, r);
                float distance = Vector2.dst(playerX, playerY, tileX, tileY);

                tile.isVisible = distance <= viewRadius;
                if (tile.isVisible) {
                    tile.isExplored = true;
                }
            }
        }
    }

    private void renderWorld(float playerX, float playerY, float viewRadius, float delta) {
        drawTiles(playerX, playerY, viewRadius, delta);
        drawTileOutlines(playerX, playerY, viewRadius);
        drawWorldShadows();
        drawWorldSprites();
        drawEnemyHealthBars();
        drawWorldOverlay();
    }

    private void drawTiles(float playerX, float playerY, float viewRadius, float delta) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int r = gameMap.height - 1; r >= 0; r--) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                float tileX = getTileCenterX(q, r);
                float tileY = getTileCenterY(q, r);
                float distance = Vector2.dst(playerX, playerY, tileX, tileY);

                if (!tile.isExplored) {
                    continue;
                }

                boolean hoverable = state == GameState.PLAYING;
                float light = tile.isVisible ? Math.max(0.16f, 1.0f - distance / viewRadius) : 0.22f;
                tile.lift = MathUtils.lerp(
                    tile.lift,
                    tile.isWalkable && tile == hoveredTile && tile.isVisible && hoverable ? 4.0f : 0.0f,
                    delta * 10.0f
                );

                Color base = tile.isWalkable ? ((q + r) % 2 == 0 ? colorGrassLight : colorGrassDark) : colorWall;
                Color finalColor = new Color(base).lerp(colorFog, 1.0f - light);
                if (tile == hoveredTile && tile.isWalkable && tile.isVisible && hoverable) {
                    finalColor.lerp(colorHighlight, 0.45f * (tile.lift / 4.0f));
                }

                shapeRenderer.setColor(finalColor);
                drawHexFilled(tileX, tileY + tile.lift);
            }
        }
        shapeRenderer.end();
    }

    private void drawTileOutlines(float playerX, float playerY, float viewRadius) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int r = gameMap.height - 1; r >= 0; r--) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                if (!tile.isExplored) {
                    continue;
                }

                float tileX = getTileCenterX(q, r);
                float tileY = getTileCenterY(q, r);
                float distance = Vector2.dst(playerX, playerY, tileX, tileY);
                float light = tile.isVisible ? Math.max(0.18f, 1.0f - distance / viewRadius) : 0.2f;

                Color outlineBase = tile.isWalkable ? colorGrassOutline : colorWallOutline;
                Color finalOutline = new Color(outlineBase).lerp(colorFog, 1.0f - light);
                if (tile == hoveredTile && tile.isWalkable && tile.isVisible && state == GameState.PLAYING) {
                    finalOutline.lerp(colorHighlight, 0.75f * (tile.lift / 4.0f));
                }

                shapeRenderer.setColor(finalOutline);
                drawHexLine(tileX, tileY + tile.lift);
            }
        }
        shapeRenderer.end();
    }

    private void drawWorldShadows() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawUnitShadow(player.q, player.r);
        for (Enemy enemy : enemies) {
            Tile tile = gameMap.getTile(enemy.q, enemy.r);
            if (tile != null && tile.isVisible) {
                drawUnitShadow(enemy.q, enemy.r);
            }
        }
        shapeRenderer.end();
    }

    private void drawUnitShadow(int q, int r) {
        Tile tile = gameMap.getTile(q, r);
        float lift = tile != null ? tile.lift : 0.0f;
        float x = getTileCenterX(q, r);
        float y = getTileCenterY(q, r);
        shapeRenderer.setColor(0.16f, 0.18f, 0.22f, 0.18f);
        shapeRenderer.ellipse(x - 6.0f, y - 1.6f + lift, 12.0f, 4.0f);
    }

    private void drawWorldSprites() {
        batch.begin();
        batch.setColor(Color.WHITE);
        drawTileSpriteLayer();
        batch.setColor(Color.WHITE);
        drawUnitSprite(player, assets.playerSprite, 15.0f, 18.0f, 0.0f);
        for (Enemy enemy : enemies) {
            Tile tile = gameMap.getTile(enemy.q, enemy.r);
            if (tile != null && tile.isVisible) {
                drawUnitSprite(enemy, assets.slimeSprite, 16.0f, 14.0f, 0.0f);
            }
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawTileSpriteLayer() {
        for (int r = gameMap.height - 1; r >= 0; r--) {
            for (int q = 0; q < gameMap.width; q++) {
                Tile tile = gameMap.getTile(q, r);
                if (!tile.isExplored) {
                    continue;
                }

                float x = getTileCenterX(q, r);
                float y = getTileCenterY(q, r) + tile.lift;
                float alpha = tile.isVisible ? 1.0f : 0.35f;
                batch.setColor(1.0f, 1.0f, 1.0f, alpha);

                if (tile.hasPotion) {
                    drawTextureCentered(assets.potionSprite, x, y + 4.0f, 9.0f, 9.0f);
                }

                if (tile.hasTreasure) {
                    drawTextureCentered(assets.relicSprite, x, y + 4.0f, 10.0f, 10.0f);
                }

                if (tile.hasExit) {
                    Texture gateTexture = enemies.isEmpty() ? assets.gateOpenSprite : assets.gateClosedSprite;
                    drawTextureCentered(gateTexture, x, y + 5.0f, 18.0f, 22.0f);
                }
            }
        }

        batch.setColor(Color.WHITE);
    }

    private void drawUnitSprite(Entity entity, Texture texture, float width, float height, float yOffset) {
        Tile tile = gameMap.getTile(entity.q, entity.r);
        float lift = tile != null ? tile.lift : 0.0f;
        float x = getTileCenterX(entity.q, entity.r);
        float y = getTileCenterY(entity.q, entity.r) + lift + yOffset;
        drawTextureCentered(texture, x, y + height * 0.5f - 1.0f, width, height);
    }

    private void drawTextureCentered(Texture texture, float centerX, float centerY, float width, float height) {
        batch.draw(texture, centerX - width * 0.5f, centerY - height * 0.5f, width, height);
    }

    private void drawEnemyHealthBars() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Enemy enemy : enemies) {
            Tile tile = gameMap.getTile(enemy.q, enemy.r);
            if (tile == null || !tile.isVisible) {
                continue;
            }

            float x = getTileCenterX(enemy.q, enemy.r);
            float y = getTileCenterY(enemy.q, enemy.r) + tile.lift + 11.0f;
            float hpBar = enemy.getHpRatio();
            shapeRenderer.setColor(0.18f, 0.20f, 0.24f, 0.84f);
            shapeRenderer.rect(x - 6.0f, y, 12.0f, 1.8f);
            shapeRenderer.setColor(hpBar > 0.4f ? 0.54f : 0.86f, hpBar > 0.4f ? 0.78f : 0.43f, hpBar > 0.4f ? 0.59f : 0.49f, 1.0f);
            shapeRenderer.rect(x - 6.0f, y, 12.0f * hpBar, 1.8f);
        }
        shapeRenderer.end();
    }

    private void drawWorldOverlay() {
        if (!isOverlayState()) {
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (state == GameState.WON) {
            shapeRenderer.setColor(0.20f, 0.31f, 0.30f, 0.58f);
        } else if (state == GameState.LOST) {
            shapeRenderer.setColor(0.33f, 0.19f, 0.24f, 0.62f);
        } else {
            shapeRenderer.setColor(0.18f, 0.20f, 0.24f, 0.42f);
        }
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.end();
    }

    private void drawInterfaceChrome() {
        float worldOuterRight = worldFrameX + worldFrameWidth + 8.0f;
        float gutterWidth = Math.max(0.0f, sidebarPanelX - worldOuterRight);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(colorPanelShadow);
        shapeRenderer.rect(worldFrameX + 6.0f, worldFrameY - 6.0f, worldFrameWidth + 14.0f, worldFrameHeight + 14.0f);

        shapeRenderer.setColor(colorFrame);
        shapeRenderer.rect(worldFrameX - 8.0f, worldFrameY - 8.0f, worldFrameWidth + 16.0f, worldFrameHeight + 16.0f);

        if (gutterWidth > 0.0f) {
            shapeRenderer.setColor(colorBackdrop);
            shapeRenderer.rect(worldOuterRight, sidebarPanelY, gutterWidth, sidebarPanelHeight);
        }

        shapeRenderer.setColor(colorPanel);
        shapeRenderer.rect(sidebarPanelX, sidebarPanelY, sidebarPanelWidth, sidebarPanelHeight);

        shapeRenderer.setColor(colorPanelSoft);
        fillPanel(vitalsCard);
        fillPanel(runCard);
        fillPanel(objectiveCard);
        fillPanel(controlsCard);

        shapeRenderer.setColor(colorFrameAccent);
        shapeRenderer.rect(sidebarPanelX + 16.0f, sidebarPanelY + sidebarPanelHeight - 68.0f, sidebarPanelWidth - 32.0f, 3.0f);

        drawVitalsBars();
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(colorFrameAccent);
        shapeRenderer.rect(worldFrameX - 8.0f, worldFrameY - 8.0f, worldFrameWidth + 16.0f, worldFrameHeight + 16.0f);
        shapeRenderer.rect(sidebarPanelX, sidebarPanelY, sidebarPanelWidth, sidebarPanelHeight);

        shapeRenderer.setColor(colorFrame);
        outlinePanel(vitalsCard);
        outlinePanel(runCard);
        outlinePanel(objectiveCard);
        outlinePanel(controlsCard);
        shapeRenderer.end();
    }

    private void drawVitalsBars() {
        float padding = 12.0f;
        float barX = vitalsCard.x + padding;
        float barWidth = vitalsCard.width - padding * 2.0f;
        drawBar(barX, vitalsCard.y + 38.0f, barWidth, 8.0f, player.getHpRatio(), colorHp);
        drawBar(barX, vitalsCard.y + 14.0f, barWidth, 6.0f, player.getXpRatio(), colorXp);
    }

    private void drawBar(float x, float y, float width, float height, float ratio, Color fillColor) {
        shapeRenderer.setColor(colorPanel);
        shapeRenderer.rect(x, y, width, height);

        shapeRenderer.setColor(fillColor);
        shapeRenderer.rect(x, y, width * MathUtils.clamp(ratio, 0.0f, 1.0f), height);
    }

    private void fillPanel(UiBox panel) {
        shapeRenderer.rect(panel.x, panel.y, panel.width, panel.height);
    }

    private void outlinePanel(UiBox panel) {
        shapeRenderer.rect(panel.x, panel.y, panel.width, panel.height);
    }

    private void drawSidebar() {
        drawSidebarHeader();
        drawVitalsPanel();
        drawRunPanel();
        drawObjectivePanel();
        drawControlsPanel();
    }

    private void drawSidebarHeader() {
        headingFont.setColor(colorText);
        headingFont.draw(batch, "SILENT GATE", sidebarPanelX + 16.0f, sidebarPanelY + sidebarPanelHeight - 18.0f);

        font.setColor(colorSubtleText);
        font.draw(
            batch,
            "Quiet hexcrawl in a sealed ruin.",
            sidebarPanelX + 16.0f,
            sidebarPanelY + sidebarPanelHeight - 44.0f,
            sidebarPanelWidth - 32.0f,
            Align.left,
            true
        );
    }

    private void drawVitalsPanel() {
        drawCardTitle("VITALS", vitalsCard);
        drawLabeledValue(vitalsCard, vitalsCard.y + 54.0f, "HP", player.hp + "/" + player.maxHp);
        drawLabeledValue(vitalsCard, vitalsCard.y + 30.0f, "XP", player.xp + "/" + player.xpToNextLevel);
    }

    private void drawRunPanel() {
        drawCardTitle("RUN", runCard);

        float rowY = runCard.y + runCard.height - 32.0f;
        float rowGap = 12.0f;
        drawLabeledValue(runCard, rowY, "Floor", floor + "/" + MAX_FLOORS);
        drawLabeledValue(runCard, rowY - rowGap, "Level", String.valueOf(player.level));
        drawLabeledValue(runCard, rowY - rowGap * 2.0f, "Attack", String.valueOf(player.attackDamage));
        drawLabeledValue(runCard, rowY - rowGap * 3.0f, "Foes", String.valueOf(enemies.size()));
        drawLabeledValue(runCard, rowY - rowGap * 4.0f, "Score", String.valueOf(player.score));
        drawLabeledValue(runCard, rowY - rowGap * 5.0f, "Gate", enemies.isEmpty() ? "Open" : "Sealed");
    }

    private void drawObjectivePanel() {
        drawCardTitle("OBJECTIVE", objectiveCard);
        float textX = objectiveCard.x + 12.0f;
        float textWidth = objectiveCard.width - 24.0f;

        font.setColor(colorText);
        font.draw(
            batch,
            getObjectiveText(),
            textX,
            objectiveCard.y + objectiveCard.height - 30.0f,
            textWidth,
            Align.left,
            true
        );

        font.setColor(colorSubtleText);
        font.draw(
            batch,
            getSidebarHint(),
            textX,
            objectiveCard.y + 34.0f,
            textWidth,
            Align.left,
            true
        );
    }

    private void drawControlsPanel() {
        drawCardTitle("CONTROLS", controlsCard);

        font.setColor(colorText);
        float lineX = controlsCard.x + 12.0f;
        float lineY = controlsCard.y + controlsCard.height - 32.0f;
        float lineGap = 11.0f;
        font.draw(batch, "LMB  move / attack", lineX, lineY);
        font.draw(batch, "SPACE  wait", lineX, lineY - lineGap);
        font.draw(batch, "ESC  pause", lineX, lineY - lineGap * 2.0f);
        font.draw(batch, "R  restart", lineX, lineY - lineGap * 3.0f);
    }

    private void drawCardTitle(String title, UiBox panel) {
        font.setColor(colorText);
        font.draw(batch, title, panel.x + 12.0f, panel.y + panel.height - 16.0f);
    }

    private void drawLabeledValue(UiBox panel, float baselineY, String label, String value) {
        float padding = 12.0f;
        float textWidth = panel.width - padding * 2.0f;

        font.setColor(colorText);
        font.draw(batch, label, panel.x + padding, baselineY);
        font.draw(batch, value, panel.x + padding, baselineY, textWidth, Align.right, false);
    }

    private String getObjectiveText() {
        return switch (state) {
            case TITLE -> "Choose a volume, then press Play to begin a five-floor run.";
            case WON -> "You broke through the ruin and crossed the Silent Gate.";
            case LOST -> "The expedition ended before the final gate opened.";
            default -> {
                if (enemies.isEmpty()) {
                    yield floor == MAX_FLOORS
                        ? "The final gate is open. Step through and leave the ruin."
                        : "The gate is open. Step through to descend.";
                }
                yield floor == MAX_FLOORS
                    ? "Final floor. Clear foes, gather relics, and escape."
                    : "Clear foes, gather relics, and reach the gate.";
            }
        };
    }

    private String getSidebarHint() {
        return switch (state) {
            case TITLE -> "Five floors, denser ruins, and a longer push to the gate.";
            case PAUSED -> "Adjust volume here or press ESC to return.";
            case WON -> "Press Play Again or R to start another run.";
            case LOST -> "Press Play Again or R to try again.";
            default -> {
                if (statusTimer > 0.0f) {
                    yield statusMessage;
                }
                yield "Every wait still counts as a turn.";
            }
        };
    }

    private boolean isOverlayState() {
        return state == GameState.TITLE || state == GameState.PAUSED || state == GameState.WON || state == GameState.LOST;
    }

    private void drawStateOverlay() {
        if (!isOverlayState()) {
            return;
        }

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(colorDim);
        shapeRenderer.rect(0, 0, UI_WIDTH, UI_HEIGHT);
        shapeRenderer.end();

        switch (state) {
            case TITLE -> drawTitleOverlay();
            case PAUSED -> drawPauseOverlay();
            case WON, LOST -> drawEndingOverlay();
            default -> {
            }
        }
    }

    private void drawTitleOverlay() {
        drawModalChrome(titleCard, new Color(0.73f, 0.61f, 0.45f, 1.0f));
        drawModalButtons(titlePlayButton, titleExitButton);
        drawModalSlider(titleVolumeSlider);

        float textX = titleCard.x + 24.0f;
        float contentWidth = titleCard.width - 48.0f;
        float sliderLabelY = titleVolumeSlider.y + 24.0f;
        float sliderValueX = titleVolumeSlider.x + titleVolumeSlider.width + 12.0f;

        batch.begin();
        headingFont.setColor(colorText);
        headingFont.draw(batch, "SILENT GATE", textX, titleCard.y + titleCard.height - 18.0f);
        font.setColor(colorSubtleText);
        font.draw(batch, "A compact five-floor run through a sealed ruin.", textX, titleCard.y + titleCard.height - 42.0f, titleCard.width - 132.0f, Align.left, true);
        font.draw(batch, "Volume", textX, sliderLabelY);
        font.draw(batch, Math.round(assets.getMasterVolume() * 100.0f) + "%", sliderValueX, sliderLabelY, 42.0f, Align.right, false);
        font.draw(batch, "Press Enter or click Play to start.", textX, titleVolumeSlider.y - 18.0f, contentWidth, Align.left, true);
        drawButtonLabel(titlePlayButton);
        drawButtonLabel(titleExitButton);
        batch.draw(assets.gateOpenSprite, titleCard.x + titleCard.width - 82.0f, titleCard.y + titleCard.height - 84.0f, 42.0f, 50.0f);
        batch.end();
    }

    private void drawPauseOverlay() {
        drawModalChrome(pauseCard, new Color(0.63f, 0.56f, 0.46f, 1.0f));
        drawModalButtons(pauseResumeButton, pauseExitButton);
        drawModalSlider(pauseVolumeSlider);

        float sliderLabelY = pauseVolumeSlider.y + 24.0f;
        float sliderValueX = pauseVolumeSlider.x + pauseVolumeSlider.width + 12.0f;

        batch.begin();
        headingFont.setColor(colorText);
        headingFont.draw(batch, "PAUSED", pauseCard.x + 24.0f, pauseCard.y + pauseCard.height - 18.0f);
        font.setColor(colorSubtleText);
        font.draw(batch, "The ruin waits. Adjust the sound or return to the run.", pauseCard.x + 24.0f, pauseCard.y + pauseCard.height - 42.0f, pauseCard.width - 48.0f, Align.left, true);
        font.draw(batch, "Volume", pauseCard.x + 24.0f, sliderLabelY);
        font.draw(batch, Math.round(assets.getMasterVolume() * 100.0f) + "%", sliderValueX, sliderLabelY, 42.0f, Align.right, false);
        font.draw(batch, "Press ESC to resume instantly.", pauseCard.x + 24.0f, pauseVolumeSlider.y - 18.0f, pauseCard.width - 48.0f, Align.left, true);
        drawButtonLabel(pauseResumeButton);
        drawButtonLabel(pauseExitButton);
        batch.end();
    }

    private void drawEndingOverlay() {
        Color accent = state == GameState.WON
            ? new Color(0.43f, 0.68f, 0.64f, 1.0f)
            : new Color(0.74f, 0.42f, 0.48f, 1.0f);

        drawModalChrome(endingCard, accent);
        drawModalButtons(endingPlayButton, endingExitButton);

        batch.begin();
        headingFont.setColor(colorText);
        headingFont.draw(batch, state == GameState.WON ? "RUN COMPLETE" : "RUN ENDED", endingCard.x + 24.0f, endingCard.y + endingCard.height - 18.0f);
        font.setColor(colorSubtleText);
        String summary = state == GameState.WON
            ? "You crossed all " + MAX_FLOORS + " floors and walked beyond the Silent Gate."
            : "The ruin held after floor " + floor + ".";
        font.draw(batch, summary, endingCard.x + 24.0f, endingCard.y + endingCard.height - 42.0f, endingCard.width - 48.0f, Align.left, true);
        font.setColor(colorText);
        font.draw(batch, "Score " + player.score + "   Level " + player.level, endingCard.x + 24.0f, endingCard.y + 92.0f);
        drawButtonLabel(endingPlayButton);
        drawButtonLabel(endingExitButton);
        batch.end();
    }

    private void drawModalChrome(UiBox panel, Color accent) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(colorPanelShadow);
        shapeRenderer.rect(panel.x + 6.0f, panel.y - 6.0f, panel.width + 12.0f, panel.height + 12.0f);
        shapeRenderer.setColor(colorPanel);
        shapeRenderer.rect(panel.x, panel.y, panel.width, panel.height);
        shapeRenderer.setColor(accent);
        shapeRenderer.rect(panel.x + 24.0f, panel.y + panel.height - 66.0f, panel.width - 48.0f, 3.0f);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(colorFrame);
        shapeRenderer.rect(panel.x, panel.y, panel.width, panel.height);
        shapeRenderer.end();
    }

    private void drawModalButtons(UiButton leftButton, UiButton rightButton) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawButtonFill(leftButton);
        drawButtonFill(rightButton);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(colorFrame);
        outlinePanel(leftButton);
        outlinePanel(rightButton);
        shapeRenderer.end();
    }

    private void drawButtonFill(UiButton button) {
        shapeRenderer.setColor(button.contains(uiPointer.x, uiPointer.y) ? colorButtonHover : colorButton);
        shapeRenderer.rect(button.x, button.y, button.width, button.height);
    }

    private void drawModalSlider(UiSlider slider) {
        float handleWidth = 10.0f;
        float handleHeight = slider.height + 10.0f;
        float handleX = slider.getHandleCenterX() - handleWidth * 0.5f;
        float handleY = slider.y - (handleHeight - slider.height) * 0.5f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(colorSliderTrack);
        shapeRenderer.rect(slider.x, slider.y, slider.width, slider.height);
        shapeRenderer.setColor(colorSliderFill);
        shapeRenderer.rect(slider.x, slider.y, slider.width * slider.value, slider.height);
        shapeRenderer.setColor(colorFrame);
        shapeRenderer.rect(handleX, handleY, handleWidth, handleHeight);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(colorFrame);
        outlinePanel(slider);
        shapeRenderer.rect(handleX, handleY, handleWidth, handleHeight);
        shapeRenderer.end();
    }

    private void drawButtonLabel(UiButton button) {
        font.setColor(colorText);
        font.draw(batch, button.label, button.x, button.y + button.height * 0.5f + 6.0f, button.width, Align.center, false);
    }

    private boolean buttonPressed(UiButton button) {
        return Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && button.contains(uiPointer.x, uiPointer.y);
    }

    private boolean beginSliderDrag(UiSlider slider) {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || !slider.contains(uiPointer.x, uiPointer.y)) {
            return false;
        }

        draggedSlider = slider;
        draggedSlider.setValueFrom(uiPointer.x);
        assets.setMasterVolume(draggedSlider.value);
        syncMenuVolume();
        assets.playUiClick();
        return true;
    }

    private void syncDraggedSlider() {
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            draggedSlider = null;
            return;
        }

        if (draggedSlider == null) {
            return;
        }

        draggedSlider.setValueFrom(uiPointer.x);
        assets.setMasterVolume(draggedSlider.value);
        syncMenuVolume();
    }

    private void drawHexFilled(float x, float y) {
        float[] vertices = getHexPoints(x, y);
        for (int i = 0; i < 6; i++) {
            shapeRenderer.triangle(
                x,
                y,
                vertices[i * 2],
                vertices[i * 2 + 1],
                vertices[(i * 2 + 2) % 12],
                vertices[(i * 2 + 3) % 12]
            );
        }
    }

    private void drawHexLine(float x, float y) {
        float[] vertices = getHexPoints(x, y);
        for (int i = 0; i < 6; i++) {
            shapeRenderer.line(
                vertices[i * 2],
                vertices[i * 2 + 1],
                vertices[(i * 2 + 2) % 12],
                vertices[(i * 2 + 3) % 12]
            );
        }
    }

    private float[] getHexPoints(float x, float y) {
        float[] vertices = new float[12];
        for (int i = 0; i < 6; i++) {
            float angle = 60 * i + 30;
            vertices[i * 2] = x + HEX_RADIUS * MathUtils.cosDeg(angle);
            vertices[i * 2 + 1] = y + HEX_RADIUS * MathUtils.sinDeg(angle) * HEX_Y_STRETCH;
        }
        return vertices;
    }

    private float getHexWidth() {
        return (float) Math.sqrt(3) * HEX_RADIUS;
    }

    private float getHexHeight() {
        return 2.0f * HEX_RADIUS;
    }

    private float getTileCenterX(int q, int r) {
        float hexWidth = getHexWidth();
        return offsetX + q * hexWidth + (r % 2) * (hexWidth / 2.0f) + hexWidth / 2.0f;
    }

    private float getTileCenterY(int q, int r) {
        float hexHeight = getHexHeight();
        return offsetY + r * hexHeight * 0.75f * HEX_Y_STRETCH + hexHeight * HEX_Y_STRETCH / 2.0f;
    }

    @Override
    public void resize(int width, int height) {
        updateLayout(width, height);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        headingFont.dispose();
        frameBuffer.dispose();
        assets.dispose();
    }
}
