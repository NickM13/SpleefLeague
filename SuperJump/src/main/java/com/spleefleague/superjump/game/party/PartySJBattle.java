/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.game.party;

import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.battle.dynamic.DynamicBattle;
import com.spleefleague.superjump.SuperJump;
import com.spleefleague.superjump.game.SJMode;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 */
public class PartySJBattle extends DynamicBattle<PartySJPlayer> {

    public PartySJBattle(UUID battleId, List<UUID> players, Arena arena) {
        super(SuperJump.getInstance(), battleId, players, arena, PartySJPlayer.class, SJMode.PARTY.getBattleMode());
    }
    
    @Override
    public void reset() {
    
    }

    @Override
    protected void sendEndMessage(PartySJPlayer partySJPlayer) {

    }

}
