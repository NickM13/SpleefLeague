/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.battle.multi;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.dynamic.DynamicBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.util.SpleefUtils;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class MultiSpleefBattle extends DynamicBattle<MultiSpleefPlayer> {
    
    public MultiSpleefBattle(List<UUID> players,
                             Arena arena) {
        super(Spleef.getInstance(), players, arena, MultiSpleefPlayer.class, SpleefMode.MULTI.getBattleMode());
    }
    
    @Override
    public void fillField() {
        SpleefUtils.fillFieldFast(this, null);
    }
    
    @Override
    public void reset() {
        fillField();
    }
    
    @Override
    public void updateScoreboard() {
        chatGroup.setScoreboardName(Chat.DEFAULT + getRuntimeString() + "     " + Chat.SCORE + "Score");
        chatGroup.setTeamDisplayName("PlayerCount", "Players (" + battlers.size() + ")");
        //chatGroup.setTeamScore("PlayTo", playToPoints);
    }

    @Override
    public void updateField() {
    
    }
    
}
