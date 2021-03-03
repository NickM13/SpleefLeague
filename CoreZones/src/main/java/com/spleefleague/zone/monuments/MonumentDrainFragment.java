package com.spleefleague.zone.monuments;

import com.spleefleague.core.world.FakeUtils;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 2/15/2021
 */
public class MonumentDrainFragment {

    final int entityId = FakeUtils.getNextId();

    private static final double SPEED = 30;

    final short changeX;
    final short changeY;
    final short changeZ;

    final Player player;

    int remainingTicks;

    public MonumentDrainFragment(Player player, Vector totalDist) {
        this.player = player;

        remainingTicks = (int) (totalDist.length() / SPEED * 10);

        changeX = (short) (totalDist.getX() * 32 * 128 / remainingTicks);
        changeY = (short) (totalDist.getY() * 32 * 128 / remainingTicks);
        changeZ = (short) (totalDist.getZ() * 32 * 128 / remainingTicks);
    }

    public boolean hasNext() {
        return remainingTicks-- >= 0;
    }

}
