package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixFuture;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixDecay extends ClassicSpleefAffixFuture {

    public AffixDecay() {
        super();
        displayName = "Decay";
        this.activateTime = 90;
    }

    private static final FakeBlock INDICATOR = new FakeBlock(Material.RED_CONCRETE_POWDER.createBlockData());

    boolean decayTick = false;

    @Override
    protected void updateActive(ClassicSpleefBattle battle) {
        if (decayTick = !decayTick) return;
        Set<BlockPosition> blockPositionSet = battle.getGameWorld().getBaseBlocks().keySet();
        if (blockPositionSet.isEmpty()) return;
        BlockPosition randomPos = blockPositionSet.toArray(new BlockPosition[0])[new Random().nextInt(blockPositionSet.size())];
        FakeBlock fb = battle.getGameWorld().getFakeBlock(randomPos);
        if (fb != null && !fb.getBlockData().getMaterial().isAir()) {
            battle.getGameWorld().setBlock(randomPos, INDICATOR);
            battle.getGameWorld().setBlockDelayed(randomPos, FakeWorld.AIR, 20L);
        }
    }

    @Override
    protected String getPreActiveMessage(int seconds) {
        return "Decay will activate in " + seconds + " seconds";
    }

}
