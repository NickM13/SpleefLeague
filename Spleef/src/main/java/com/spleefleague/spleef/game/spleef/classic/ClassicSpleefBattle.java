/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.classic;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.game.BattleUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.spleef.game.SpleefBattleStatic;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;

import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSpleefBattle extends SpleefBattleStatic {
    
    public ClassicSpleefBattle(List<CorePlayer> players,
                               ClassicSpleefArena arena) {
        super(players, arena, ClassicSpleefBattlePlayer.class);
    }

    @Override
    protected void sendStartMessage() {
        Core.getInstance().sendMessage("Starting match on Classic Spleef");
    }

    @Override
    protected void joinBattler(CorePlayer dbPlayer) {
        Core.getInstance().sendMessage(dbPlayer.getDisplayName() + " joined a Classic Spleef");
    }

    @Override
    protected void applyEloChange(BattlePlayer winner) {
        int avgRating = 0;
        int eloChange = 0;

        for (BattlePlayer bp : battlers.values()) {
            avgRating += bp.getCorePlayer().getRating(getMode());
        }
        avgRating /= battlers.size();

        eloChange = (int) (20 - Math.max(0, Math.min(2, Math.sqrt(Math.abs((double)(winner.getCorePlayer().getRating(getMode()) - avgRating))) * 100)));
        if (winner.getCorePlayer().getRating(getMode()) > avgRating) {
            eloChange = 20 - (int)(Math.min((winner.getCorePlayer().getRating((getMode())) - avgRating) / 100.0, 1) * 15);
        } else {
            eloChange = 20 + (int)(Math.min((avgRating - winner.getCorePlayer().getRating((getMode()))) / 100.0, 1) * 20);
        }

        for (BattlePlayer bp : battlers.values()) {
            if (bp.equals(winner)) {
                bp.getCorePlayer().addRating(getMode(), eloChange);
            } else {
                bp.getCorePlayer().addRating(getMode(), -eloChange);
            }
        }
    }

    @Override
    public void updateScoreboard() {
        players.forEach(sp -> {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(sp.getUniqueId());
        });
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        //chatGroup.setTeamDisplayName("PlayTo", "PlayTo: " + playToPoints);
        
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            SpleefBattlePlayer sbp = (SpleefBattlePlayer) sortedBattlers.get(i);
            chatGroup.setTeamDisplayName("PLACE" + i, Chat.PLAYER_NAME + sbp.getCorePlayer().getName() + ": " + sbp.getPoints());
        }
    }

    @Override
    public void updateField() {

    }

    @Override
    public void updateExperience() {

    }

    @Override
    protected void endRound(BattlePlayer winner) {
        SpleefBattlePlayer wsbp = (SpleefBattlePlayer) battlers.get(winner);
        wsbp.addPoints(1);
        sortBattlers();
        if (wsbp.getPoints() < playToPoints) {
            Core.getInstance().sendMessage(chatGroup, Chat.PLAYER_NAME + winner.getCorePlayer().getDisplayName() + Chat.DEFAULT + " won the round");
        } else {
            endBattle(winner);
        }
    }

    @Override
    protected void endBattle(BattlePlayer winner) {
        SpleefBattlePlayer wsbp = (SpleefBattlePlayer) battlers.get(winner);
        SpleefBattlePlayer lsbp = null;
        for (CorePlayer dbp : battlers.keySet()) {
            if (!dbp.equals(winner)) {
                lsbp = (SpleefBattlePlayer) battlers.get(dbp);
                break;
            }
        }
        if (lsbp == null) {
            lsbp = wsbp;
        }
        applyEloChange(wsbp);
        Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.SPLEEF),
                Chat.PLAYER_NAME + wsbp.getCorePlayer().getDisplayName() +
                        wsbp.getCorePlayer().getDisplayElo(getMode()) +
                        Chat.DEFAULT + " has " + BattleUtils.randomDefeatSynonym() + " " +
                        Chat.PLAYER_NAME + lsbp.getCorePlayer().getDisplayName() +
                        lsbp.getCorePlayer().getDisplayElo(getMode()) +
                        Chat.DEFAULT + " in " +
                        Chat.GAMEMODE + getMode().getDisplayName() + " " +
                        Chat.DEFAULT + "(" +
                        Chat.SCORE + wsbp.getPoints() + Chat.DEFAULT + "-" + Chat.SCORE + lsbp.getPoints() +
                        Chat.DEFAULT + ")");
        super.endBattle();
    }

    @Override
    protected void resetPlayer(CorePlayer cp) {

    }

}
