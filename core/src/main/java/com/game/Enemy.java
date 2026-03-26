package com.game;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;

public class Enemy extends Entity {
    public Enemy(int q, int r, String name) {
        super(q, r, new Color(0.3f, 0.7f, 1f, 0.7f), 3);
    }

    public void takeTurn(Entity player, Map map, ArrayList<Enemy> allEnemies) {
        int Q = q;
        int R = r;

        if (q < player.q) Q++;
        else if (q > player.q) Q--;
        if (r < player.r) R++;
        else if (r > player.r) R--;

        Tile targetTile = map.getTile(Q, R);
        if (targetTile == null || !targetTile.isSteppable) return;

        if (Q == player.q && R == player.r) return;

        for (Enemy e : allEnemies) {
            if (e != this && e.q == Q && e.r == R) return;
        }

        this.q = Q;
        this.r = R;
    }
}
