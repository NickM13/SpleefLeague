package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixFuture;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffixes;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixDecay extends ClassicSpleefAffixFuture {

    private List<BlockPosition> baseBlocks = new ArrayList<>();

    public AffixDecay() {
        super(ClassicSpleefAffixes.AffixType.DECAY, 90);
    }

    private static final FakeBlock INDICATOR = new FakeBlock(Material.RED_CONCRETE_POWDER.createBlockData());

    @Override
    public void startRound() {
        super.startRound();

    }

    int decayTick = 0;

    @Override
    protected void updateActive(ClassicSpleefBattle battle) {
        if (++decayTick >= 3) {
            if (baseBlocks.isEmpty()) return;
            BlockPosition randomPos = baseBlocks.get(new Random().nextInt(baseBlocks.size()));
            FakeBlock fb = battle.getGameWorld().getFakeBlock(randomPos);
            if (fb != null && !fb.getBlockData().getMaterial().isAir()) {
                battle.getGameWorld().setBlock(randomPos, INDICATOR);
                battle.getGameWorld().setBlockDelayed(randomPos, FakeWorld.AIR, 20L);
            }
            decayTick = 0;
        }
    }

}
