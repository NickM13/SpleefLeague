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
import com.spleefleague.core.queue.PlayerQueue;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.ArrayList;
import java.util.List;

/**
 * BattleManagerTeam is a BattleManager that manages battles of static sized teams
 * Uses lead player of parties for queue id and parties for comparisons
 * 
 * @author NickM13
 * @param <B>
 */
public class BattleManagerTeam<B extends Battle> extends BattleManager<B> {
    
    protected PlayerQueue queue;
    
    public BattleManagerTeam(ArenaMode mode) {
        super(mode);
        
        queue = new PlayerQueue();
        queue.initialize(displayName, this, true);
    }
    
    private ArrayList<DBPlayer> gatherPlayers(int num, int teamSize) {
        ArrayList<DBPlayer> splayers = new ArrayList<>();
        ArrayList<DBPlayer> dbplayers = queue.getMatchedPlayers(num, teamSize);
        if (dbplayers == null) return null;
        for (DBPlayer dbp : dbplayers) {
            splayers.add(Core.getInstance().getPlayers().get(dbp.getPlayer()));
            if (splayers.size() == num) return splayers;
        }
        return null;
    }
    
    @Override
    public int queuePlayer(DBPlayer dbp) {
        CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
        Party party = cp.getParty();
        if (party == null) {
            Core.sendMessageToPlayer(dbp, "You must been in a party to join this queue!");
            return 1;
        }
        if (!this.mode.getRequiredTeamSizes().contains(party.getPlayers().size())) {
            Core.sendMessageToPlayer(dbp, "No queue exists for your party size!");
            return 2;
        }
        queue.queuePlayer(dbp);
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
                Party party = cp.getParty();
                if (party == null) {
                    Core.sendMessageToPlayer(dbp, "You must been in a party to join this queue!");
                    return 1;
                }
                if (arena.getTeamSize() != party.getPlayers().size()) {
                    Core.sendMessageToPlayer(dbp, "That arena requires a team size of " + arena.getTeamSize() + "!");
                    return 2;
                }
                queue.queuePlayer(dbp, arena);
                return 0;
            }
        }
    }

    @Override
    public void checkQueue() {
        for (int size : this.mode.getRequiredTeamSizes()) {
            if (queue.getQueueSize() >= this.mode.getRequiredTeams()) {
                ArrayList<DBPlayer> players = gatherPlayers(this.mode.getRequiredTeams(), size);
                if (players != null) {
                    startMatch(players, queue.getLastArenaName());
                }
            }
        }
    }
    
    @Override
    public void startMatch(List<DBPlayer> players, String name) {
        Arena arena = Arena.getByName(name, mode);
        B sb = null;
        List<DBPlayer> playersFull = new ArrayList<>();
        int size = -1;
        for (DBPlayer dbp : players) {
            CorePlayer cp = Core.getInstance().getPlayers().get(dbp);
            Party party = cp.getParty();
            if (party == null) return;
            if (size == -1) {
                size = party.getPlayers().size();
            } else if (size != party.getPlayers().size()) {
                return;
            }
            for (CorePlayer cp2 : party.getPlayers()) {
                playersFull.add(cp2);
                if (CorePlugin.isInBattleGlobal(cp2.getPlayer())) {
                    party.getChatGroup().sendMessage(cp2.getDisplayName() + " is already in a battle!");
                    System.out.println("Player " + cp2.getDisplayName() + " is already in a battle!");
                    Core.getInstance().unqueuePlayerGlobally(cp);
                    Core.getInstance().unqueuePlayerGlobally(cp2);
                    return;
                }
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
