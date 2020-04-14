/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.multi;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleefBattleDynamic;
import java.util.List;

/**
 * @author NickM13
 */
public class MultiSpleefBattle extends SpleefBattleDynamic {
    
    protected long FIELD_RESET = 5000;
    protected long fieldResetTime = 0;
    
    public MultiSpleefBattle(List<DBPlayer> players, MultiSpleefArena arena) {
        super(players, arena);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", "Players (" + sortedBattlers.size() + ")");
        //chatGroup.setTeamScore("PlayerCount", playToPoints+1);
        //chatGroup.setTeamScore("PlayTo", playToPoints);
        
        BattlePlayer bp;
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            bp = sortedBattlers.get(i);
            chatGroup.setTeamDisplayName("PLACE" + i, Chat.PLAYER_NAME + bp.player.getName());
            //chatGroup.setTeamScore("PLACE" + i, bp.points);
        }
    }
    
    @Override
    public void updateField() {
        if (System.currentTimeMillis() > fieldResetTime) {
            fillField();
            fieldResetTime = System.currentTimeMillis() + FIELD_RESET;
        }
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
        chatGroup.addTeam("PlayerCount", Chat.SCORE + "Players");
        for (BattlePlayer bp : battlers.values()) {
            bp.player.getPlayer().getInventory().addItem(bp.player.getActiveShovel().getItem());
        }
    }
    
}
