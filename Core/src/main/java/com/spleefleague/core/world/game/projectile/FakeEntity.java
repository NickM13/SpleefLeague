package com.spleefleague.core.world.game.projectile;

import com.spleefleague.core.player.CorePlayer;
import net.minecraft.server.v1_15_R1.Entity;

/**
 * @author NickM13
 */
public interface FakeEntity {

    CorePlayer getCpShooter();

    void reducedStats(FakeEntity fakeEntity);

    Entity getEntity();

    ProjectileStats getStats();

    int getRemainingLife();

    int getRemainingBounces();

    int getRemainingBreakAfter();
}
