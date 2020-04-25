/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.BattlePlayer;
import com.spleefleague.core.game.battle.BattleStatic;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.spleef.SpleefBattlePlayer;

import java.util.List;

/**
 * @author NickM13
 */
public abstract class SpleefBattleStatic extends BattleStatic {

    public SpleefBattleStatic(List<CorePlayer> players,
                              SpleefArena arena,
                              Class<? extends BattlePlayer> battlePlayerClass) {
        super(Spleef.getInstance(), players, arena, battlePlayerClass);
    }

    @Override
    protected void setupBattlers() {
        super.setupBattlers();
        for (int i = 0; i < sortedBattlers.size(); i++) {
            ((SpleefBattlePlayer) sortedBattlers.get(i)).setSpawn(arena.getSpawns().get(i));
        }
    }

    @Override
    protected abstract void sendStartMessage();

    @Override
    protected abstract void applyEloChange(BattlePlayer winner);

    @Override
    public void surrender(CorePlayer cp) {
        if (battlers.containsKey(cp)) {
            if (battlers.size() <= 1) {
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + cp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered");
                endBattle();
            } else {
                SpleefBattlePlayer winner = (SpleefBattlePlayer) (battlers.keySet().toArray()[0].equals(cp)
                        ? battlers.values().toArray()[1]
                        : battlers.values().toArray()[0]);
                endBattle();
                /*
                Core.getInstance().sendMessage(Chat.PLAYER_NAME + dbp.getDisplayName() +
                        Chat.DEFAULT + " has surrendered to " +
                        Chat.PLAYER_NAME + winner.player.getDisplayName());
                */
            }
        }
    }

}
