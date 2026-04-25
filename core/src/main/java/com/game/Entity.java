package com.game;

import com.badlogic.gdx.graphics.Color;

public class Entity {
    public int q;
    public int r;
    public final Color color;
    public int hp;
    public int maxHp;

    public Entity(int q, int r, Color color, int maxHp) {
        this.q = q;
        this.r = r;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public void moveTo(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int heal(int amount) {
        int previous = hp;
        hp = Math.min(maxHp, hp + amount);
        return hp - previous;
    }

    public int takeDamage(int amount) {
        int previous = hp;
        hp = Math.max(0, hp - amount);
        return previous - hp;
    }

    public float getHpRatio() {
        return maxHp <= 0 ? 0.0f : (float) hp / maxHp;
    }
}
