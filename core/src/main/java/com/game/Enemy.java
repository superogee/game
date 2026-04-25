package com.game;

import com.badlogic.gdx.graphics.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Enemy extends Entity {
    public final int contactDamage;
    public final int xpReward;

    public Enemy(int q, int r, int floor) {
        super(q, r, getEnemyColor(floor), 5 + floor * 2);
        this.contactDamage = 1 + floor / 2;
        this.xpReward = 2 + floor;
    }

    public boolean canAttack(Player target, Map map) {
        return map.getDistance(q, r, target.q, target.r) == 1;
    }

    public Tile findNextStep(Player target, Map map, Set<String> blockedCells) {
        Tile start = map.getTile(q, r);
        Tile end = map.getTile(target.q, target.r);
        if (start == null || end == null) {
            return null;
        }

        Queue<Tile> todo = new LinkedList<>();
        java.util.Map<Tile, Tile> paths = new HashMap<>();
        todo.add(start);
        paths.put(start, null);

        while (!todo.isEmpty()) {
            Tile current = todo.poll();
            if (current == end) {
                break;
            }

            for (Tile next : map.getNearby(current.q, current.r)) {
                if (!next.isWalkable || paths.containsKey(next)) {
                    continue;
                }

                if (blockedCells.contains(cellKey(next.q, next.r))) {
                    continue;
                }

                paths.put(next, current);
                todo.add(next);
            }
        }

        if (!paths.containsKey(end)) {
            return null;
        }

        Tile point = end;
        while (paths.get(point) != null && paths.get(point) != start) {
            point = paths.get(point);
        }

        return point == end ? null : point;
    }

    private String cellKey(int q, int r) {
        return q + ":" + r;
    }

    private static Color getEnemyColor(int floor) {
        float tint = Math.min(0.12f, floor * 0.01f);
        return new Color(0.60f - tint, 0.74f - tint * 0.5f, 0.86f, 1.0f);
    }
}
