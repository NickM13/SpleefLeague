/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.classic;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.scoreboard.PersonalScoreboard;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.spleef.game.SpleefBattleStatic;
import java.util.List;

/**
 * @author NickM13
 */
public class ClassicSpleefBattle extends SpleefBattleStatic {
    
    public ClassicSpleefBattle(List<DBPlayer> players, ClassicSpleefArena arena) {
        super(players, arena);
    }
    
    @Override
    protected void startBattle() {
        super.startBattle();
    }
    
    @Override
    public void updateScoreboard() {
        players.forEach(sp -> {
            PersonalScoreboard ps = PersonalScoreboard.getScoreboard(sp.getUniqueId());
        });
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        //chatGroup.setTeamDisplayName("PlayTo", "PlayTo: " + playToPoints);
        
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            BattlePlayer bp = sortedBattlers.get(i);
            chatGroup.setTeamDisplayName("PLACE" + i, Chat.PLAYER_NAME + bp.player.getName() + ": " + bp.points);
        }
    }
    
}
