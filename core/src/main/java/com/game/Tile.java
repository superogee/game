package com.game;

public class Tile {
    public int q, r;
    public boolean isWalkable;
    public boolean isVisible, isExplored;
    public float lift = 0.0f;

    public Tile(int q, int r, boolean w) {
        this.q = q;
        this.r = r;
        this.isWalkable = w;
    }
}
