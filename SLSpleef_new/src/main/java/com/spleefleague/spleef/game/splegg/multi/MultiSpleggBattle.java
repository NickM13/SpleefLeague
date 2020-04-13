/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.splegg.multi;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleggBattle;
import java.util.List;

/**
 * @author NickM13
 */
public class MultiSpleggBattle extends SpleggBattle {

    public MultiSpleggBattle(List<DBPlayer> players, MultiSpleggArena arena) {
        super(players, arena);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", "Players (" + sortedBattlers.size() + ")");
        chatGroup.setTeamScore("PlayerCount", playToPoints+1);
        chatGroup.setTeamScore("PlayTo", playToPoints);
        
        BattlePlayer bp;
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            bp = sortedBattlers.get(i);
            chatGroup.setTeamDisplayName("PLACE" + i, Chat.PLAYER_NAME + bp.player.getName());
            chatGroup.setTeamScore("PLACE" + i, bp.points);
        }
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
        chatGroup.addTeam("PlayerCount", Chat.SCORE + "Players");
        for (BattlePlayer bp : battlers.values()) {
            //bp.player.getPlayer().getInventory().addItem(bp.player.getActiveShovel().getItem());
        }
    }
    
}
