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
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.CorePlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * BattleManagerTeam is a BattleManager that manages battles of static sized teams
 * Uses lead player of parties for queue id and parties for comparisons
 * 
 * @author NickM13
 */
public class BattleManagerTeam extends BattleManager {
    
    public BattleManagerTeam(BattleMode mode) {
        super(mode);
    }
    
    @Override
    public void startMatch(List<CorePlayer> players, String arenaName) {
        Arena arena = Arenas.get(arenaName, mode);
        if (arena == null) {
            CoreLogger.logError("", new NullPointerException("Null arena: " + arenaName));
            return;
        }
        Battle<?> battle;
        List<CorePlayer> playersFull = new ArrayList<>();
        int size = -1;
        for (CorePlayer cp : players) {
            Party party = cp.getParty();
            if (party == null) return;
            if (size == -1) {
                size = party.getPlayers().size();
            } else if (size != party.getPlayers().size()) {
                return;
            }
            for (CorePlayer cp2 : party.getPlayers()) {
                playersFull.add(cp2);
                if (!cp2.canJoinBattle()) {
                    party.getChatGroup().sendMessage(cp2.getDisplayName() + " is already in a battle!");
                    Core.getInstance().unqueuePlayerGlobally(cp);
                    Core.getInstance().unqueuePlayerGlobally(cp2);
                    return;
                }
            }
        }
        try {
            if (arena.isAvailable()) {
                for (CorePlayer cp : playersFull) {
                    Core.getInstance().unqueuePlayerGlobally(cp);
                }
                battle = battleClass
                        .getDeclaredConstructor(List.class, Arena.class)
                        .newInstance(players, arena);
                battle.startBattle();
                battles.add(battle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
