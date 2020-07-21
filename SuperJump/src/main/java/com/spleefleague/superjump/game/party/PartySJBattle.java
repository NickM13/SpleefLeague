/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.party;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.dynamic.DynamicBattle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;

import java.util.List;

/**
 * @author NickM13
 */
public class PartySJBattle extends DynamicBattle<PartySJPlayer> {

    public PartySJBattle(List<CorePlayer> players, Arena arena) {
        super(SuperJump.getInstance(), players, arena, PartySJPlayer.class, SJMode.PARTY.getBattleMode());
    }
    
    @Override
    public void reset() {
    
    }
}
