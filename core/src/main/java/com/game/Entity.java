package com.game;

import com.badlogic.gdx.graphics.Color;

public class Entity {
    public int q, r;
    public Color color;
    public int hp, maxHp;

    public Entity(int q, int r, Color c, int m) {
        this.q = q;
        this.r = r;
        this.color = c;
        this.maxHp = m;
        this.hp = m;
    }
}
