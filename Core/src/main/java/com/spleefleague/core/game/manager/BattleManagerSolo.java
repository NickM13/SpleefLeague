/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game.manager;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.game.Battle;
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.CorePlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * Single-player gamemodes, doesn't actually use a queue system!
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManagerSolo<B extends Battle<? extends Arena>> extends BattleManager<B> {
    
    public BattleManagerSolo(ArenaMode mode) {
        super(mode);
    }
    
    @Override
    public int queuePlayer(CorePlayer cp) {
        startMatch(Lists.newArrayList(cp), "");
        return 0;
    }
    
    @Override
    public int queuePlayer(CorePlayer cp, Arena arena) {
        if (arena == null) {
            return queuePlayer(cp);
        } else {
            if (!arena.isAvailable()) {
                Core.getInstance().sendMessage(cp, arena.getDisplayName() + " is currently disabled.");
                return 3;
            } else {
                startMatch(Lists.newArrayList(cp), arena.getName());
                return 0;
            }
        }
    }
    
    @Override
    public void startMatch(List<CorePlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        Battle<?> sb = null;
        List<CorePlayer> playersFull = new ArrayList<>();
        playersFull = players;
        for (CorePlayer cp : playersFull) {
            Party party = cp.getParty();
            if (party != null) {
                party.leave(cp);
            }
            if (cp.isInBattle()) {
                System.out.println("Player " + cp.getDisplayName() + " is already in a battle!");
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                for (CorePlayer cp : playersFull) {
                    Core.getInstance().unqueuePlayerGlobally(cp);
                }
                sb = battleClass
                        .getDeclaredConstructor(List.class, mode.getArenaClass())
                        .newInstance(players, arena);
                sb.startBattle();
                battles.add(sb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
