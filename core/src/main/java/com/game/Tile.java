package com.game;

public class Tile {
    public int q;
    public int r;
    public boolean isWalkable;
    public boolean isVisible;
    public boolean isExplored;
    public boolean hasPotion;
    public boolean hasTreasure;
    public boolean hasExit;
    public float lift = 0.0f;

    public Tile(int q, int r, boolean isWalkable) {
        this.q = q;
        this.r = r;
        this.isWalkable = isWalkable;
    }
}
