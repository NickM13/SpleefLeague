/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.multi;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.battle.dynamic.DynamicBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefArena;

import java.util.List;

/**
 * @author NickM13
 */
public class MultiSpleefBattle extends DynamicBattle<SpleefArena, MultiSpleefPlayer> {
    
    protected long FIELD_RESET = 5000;
    protected long fieldResetTime = 0;
    
    public MultiSpleefBattle(List<CorePlayer> players,
                             MultiSpleefArena arena) {
        super(Spleef.getInstance(), players, arena, MultiSpleefPlayer.class);
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", "Players (" + sortedBattlers.size() + ")");
        //chatGroup.setTeamScore("PlayTo", playToPoints);
    }

    @Override
    public void updateField() {
        if (System.currentTimeMillis() > fieldResetTime) {
            fillField();
            fieldResetTime = System.currentTimeMillis() + FIELD_RESET;
        }
    }
    
}
