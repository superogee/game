package com.game;

import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

public class Map {
    public int width, height;
    private Tile[][] grid;

    public Map(int w, int h) {
        this.width = w;
        this.height = h;
        this.grid = new Tile[w][h];
        for (int r = 0; r < h; r++) {
            for (int q = 0; q < w; q++) {
                boolean ground = MathUtils.random() > 0.2f;
                if (q == 4 && r == 4) ground = true;
                grid[q][r] = new Tile(q, r, ground);
            }
        }
    }

    public Tile getTile(int q, int r) {
        if (q < 0 || q >= width || r < 0 || r >= height) return null;
        return grid[q][r];
    }

    public List<Tile> getNearby(int q, int r) {
        List<Tile> res = new ArrayList<>();
        int[][] steps;
        if (r % 2 == 0) {
            steps = new int[][]{ {1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1} };
        } else {
            steps = new int[][]{ {1, 0}, {1, 1}, {0, 1}, {-1, 0}, {0, -1}, {1, -1} };
        }

        for (int[] s : steps) {
            Tile t = getTile(q + s[0], r + s[1]);
            if (t != null) res.add(t);
        }
        return res;
    }

    public int getDistance(int q1, int r1, int q2, int r2) {
        int x1 = q1 - (r1 + (r1 & 1)) / 2;
        int z1 = r1;
        int y1 = -x1 - z1;

        int x2 = q2 - (r2 + (r2 & 1)) / 2;
        int z2 = r2;
        int y2 = -x2 - z2;

        return Math.max(Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)), Math.abs(z1 - z2));
    }
}
