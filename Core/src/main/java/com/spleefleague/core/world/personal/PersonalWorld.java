package com.spleefleague.core.world.personal;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.FakeWorldPlayer;
import org.bukkit.World;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class PersonalWorld extends FakeWorld<PersonalWorldPlayer> {

    protected PersonalWorld(int priority, World world, Class<PersonalWorldPlayer> fakePlayerClass) {
        super(priority, world, fakePlayerClass);
    }

    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        return false;
    }

    /**
     * On player item use.
     *
     * @param cp            Core Player
     * @param blockPosition Click Block
     * @param blockRelative Placed Block
     * @return Cancel Event
     */
    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative) {
        return false;
    }

}
