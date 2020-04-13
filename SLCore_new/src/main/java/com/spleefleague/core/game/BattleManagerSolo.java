/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Single-player gamemodes, doesn't actually use a queue system!
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManagerSolo<B extends Battle> extends BattleManager<B> {
    
    public BattleManagerSolo(ArenaMode mode) {
        super(mode);
    }
    
    @Override
    public int queuePlayer(DBPlayer dbp) {
        CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
        startMatch(Lists.newArrayList(cp), "");
        return 0;
    }
    
    @Override
    public int queuePlayer(DBPlayer dbp, Arena arena) {
        if (arena == null) {
            return queuePlayer(dbp);
        } else {
            if (arena.isPaused()) {
                Core.sendMessageToPlayer(dbp, arena.getDisplayName() + " is currently disabled.");
                return 3;
            } else {
                CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
                startMatch(Lists.newArrayList(cp), arena.getName());
                return 0;
            }
        }
    }
    
    @Override
    public void startMatch(List<DBPlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        B sb = null;
        List<DBPlayer> playersFull = new ArrayList<>();
        playersFull = players;
        for (DBPlayer sp : playersFull) {
            CorePlayer cp = Core.getInstance().getPlayers().get(sp);
            Party party = cp.getParty();
            if (party != null) {
                party.leave(cp);
            }
            if (CorePlugin.isInBattleGlobal(sp.getPlayer())) {
                System.out.println("Player " + cp.getDisplayName() + " is already in a battle!");
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                for (DBPlayer dbp : playersFull) {
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
