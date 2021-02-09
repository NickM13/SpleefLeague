package com.spleefleague.spleef.game.battle.classic.affix.affixes;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.affix.ClassicSpleefAffix;
import org.bukkit.Material;
import java.util.HashSet;

import java.util.Random;
import java.util.Set;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class AffixLateGame extends ClassicSpleefAffix {

    private double crumblePercent;

    public AffixLateGame() {
        super();
        displayName = "Late Game";
        crumblePercent = 0.72;
    }

    /**
     * Called at the start of a round
     *
     * @param battle
     */
    @Override
    public void startRound(ClassicSpleefBattle battle) {
        Random random = new Random();
        for (BlockPosition pos : battle.getGameWorld().getBaseBlocks().keySet()) {
            if (random.nextDouble() < crumblePercent) {
                battle.getGameWorld().setBlockDelayed(pos, Material.AIR.createBlockData(), 0.2, battle.getArena().getSpawns());
            }
        }
        for (Position spawn : battle.getArena().getSpawns()) {
            BlockPosition blockPos = new BlockPosition(
                    (int) Math.floor(spawn.getX()),
                    (int) Math.floor(spawn.getY()) - 1,
                    (int) Math.floor(spawn.getZ()));
            battle.getGameWorld().setBlockDelayed(blockPos, Material.SNOW_BLOCK.createBlockData(), 8);
        }
    }

}
