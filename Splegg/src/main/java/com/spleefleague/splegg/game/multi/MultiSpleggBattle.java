/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game.multi;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.splegg.game.SpleggBattle;

import java.util.List;

/**
 * @author NickM13
 */
public class MultiSpleggBattle extends SpleggBattle {

    public MultiSpleggBattle(List<CorePlayer> players, MultiSpleggArena arena) {
        super(players, arena);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", "Players (" + sortedBattlers.size() + ")");
        chatGroup.setTeamDisplayName("PlayTo", "PlayTo (" + 0 + ")");

        /*
        BattlePlayer bp;
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            bp = sortedBattlers.get(i);
            chatGroup.setTeamDisplayName("PLACE" + i, Chat.PLAYER_NAME + bp.player.getName() + ": " + bp.points);
        }
        */
    }

    @Override
    protected void setupBaseSettings() {

    }

    @Override
    protected void setupBattlers() {

    }

    @Override
    protected void sendStartMessage() {

    }

    @Override
    protected void fillField() {

    }

    @Override
    protected void joinBattler(CorePlayer cp) {

    }

    @Override
    protected void saveBattlerStats(CorePlayer cp) {

    }

    @Override
    protected void failBattler(CorePlayer cp) {

    }

    @Override
    protected void leaveBattler(CorePlayer cp) {

    }

}
