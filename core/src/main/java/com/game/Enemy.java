package com.game;

import com.badlogic.gdx.graphics.Color;
import java.util.*;

public class Enemy extends Entity {
    public Enemy(int q, int r) {
        super(q, r, new Color(0.4f, 0.6f, 0.9f, 1.0f), 6);
    }

    public void update(Entity target, Map map, ArrayList<Enemy> others) {
        if (map.getDistance(q, r, target.q, target.r) <= 1) {
            target.hp -= 2;
            return;
        }

        Tile nextStep = search(target, map, others);
        if (nextStep != null) {
            q = nextStep.q;
            r = nextStep.r;
        }
    }

    private Tile search(Entity target, Map map, ArrayList<Enemy> list) {
        Queue<Tile> todo = new LinkedList<>();
        java.util.Map<Tile, Tile> paths = new HashMap<>();

        Tile start = map.getTile(q, r);
        Tile end = map.getTile(target.q, target.r);

        todo.add(start);
        paths.put(start, null);

        while (!todo.isEmpty()) {
            Tile curr = todo.poll();
            if (curr == end) break;

            for (Tile n : map.getNearby(curr.q, curr.r)) {
                if (!n.isWalkable || paths.containsKey(n)) continue;

                boolean busy = false;
                for (Enemy e : list) if (e != this && e.q == n.q && e.r == n.r) { busy = true; break; }
                if (busy) continue;

                paths.put(n, curr);
                todo.add(n);
            }
        }

        if (!paths.containsKey(end)) return null;
        Tile point = end;
        while (paths.get(point) != start) point = paths.get(point);
        return point;
    }
}
