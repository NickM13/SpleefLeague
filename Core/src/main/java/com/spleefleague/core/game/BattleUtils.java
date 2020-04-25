package com.spleefleague.core.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.game.GameWorld;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Some functions to get the game some variety
 *
 * @author NickM
 * @since 4/15/2020
 */
public class BattleUtils {

    private static final String[] defeatedSynonyms = {"defeated", "clobbered", "smashed",
            "pulverized", "clanked", "whapped", "wam jam'd", "destroyed", "ended the career of",
            "wang jangled", "cracked", "stolen elo from", "cheated in their match against", "given a new one to",
            "bested"};

    /**
     * Returns a random "defeat" synonym
     * @return Defeat Synonym
     */
    public static String randomDefeatSynonym() {
        Random r = new Random();
        return defeatedSynonyms[r.nextInt(defeatedSynonyms.length)];
    }

    /**
     * Returns a list of blocks that would be in the dome area around a location
     *
     * @param loc Feet
     * @return List of Block Pos
     */
    private static List<BlockPosition> getDomeBlocks(Location loc) {
        BlockPosition origin = new BlockPosition(loc.toVector());
        List<BlockPosition> blockPositions = new ArrayList<>();
        blockPositions.add(origin.add(new BlockPosition(-1, 0,  0)));
        blockPositions.add(origin.add(new BlockPosition( 1, 0,  0)));
        blockPositions.add(origin.add(new BlockPosition( 0, 0, -1)));
        blockPositions.add(origin.add(new BlockPosition( 0, 0,  1)));
        blockPositions.add(origin.add(new BlockPosition(-1, 1,  0)));
        blockPositions.add(origin.add(new BlockPosition( 1, 1,  0)));
        blockPositions.add(origin.add(new BlockPosition( 0, 1, -1)));
        blockPositions.add(origin.add(new BlockPosition( 0, 1,  1)));
        blockPositions.add(origin.add(new BlockPosition( 0, 2,  0)));
        return blockPositions;
    }

    /**
     * Fill an area around a list of locations
     *
     * @param gameWorld Game World
     * @param material Material
     * @param locs Locations
     */
    public static void fillDome(GameWorld gameWorld, Material material, List<Location> locs) {
        for (Location loc : locs) {
            List<BlockPosition> blockPositions = getDomeBlocks(loc);
            for (BlockPosition bp : blockPositions) {
                gameWorld.setBlock(bp, material.createBlockData());
            }
        }
    }

    /**
     * Remove an area around a list of locations
     *
     * @param gameWorld Game World
     * @param locs Locations
     */
    public static void clearDome(GameWorld gameWorld, List<Location> locs) {
        for (Location loc : locs) {
            List<BlockPosition> blockPositions = getDomeBlocks(loc);
            for (BlockPosition bp : blockPositions) {
                gameWorld.breakBlock(bp, null);
            }
        }
    }

}
