package com.game;

import com.badlogic.gdx.graphics.Color;

public class Entity {
    public int q, r;
    public Color color;
    public int hp;
    public int maxHp;

    public Entity(int q, int r) {
        this(q, r, new Color(0.8f, 0.2f, 0.2f, 1f), 10);
    }

    public Entity(int q, int r, Color color, int maxHp) {
        this.q = q;
        this.r = r;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }
}
