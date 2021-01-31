/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.manager;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * BattleManagerBonanza is a BattleManager that maintains
 * a single battle with an un-capped amount of players
 * Battle is started on server start and never stops
 * 
 * @author NickM13
 */
public class BattleManagerBonanza extends BattleManager {
    
    public BattleManagerBonanza(BattleMode mode) {
        super(mode);
    }
    
    public Battle<?> getMainBattle() {
        if (battles.isEmpty()) return null;
        return battles.get(0);
    }
    
    /**
     * Starts a battle on a random arena
     */
    private void startFirstAvailable() {
        Arena arena = Arenas.getRandom(mode);
        if (arena != null) {
            startMatch(new ArrayList<>(), arena.getName());
        }
    }

    /**
     * @param battle Battle
     * @param cp CorePlayer
     */
    private void addBattlePlayer(Battle<?> battle, CorePlayer cp) {
        battle.addSpectator(cp, null);
    }

    /**
     * @param players Empty Player List
     * @param arenaName Arena Name
     */
    @Override
    public void startMatch(List<CorePlayer> players, String arenaName) {
        Arena arena = Arenas.get(arenaName, mode);
        Battle<?> battle;
        for (CorePlayer cp : players) {
            if (!cp.canJoinBattle()) {
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                battle = battleClass
                        .getDeclaredConstructor(List.class, Arena.class)
                        .newInstance(players, arena);
                battle.startBattle();
                battles.add(battle);
            }
        } catch (Exception exception) {
            CoreLogger.logError("Unable to create battle", exception);
        }
    }
    
}
