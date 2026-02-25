package com.game;

public class Map {
    public final int width;
    public final int height;
    private final Tile[][] tiles;
    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];
        mapGeneration();
    }

    private void mapGeneration() {
        for (int r = 0; r < height; r++) {
            for (int q = 0; q < width; q++) {
                boolean isSteppable = true;
                tiles[q][r] = new Tile(q, r, isSteppable);
            }
        }
    }

    public Tile getTile(int q, int r) {
        if (q < 0 || q >= width || r < 0 || r >= height) return null;
        return tiles[q][r];
    }
}
