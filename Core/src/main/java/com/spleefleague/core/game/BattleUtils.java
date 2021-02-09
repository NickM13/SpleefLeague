package com.spleefleague.core.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.team.TeamBattlePlayer;
import com.spleefleague.core.game.battle.team.TeamBattleTeam;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameWorld;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            "wang jangled", "cracked", "stolen elo from",
            "bested"};

    /**
     * Returns a random "defeat" synonym
     * @return Defeat Synonym
     */
    public static String randomDefeatSynonym() {
        Random r = new Random();
        return defeatedSynonyms[r.nextInt(defeatedSynonyms.length)];
    }

    private static char[] POINT_ANIM = {/*'─', */'═', '╪', '▓', '█'};

    public static String toScoreSquares(BattlePlayer bp, int playToPoints) {
        if (playToPoints > 5) {
            return (Chat.SCORE + bp.getRoundWins() + "/" + playToPoints);
        } else {
            StringBuilder stringBuilder = new StringBuilder(Chat.SCORE);
            for (int i = 0; i < bp.getRoundWins(); i++) {
                if (i == bp.getRoundWins() - 1) {
                    long time = System.currentTimeMillis() - bp.getLastWin();
                    stringBuilder.append(POINT_ANIM[(int) Math.min(POINT_ANIM.length - 1, time / 150)]);
                } else {
                    stringBuilder.append("█");
                }
            }
            stringBuilder.append(Chat.DEFAULT);
            for (int i = bp.getRoundWins(); i < playToPoints; i++) {
                stringBuilder.append("─");
            }
            return stringBuilder.toString();
        }
    }

    public static String toScoreSquares(TeamBattleTeam<? extends TeamBattlePlayer> bp, int playToPoints) {
        if (playToPoints > 5) {
            return (Chat.SCORE + bp.getRoundWins() + "/" + playToPoints);
        } else {
            StringBuilder stringBuilder = new StringBuilder(Chat.SCORE);
            for (int i = 0; i < bp.getRoundWins(); i++) {
                if (i == bp.getRoundWins() - 1) {
                    long time = System.currentTimeMillis() - bp.getLastWin();
                    stringBuilder.append(POINT_ANIM[(int) Math.min(POINT_ANIM.length - 1, time / 150)]);
                } else {
                    stringBuilder.append("█");
                }
            }
            stringBuilder.append(Chat.DEFAULT);
            for (int i = bp.getRoundWins(); i < playToPoints; i++) {
                stringBuilder.append("─");
            }
            return stringBuilder.toString();
        }
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
     * @param positions Positions
     */
    public static void fillDome(GameWorld gameWorld, Material material, List<Position> positions) {
        for (Position pos : positions) {
            BuildStructure dome = BuildStructures.get("StartDome");
            for (Map.Entry<BlockPosition, FakeBlock> entry : dome.getFakeBlocks().entrySet()) {
                gameWorld.setBlock(entry.getKey().add(pos.toBlockPosition()), entry.getValue().getBlockData());
            }
        }
    }

    /**
     * Remove an area around a list of locations
     *
     * @param gameWorld Game World
     * @param positions Positions
     */
    public static void clearDome(GameWorld gameWorld, List<Position> positions) {
        for (Position pos : positions) {
            BuildStructure dome = BuildStructures.get("StartDome");
            for (Map.Entry<BlockPosition, FakeBlock> entry : dome.getFakeBlocks().entrySet()) {
                gameWorld.breakBlock(entry.getKey().add(pos.toBlockPosition()), null);
            }
        }
    }

}
