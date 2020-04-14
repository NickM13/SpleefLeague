/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.queue.QueueContainer;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;

/**
 * BattleManager contains a queue system, list of ongoing battles
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManager<B extends Battle> implements QueueContainer {
    
    public static BattleManager createManager(ArenaMode mode) {
        BattleManager bm;
        switch (mode.teamStyle) {
            case SOLO:
                bm = new BattleManagerSolo(mode);
                break;
            case TEAM:
                bm = new BattleManagerTeam(mode);
                break;
            case MULTI_DYNAMIC:
                bm = new BattleManagerMultiDynamic(mode);
                break;
            case MULTI_STATIC:
                bm = new BattleManagerMultiStatic(mode);
                break;
            case MULTI_BANANA:
                bm = new BattleManagerMultiBonanza(mode);
                break;
            default:
                bm = null;
                break;
        }
        if (bm != null) {
            bm.init();
        }
        return bm;
    }
    
    Class battleClass;

    String name;
    String displayName;
    ArenaMode mode;

    List<B> battles = new ArrayList<>();

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
            Iterator<B> bit = battles.iterator();
            Battle b;
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
            Iterator<B> bit = battles.iterator();
            Battle b;
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
        for (B b : battles) {
            b.endBattle();
        }
        battles.clear();
    }
    
    public int getOngoingBattles() {
        return battles.size();
    }
    
    public int getIngamePlayers() {
        int players = 0;
        for (B b : battles) {
            players += b.getPlayers().size();
        }
        return players;
    }
    
    public int queuePlayer(DBPlayer dbp) { return -1; }
    public int queuePlayer(DBPlayer dbp, Arena arena) { return -1; }

    @Override
    public void checkQueue() { }
    
    public B getPlayerBattle(DBPlayer player) {
        return (B) player.getBattle();
    }
    
    public void startMatch(List<DBPlayer> players, String name) { }
    public void endMatch(B battle) {
        battles.remove(battle);
    }
    
}
