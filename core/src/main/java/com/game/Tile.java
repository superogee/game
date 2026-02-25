package com.game;

public class Tile {
    public final int q;
    public final int r;
    public boolean isSteppable;
    public boolean isRevealed;
    public float hoverOffset = 0f;

    public Tile(int q, int r, boolean isSteppable) {
        this.q = q;
        this.r = r;
        this.isSteppable = isSteppable;
        this.isRevealed = false;
    }
}
