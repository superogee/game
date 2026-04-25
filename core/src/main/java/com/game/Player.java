package com.game;

import com.badlogic.gdx.graphics.Color;

public class Player extends Entity {
    public int attackDamage = 4;
    public int level = 1;
    public int xp = 0;
    public int xpToNextLevel = 5;
    public int score = 0;

    public Player(int q, int r) {
        super(q, r, new Color(0.90f, 0.61f, 0.65f, 1.0f), 14);
    }

    public int gainXp(int amount) {
        xp += amount;
        int levelsGained = 0;

        while (xp >= xpToNextLevel) {
            xp -= xpToNextLevel;
            xpToNextLevel += 3;
            level++;
            attackDamage++;
            maxHp += 3;
            hp = maxHp;
            levelsGained++;
        }

        return levelsGained;
    }

    public float getXpRatio() {
        return xpToNextLevel <= 0 ? 0.0f : (float) xp / xpToNextLevel;
    }
}
