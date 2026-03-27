package com.game;

import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

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
                boolean isSteppable = MathUtils.random() > 0.2f;
                if (q == 4 && r == 4){
                    isSteppable = true;
                }
                tiles[q][r] = new Tile(q, r, isSteppable);
            }
        }
    }

    public Tile getTile(int q, int r) {
        if (q < 0 || q >= width || r < 0 || r >= height){
            return null;
        }
        return tiles[q][r];
    }

    public List<Tile> getNeighbors(int q, int r) {
        List<Tile> neighbors = new ArrayList<>();
        int[][] offsets = (r % 2 == 0)
            ? new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}}
            : new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, 0}, {0, 1}};

        for (int[] offset : offsets) {
            Tile t = getTile(q + offset[0], r + offset[1]);
            if (t != null){
                neighbors.add(t);
            }
        }
        return neighbors;
    }
}
