package com.game;

import com.badlogic.gdx.graphics.Color;
import java.util.*;

public class Enemy extends Entity {
    public Enemy(int q, int r, String name) {
        super(q, r, new Color(0.3f, 0.7f, 1f, 1f), 3);
    }

    public void takeTurn(Entity player, Map map, ArrayList<Enemy> allEnemies) {
        if (isNear(player)) {
            player.hp -= 1;
            System.out.println("Монстр укусил вас! Ваше HP: " + player.hp);
            return;
        }

        Tile nextStep = findPath(player, map, allEnemies);
        if (nextStep != null) {
            this.q = nextStep.q;
            this.r = nextStep.r;
        }
    }

    private boolean isNear(Entity target) {
        int dq = Math.abs(this.q - target.q);
        int dr = Math.abs(this.r - target.r);
        return (dq <= 1 && dr <= 1);
    }

    private Tile findPath(Entity target, Map map, ArrayList<Enemy> allEnemies) {
        Queue<Tile> queue = new LinkedList<>();
        java.util.Map<Tile, Tile> cameFrom = new HashMap<>();

        Tile start = map.getTile(this.q, this.r);
        Tile goal = map.getTile(target.q, target.q);
        goal = map.getTile(target.q, target.r);

        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {
            Tile current = queue.poll();
            if (current == goal){
                break;
            }

            for (Tile next : map.getNeighbors(current.q, current.r)) {
                if (!next.isSteppable || cameFrom.containsKey(next)){
                    continue;
                }

                boolean occupied = false;
                for (Enemy e : allEnemies) {
                    if (e != this && e.q == next.q && e.r == next.r) {
                        occupied = true;
                        break;
                    }
                }
                if (occupied){
                    continue;
                }

                cameFrom.put(next, current);
                queue.add(next);
            }
        }

        Tile current = goal;
        if (!cameFrom.containsKey(goal)){
            return null;
        }

        while (cameFrom.get(current) != start) {
            current = cameFrom.get(current);
        }
        return current;
    }
}
