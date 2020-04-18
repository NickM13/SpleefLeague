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
import com.spleefleague.core.player.party.Party;
import com.spleefleague.core.player.CorePlayer;

import java.util.ArrayList;
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
public class BattleManagerMultiBonanza<B extends Battle<? extends Arena>> extends BattleManager<B> {
    
    public BattleManagerMultiBonanza(ArenaMode mode) {
        super(mode);
    }
    
    public Battle<? extends Arena> getMainBattle() {
        if (battles.isEmpty()) return null;
        return battles.get(0);
    }

    /**
     * Queue the player for a random arena
     *
     * @param cp DBPlayer
     * @return 0 for success, 1 for no battle, 2 for already ingame
     */
    @Override
    public int queuePlayer(CorePlayer cp) {
        if (getMainBattle() == null)
            startFirstAvailable();
        Battle<? extends Arena> battle = getMainBattle();
        if (battle != null) {
            if (cp.isInBattle()) {
                return 2;
            }
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

    /**
     * Starts a battle on a random arena
     */
    private void startFirstAvailable() {
        Arena arena = Arena.getRandomArena(mode);
        if (arena != null)
            startMatch(new ArrayList<>(), arena.getName());
    }

    /**
     * @param battle Battle
     * @param cp CorePlayer
     */
    private void addBattlePlayer(Battle<? extends Arena> battle, CorePlayer cp) {
        if (cp.getParty() != null) cp.getParty().leave(cp);
        Core.getInstance().unqueuePlayerGlobally(cp);
        battle.addBattler(cp);
    }

    /**
     * @param players Empty Player List
     * @param name Arena Name
     */
    @Override
    public void startMatch(List<CorePlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        Battle<?> sb = null;
        for (CorePlayer cp : players) {
            Party party = cp.getParty();
            if (party != null) {
                party.leave(cp);
            }
            if (cp.isInBattle()) {
                Core.getInstance().unqueuePlayerGlobally(cp);
                return;
            }
        }
        try {
            if (arena.isAvailable()) {
                List<CorePlayer> playersConverted = new ArrayList<>();
                for (CorePlayer cp : players) {
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
