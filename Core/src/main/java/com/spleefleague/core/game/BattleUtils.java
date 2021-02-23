package com.spleefleague.core.game;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.game.battle.team.TeamBattlePlayer;
import com.spleefleague.core.game.battle.team.TeamBattleTeam;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.build.BuildStructure;
import com.spleefleague.core.world.build.BuildStructures;
import com.spleefleague.core.world.game.GameWorld;
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
     *
     * @return Defeat Synonym
     */
    public static String randomDefeatSynonym() {
        Random r = new Random();
        return defeatedSynonyms[r.nextInt(defeatedSynonyms.length)];
    }

    private static final char[] POINT_ANIM = {/*'─', */'═', '╪', '▓', '█'};

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

    private static final int RESET_SQUARES = 4;

    public static String toRequestSquares(double percent) {
        percent *= RESET_SQUARES;
        int filledSquares = (int) Math.floor(percent);
        int emptySquares = (int) Math.ceil(percent);
        StringBuilder stringBuilder = new StringBuilder(Chat.SCORE);
        for (int i = 0; i < RESET_SQUARES; i++) {
            if (i >= emptySquares) {
                stringBuilder.append(Chat.DEFAULT).append('─');
            } else if (i < filledSquares) {
                stringBuilder.append('█');
            } else {
                stringBuilder.append(POINT_ANIM[(int) (percent % 1) * POINT_ANIM.length]);
            }
        }
        return stringBuilder.toString();
    }

    @SuppressWarnings("unused")
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

}
