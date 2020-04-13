/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.spleefleague.core.Core;
import com.spleefleague.core.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.Collections;
import java.util.List;

/**
 * BattleManagerMultiBonanza is a BattleManager that maintains
 * a single battle with an un-capped amount of players
 * Battle is started on server start and never stops
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManagerMultiBonanza<B extends Battle> extends BattleManager<B> {
    
    public BattleManagerMultiBonanza(ArenaMode mode) {
        super(mode);
    }
    
    public Battle getMainBattle() {
        if (battles.isEmpty()) return null;
        return battles.get(0);
    }
    
    @Override
    public int queuePlayer(DBPlayer dbp) {
        if (getMainBattle() == null)
            startFirstAvailable();
        Battle battle = getMainBattle();
        if (battle != null) {
            if (CorePlugin.isInBattleGlobal(dbp.getPlayer())) {
                return 2;
            }
            CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
            Party party = cp.getParty();
            if (party != null && party.isOwner(cp)) {
                for (CorePlayer cp2 : party.getPlayers()) {
                    addBattlePlayer(battle, cp2);
                }
            } else {
                addBattlePlayer(battle, cp);
            }
            return 0;
        } else {
            return 1;
        }
    }
    
    protected void startFirstAvailable() {
        Arena arena = Arena.getRandomArena(mode);
        startMatch(Collections.EMPTY_LIST, arena.getName());
    }
    
    public void addBattlePlayer(Battle battle, CorePlayer cp) {
        if (cp.getParty() != null) cp.getParty().leave(cp);
        Core.getInstance().unqueuePlayerGlobally(cp);
        battle.addBattler(cp);
    }
    
    @Override
    public void startMatch(List<DBPlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        B sb = null;
        for (DBPlayer dbp : players) {
            CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
            Party party = cp.getParty();
            if (party != null) {
                party.leave(cp);
            }
            if (CorePlugin.isInBattleGlobal(dbp.getPlayer())) {
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                for (DBPlayer dbp : players) {
                    CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
                    Core.getInstance().unqueuePlayerGlobally(cp);
                }
                sb = (B) battleClass
                        .getDeclaredConstructor(List.class, mode.arenaClass)
                        .newInstance(players, arena);
                sb.startBattle();
                battles.add(sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
