/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.manager;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.queue.QueueContainer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * BattleManager contains a queue system, list of ongoing battles
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManager<B extends Battle<? extends Arena>> implements QueueContainer {
    
    public static BattleManager<? extends Battle<? extends Arena>> createManager(ArenaMode mode) {
        BattleManager<? extends Battle<? extends Arena>> bm = null;
        switch (mode.getTeamStyle()) {
            case SOLO:
                bm = new BattleManagerSolo<>(mode);
                break;
            case TEAM:
                bm = new BattleManagerTeam<>(mode);
                break;
            case MULTI_DYNAMIC:
                bm = new BattleManagerMultiDynamic<>(mode);
                break;
            case MULTI_STATIC:
                bm = new BattleManagerMultiStatic<>(mode);
                break;
            case MULTI_BANANA:
                bm = new BattleManagerMultiBonanza<>(mode);
                break;
        }
        bm.init();
        return bm;
    }
    
    Class<? extends Battle<? extends Arena>> battleClass;

    String name;
    String displayName;
    ArenaMode mode;

    List<Battle<? extends Arena>> battles;

    protected BattleManager(ArenaMode mode) {
        this.name = mode.getName();
        this.displayName = mode.getDisplayName();
        this.mode = mode;
        this.battleClass = mode.getBattleClass();

        this.battles = new ArrayList<>();
    }
    
    public void init() {
        // Should these be asynchronous?
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            Iterator<? extends Battle<?>> bit = battles.iterator();
            Battle<?> b;
            while (bit.hasNext()) {
                b = bit.next();
                if (b != null) {
                    if (!b.isOngoing()) {
                        bit.remove();
                    } else {
                        b.updateScoreboard();
                        b.doCountdown();
                    }
                }
            }
        }, 0L, 20L);
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            Iterator<? extends Battle<?>> bit = battles.iterator();
            Battle<?> b;
            while (bit.hasNext()) {
                b = bit.next();
                if (b != null) {
                    b.updateExperience();
                    b.updateField();
                }
            }
        }, 0L, 2L);
    }
    
    public void close() {
        for (Battle<?> b : battles) {
            b.endBattle();
        }
        battles.clear();
    }
    
    public int getOngoingBattles() {
        return battles.size();
    }
    
    public int getIngamePlayers() {
        int players = 0;
        for (Battle<?> b : battles) {
            players += b.getPlayers().size();
        }
        return players;
    }
    
    public int queuePlayer(CorePlayer cp) { return -1; }
    public int queuePlayer(CorePlayer cp, Arena arena) { return -1; }

    @Override
    public void checkQueue() { }
    
    public void startMatch(List<CorePlayer> corePlayers, String name) { }
    public void endMatch(B battle) {
        battles.remove(battle);
    }
    
}
