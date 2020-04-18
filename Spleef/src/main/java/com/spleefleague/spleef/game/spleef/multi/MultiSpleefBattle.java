/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.multi;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.game.SpleefBattleDynamic;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;

import java.util.List;

/**
 * @author NickM13
 */
public class MultiSpleefBattle extends SpleefBattleDynamic {
    
    protected long FIELD_RESET = 5000;
    protected long fieldResetTime = 0;
    
    public MultiSpleefBattle(List<CorePlayer> players,
                             MultiSpleefArena arena) {
        super(players, arena, MultiSpleefBattlePlayer.class);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", "Players (" + sortedBattlers.size() + ")");
        //chatGroup.setTeamScore("PlayerCount", playToPoints+1);
        //chatGroup.setTeamScore("PlayTo", playToPoints);
        
        SpleefBattlePlayer sbp;
        for (int i = 0; i < sortedBattlers.size() && i < seenScores; i++) {
            sbp = (SpleefBattlePlayer) sortedBattlers.get(i);
            chatGroup.setTeamDisplayName("PLACE" + i, Chat.PLAYER_NAME + sbp.getCorePlayer().getName());
            //chatGroup.setTeamScore("PLACE" + i, bp.points);
        }
    }

    @Override
    protected void endRound(BattlePlayer winner) {

    }

    @Override
    protected void endBattle(BattlePlayer winner) {

    }

    @Override
    public void updateField() {
        if (System.currentTimeMillis() > fieldResetTime) {
            fillField();
            fieldResetTime = System.currentTimeMillis() + FIELD_RESET;
        }
    }
    
}
