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
        crumblePercent = 0.72;
    }

    /**
     * Called at the start of a round
     *
     * @param battle
     */
    @Override
    public void startRound(ClassicSpleefBattle battle) {
        Set<BlockPosition> positions = new HashSet<>();
        BlockPosition origin = battle.getArena().getStructures().get(0).getOriginPos();
        positions.addAll(battle.getArena().getStructures().get(0).getFakeBlocks().keySet());
        Random random = new Random();
        for (BlockPosition pos : positions) {
            if (random.nextDouble() < crumblePercent) {
                battle.getGameWorld().setBlock(pos.add(origin), Material.AIR.createBlockData(), true);
            }
        }
        for (Position spawn : battle.getArena().getSpawns()) {
            BlockPosition blockPos = new BlockPosition(
                    (int) Math.floor(spawn.getX()),
                    (int) Math.floor(spawn.getY()) - 1,
                    (int) Math.floor(spawn.getZ()));
            battle.getGameWorld().setBlock(blockPos,
                    battle.getArena().getStructures().get(0).getFakeBlocks().getOrDefault(blockPos.subtract(battle.getArena().getStructures().get(0).getOriginPos()), new FakeBlock(Material.SNOW.createBlockData())).getBlockData(), true);
        }
        battle.getGameWorld().updateAll();
    }

}
