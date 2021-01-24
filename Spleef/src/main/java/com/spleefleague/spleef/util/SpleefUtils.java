package com.spleefleague.spleef.util;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.game.GameWorld;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/24/2020
 */
public class SpleefUtils {

    public static final Set<Material> breakableBlocks = new HashSet<>();

    static {
        breakableBlocks.add(Material.SNOW);
        breakableBlocks.add(Material.SNOW_BLOCK);
        for (Material mat : Material.values()) {
            if (mat.name().endsWith("CONCRETE_POWDER")) {
                breakableBlocks.add(mat);
            }
        }
    }

    /**
     * Initialize base battle settings such as GameWorld tools and Scoreboard values
     */
    public static void setupBaseSettings(Battle<?> battle) {
        battle.setGameMode(GameMode.ADVENTURE);
        for (Material mat : breakableBlocks) {
            battle.getGameWorld().addBreakableBlock(mat);
        }
        battle.getGameWorld().addBreakTool(Material.DIAMOND_SHOVEL);
    }

    public static void fillFieldFast(Battle<?> battle) {
        if (battle == null || battle.getGameWorld() == null) return;
        GameWorld gameWorld = battle.getGameWorld();
        gameWorld.clear();
        for (BuildStructure structure : battle.getArena().getStructures()) {
            gameWorld.overwriteBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(structure.getFakeBlocks(), (int) battle.getArena().getOrigin().getYaw()),
                            battle.getArena().getOrigin().toBlockPosition()));
        }
    }

    /**
     * Fill the field without any delay
     *
     * @param battle Battle
     */
    public static void fillFieldFast(Battle<?> battle, BuildStructure field) {
        if (battle == null || battle.getGameWorld() == null) return;
        GameWorld gameWorld = battle.getGameWorld();
        List<Map<BlockPosition, FakeBlock>> toMerge = new ArrayList<>();
        if (field != null) {
            toMerge.add(field.getFakeBlocks());
        }
        gameWorld.overwriteBlocks(
                FakeUtils.translateBlocks(
                        FakeUtils.rotateBlocks(FakeUtils.mergeBlocks(toMerge), (int) battle.getArena().getOrigin().getYaw()),
                        battle.getArena().getOrigin().toBlockPosition()));
    }

    /**
     * Fill the field without any delay
     *
     * @param battle Battle
     */
    public static void fillFieldFancy(Battle<?> battle, BuildStructure field, boolean overwrite) {
        if (battle == null || battle.getGameWorld() == null) return;
        GameWorld gameWorld = battle.getGameWorld();
        List<Map<BlockPosition, FakeBlock>> toMerge = new ArrayList<>();
        /*
        for (BuildStructure structure : battle.getArena().getStructures()) {
            if (structure != null) {
                toMerge.add(structure.getFakeBlocks());
            }
        }
        */
        if (field != null) {
            toMerge.add(field.getFakeBlocks());
        }
        if (overwrite) {
            gameWorld.overwriteBlocks(
                    FakeUtils.translateBlocks(
                            FakeUtils.rotateBlocks(FakeUtils.mergeBlocks(toMerge), (int) battle.getArena().getOrigin().getYaw()),
                            battle.getArena().getOrigin().toBlockPosition()));
        } else {
            Map<BlockPosition, FakeBlock> fakeBlocks = FakeUtils.translateBlocks(
                    FakeUtils.rotateBlocks(FakeUtils.mergeBlocks(toMerge), (int) battle.getArena().getOrigin().getYaw()),
                    battle.getArena().getOrigin().toBlockPosition());
            for (Map.Entry<BlockPosition, FakeBlock> entry : fakeBlocks.entrySet()) {
                gameWorld.setBlockDelayed(entry.getKey(), entry.getValue().getBlockData(), 5, battle.getArena().getSpawns());
            }
        }
    }
    
}
