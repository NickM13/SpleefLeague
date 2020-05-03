/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.manager;

import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.battle.Battle;
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
 */
public abstract class BattleManager implements QueueContainer {
    
    /**
     * Returns a new battle manager based on the team style of the passed ArenaMode
     *
     * @param mode Arena Mode
     * @return New Battle Manager
     */
    public static BattleManager createManager(BattleMode mode) {
        BattleManager bm = null;
        switch (mode.getTeamStyle()) {
            case SOLO:      bm = new BattleManagerSolo(mode);       break;
            case TEAM:      bm = new BattleManagerTeam(mode);       break;
            case DYNAMIC:   bm = new BattleManagerDynamic(mode);    break;
            case VERSUS:    bm = new BattleManagerVersus(mode);     break;
            case BONANZA:   bm = new BattleManagerBonanza(mode);    break;
        }
        bm.init();
        return bm;
    }
    
    protected final String name;
    protected final String displayName;
    protected final BattleMode mode;
    protected Class<? extends Battle<?>> battleClass;

    protected final List<Battle<?>> battles;

    protected BattleManager(BattleMode mode) {
        this.name = mode.getName();
        this.displayName = mode.getDisplayName();
        this.mode = mode;
        this.battleClass = mode.getBattleClass();

        this.battles = new ArrayList<>();
    }
    
    /**
     * Initializes task timers to update battles, removing ones
     * that are marked for removal, updating scores and countdown,
     * and updating the field and experience bar for timers
     */
    public void init() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
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
    
    /**
     * Terminates battles
     */
    public void close() {
        for (Battle<?> battle : battles) {
            battle.endBattle();
        }
        battles.clear();
    }
    
    /**
     * Returns the number of battles currently contained by this manager
     *
     * @return Battle Count
     */
    public int getOngoingBattles() {
        return battles.size();
    }
    
    /**
     * Returns the number of players currently in all of the battles in
     * this manager including spectators
     *
     * @return Player Count
     */
    public int getIngamePlayers() {
        int players = 0;
        for (Battle<?> battle : battles) {
            players += battle.getPlayers().size();
        }
        return players;
    }
    
    public abstract int queuePlayer(CorePlayer cp);
    public abstract int queuePlayer(CorePlayer cp, Arena arena);

    @Override
    public abstract void checkQueue();
    
    public abstract void startMatch(List<CorePlayer> corePlayers, String name);
    public void endMatch(Battle<?> battle) {
        battles.remove(battle);
    }
    
}
