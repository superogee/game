package com.game;

import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Map {
    public final int width;
    public final int height;
    private final Tile[][] grid;

    public Map(int width, int height, int safeQ, int safeR) {
        this.width = width;
        this.height = height;
        this.grid = new Tile[width][height];

        int attempts = 0;
        do {
            generateLayout(safeQ, safeR);
            attempts++;
        } while (attempts < 40 && getReachableTiles(safeQ, safeR).size() < width * height * 0.48f);
    }

    private void generateLayout(int safeQ, int safeR) {
        for (int r = 0; r < height; r++) {
            for (int q = 0; q < width; q++) {
                boolean ground = MathUtils.random() > 0.30f;
                grid[q][r] = new Tile(q, r, ground);
            }
        }

        ensureWalkableArea(safeQ, safeR, 1);
    }

    public Tile getTile(int q, int r) {
        if (q < 0 || q >= width || r < 0 || r >= height) {
            return null;
        }
        return grid[q][r];
    }

    public List<Tile> getNearby(int q, int r) {
        List<Tile> result = new ArrayList<>();
        int[][] steps = r % 2 == 0
            ? new int[][]{{1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}}
            : new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 0}, {0, -1}, {1, -1}};

        for (int[] step : steps) {
            Tile tile = getTile(q + step[0], r + step[1]);
            if (tile != null) {
                result.add(tile);
            }
        }
        return result;
    }

    public ArrayList<Tile> getReachableTiles(int startQ, int startR) {
        ArrayList<Tile> reachable = new ArrayList<>();
        Tile start = getTile(startQ, startR);
        if (start == null || !start.isWalkable) {
            return reachable;
        }

        boolean[][] visited = new boolean[width][height];
        Queue<Tile> queue = new LinkedList<>();
        queue.add(start);
        visited[start.q][start.r] = true;

        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            reachable.add(current);

            for (Tile next : getNearby(current.q, current.r)) {
                if (!next.isWalkable || visited[next.q][next.r]) {
                    continue;
                }

                visited[next.q][next.r] = true;
                queue.add(next);
            }
        }

        return reachable;
    }

    public int getDistance(int q1, int r1, int q2, int r2) {
        int x1 = q1 - (r1 - (r1 & 1)) / 2;
        int z1 = r1;
        int y1 = -x1 - z1;

        int x2 = q2 - (r2 - (r2 & 1)) / 2;
        int z2 = r2;
        int y2 = -x2 - z2;

        return Math.max(Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)), Math.abs(z1 - z2));
    }

    private void ensureWalkableArea(int centerQ, int centerR, int radius) {
        for (int r = 0; r < height; r++) {
            for (int q = 0; q < width; q++) {
                if (getDistance(q, r, centerQ, centerR) <= radius) {
                    grid[q][r].isWalkable = true;
                }
            }
        }
    }
}
