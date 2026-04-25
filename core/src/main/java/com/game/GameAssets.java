package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class GameAssets {
    public final Texture playerSprite;
    public final Texture slimeSprite;
    public final Texture potionSprite;
    public final Texture relicSprite;
    public final Texture gateClosedSprite;
    public final Texture gateOpenSprite;

    private final Sound uiClick;
    private final Sound attack;
    private final Sound hurt;
    private final Sound pickup;
    private final Sound gateOpen;
    private final Sound victory;
    private final Sound defeat;
    private final Music ambience;

    private float masterVolume = 0.72f;

    public GameAssets() {
        playerSprite = loadTexture("sprites/knight.png");
        slimeSprite = loadTexture("sprites/slime.png");
        potionSprite = loadTexture("sprites/potion.png");
        relicSprite = loadTexture("sprites/relic.png");
        gateClosedSprite = loadTexture("sprites/gate-closed.png");
        gateOpenSprite = loadTexture("sprites/gate-open.png");

        uiClick = Gdx.audio.newSound(Gdx.files.internal("audio/ui-click.wav"));
        attack = Gdx.audio.newSound(Gdx.files.internal("audio/attack.wav"));
        hurt = Gdx.audio.newSound(Gdx.files.internal("audio/hurt.wav"));
        pickup = Gdx.audio.newSound(Gdx.files.internal("audio/pickup.wav"));
        gateOpen = Gdx.audio.newSound(Gdx.files.internal("audio/gate-open.wav"));
        victory = Gdx.audio.newSound(Gdx.files.internal("audio/victory.wav"));
        defeat = Gdx.audio.newSound(Gdx.files.internal("audio/defeat.wav"));

        ambience = Gdx.audio.newMusic(Gdx.files.internal("audio/ticking-beneath-the-stone.mp3"));
        ambience.setLooping(true);
        ambience.play();
        applyVolume();
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float masterVolume) {
        this.masterVolume = MathUtils.clamp(masterVolume, 0.0f, 1.0f);
        applyVolume();
    }

    public void playUiClick() {
        play(uiClick, 0.55f);
    }

    public void playAttack() {
        play(attack, 0.70f);
    }

    public void playHurt() {
        play(hurt, 0.72f);
    }

    public void playPickup() {
        play(pickup, 0.68f);
    }

    public void playGateOpen() {
        play(gateOpen, 0.65f);
    }

    public void playVictory() {
        play(victory, 0.72f);
    }

    public void playDefeat() {
        play(defeat, 0.72f);
    }

    public void dispose() {
        playerSprite.dispose();
        slimeSprite.dispose();
        potionSprite.dispose();
        relicSprite.dispose();
        gateClosedSprite.dispose();
        gateOpenSprite.dispose();

        uiClick.dispose();
        attack.dispose();
        hurt.dispose();
        pickup.dispose();
        gateOpen.dispose();
        victory.dispose();
        defeat.dispose();

        ambience.dispose();
    }

    private Texture loadTexture(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return texture;
    }

    private void play(Sound sound, float gain) {
        if (masterVolume <= 0.001f) {
            return;
        }
        sound.play(masterVolume * gain);
    }

    private void applyVolume() {
        ambience.setVolume(masterVolume * 0.32f);
        if (!ambience.isPlaying()) {
            ambience.play();
        }
    }
}
