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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * BattleManagerTeam is a BattleManager that manages battles of
 * any number of players, and once it has begun players can't
 * enter the battle but if a player leaves it does not stop
 *
 * @author NickM13
 */
public class BattleManagerDynamic extends BattleManager {

    protected static Long DELAY_START_TIME = 1000L;

    protected Long delayedStart = null;

    public BattleManagerDynamic(BattleMode mode) {
        super(mode);
    }

    @Override
    public void startMatch(List<CorePlayer> players, String arenaName) {
        Arena arena = Arenas.get(arenaName, mode);
        if (arena == null) return;
        Battle<?> battle;
        for (CorePlayer cp : players) {
            Core.getInstance().getPartyManager().leave(cp);
            if (!cp.canJoinBattle()) {
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                for (CorePlayer cp : players) {
                    Core.getInstance().unqueuePlayerGlobally(cp);
                }
                battle = battleClass
                        .getDeclaredConstructor(List.class, Arena.class)
                        .newInstance(players, arena);
                battle.startBattle();
                battles.add(battle);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            CoreLogger.logError("A battle failed to begin on arena " + arena.getName(), exception);
        }
    }

}
