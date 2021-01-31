package com.spleefleague.core.world.global;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.game.projectile.ProjectileWorld;
import com.spleefleague.core.world.global.lock.GlobalLock;
import com.spleefleague.core.world.global.vehicle.GlobalVehicle;
import org.bukkit.World;

/**
 * Global Worlds are instances of Fake Worlds that all players are
 * added to on login and removed from on quit, which allows for things
 * such as spleef field showing out of matches
 *
 * @author NickM13
 * @since 4/21/2020
 */
public class GlobalWorld extends ProjectileWorld<GlobalWorldPlayer> {

    public static void init() {
        GlobalVehicle.init();
        GlobalLock.init();
    }
    
    public GlobalWorld(World world) {
        super(-1, world, GlobalWorldPlayer.class);
    }
    
    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        if (!fakeBlocks.containsKey(pos)) return false;
        updateBlock(pos);
        return true;
    }
    
    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition pos, BlockPosition blockRelative) {
        if (!fakeBlocks.containsKey(pos)) return false;
        updateBlock(pos);
        return true;
    }

}
