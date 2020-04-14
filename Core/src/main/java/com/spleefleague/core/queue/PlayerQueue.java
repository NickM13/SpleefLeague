/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.queue;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author NickM13
 */
public class PlayerQueue {
    private String name;
    private QueueContainer owner;
    
    private class QueuePlayer {
        DBPlayer dbp;
        Arena arena;
        
        public QueuePlayer(DBPlayer dbp, Arena arena) {
            this.dbp = dbp;
            this.arena = arena;
        }
        
        public boolean equals(DBPlayer dbp) {
            return this.dbp.equals(dbp);
        }
    }
    
    private boolean teamQueue;
    private List<QueuePlayer> players;
    
    public PlayerQueue() {}
    
    public void initialize(String name, QueueContainer qc, boolean teamQueue) {
        this.name = name;
        owner = qc;
        players = new ArrayList<>();
        this.teamQueue = teamQueue;
        Core.getInstance().addQueue(this);
    }
    public void terminate() {
        players.clear();
    }
    
    public boolean isTeamQueue() {
        return teamQueue;
    }
    public void checkQueue() {
        owner.checkQueue();
    }
    
    public String getQueueName() {
        return name;
    }
    public int getQueueSize() {
        return players.size();
    }
    
    private int findPlayer(DBPlayer dbp) {
        // Probably rework this at some point?
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).dbp.getPlayer().getName().equals(dbp.getPlayer().getName())) {
                return i;
            }
        }
        return -1;
    }
    public boolean queuePlayer(DBPlayer dbp) {
        if (CorePlugin.isInBattleGlobal(dbp.getPlayer())) {
            Core.getInstance().sendMessage(dbp, "Please leave your current game to queue");
            return false;
        }
        int id;
        if ((id = findPlayer(dbp)) != -1) {
            players.remove(id);
            Core.getInstance().sendMessage(dbp, "You have left the queue for " +
                    Chat.GAMEMODE + this.name);
            return false;
        }
        Core.getInstance().sendMessage(dbp, "You have joined the queue for " +
                Chat.GAMEMODE + this.name);
        players.add(new QueuePlayer(dbp, null));
        checkQueue();
        return true;
    }
    public boolean queuePlayer(DBPlayer dbp, Arena arena) {
        if (arena == null) {
            return queuePlayer(dbp);
        }
        if (CorePlugin.isInBattleGlobal(dbp.getPlayer())) {
            Core.getInstance().sendMessage(dbp, "Please leave your current battle to queue");
            return false;
        }
        int id;
        if ((id = findPlayer(dbp)) != -1) {
            if (players.get(id).arena == arena) {
                Core.getInstance().sendMessage(dbp, "You are already in queue for " +
                        Chat.GAMEMODE + this.name +
                        Chat.BRACE + " (" +
                        Chat.GAMEMAP + arena.getDisplayName() +
                        Chat.BRACE + ")");
            } else {
                if (players.get(id).arena != null)
                    players.get(id).arena.decrementQueues();
                players.get(id).arena = arena;
                players.get(id).arena.incrementQueues();
                Core.getInstance().sendMessage(dbp, "You have joined the queue for " +
                        Chat.GAMEMODE + this.name +
                        Chat.BRACE + " (" +
                        Chat.GAMEMAP + arena.getDisplayName() +
                        Chat.BRACE + ")");
            }
            return false;
        }
        Core.getInstance().sendMessage(dbp, "You have joined the queue for " +
                Chat.GAMEMODE + this.name +
                Chat.BRACE + " (" +
                Chat.GAMEMAP + arena.getDisplayName() +
                Chat.BRACE + ")");
        players.add(new QueuePlayer(dbp, arena));
        arena.incrementQueues();
        checkQueue();
        return true;
    }
    
    public boolean unqueuePlayer(DBPlayer dbp) {
        int id;
        if ((id = findPlayer(dbp)) != -1) {
            //Core.getInstance().sendMessage(player, "You have left the queue for " +
            //        Chat.GAMEMODE + this.name);
            QueuePlayer qp = players.get(id);
            if (qp.arena != null) {
                qp.arena.decrementQueues();
            }
            players.remove(id);
            Core.getInstance().sendMessage(dbp, "You have left the queue for " +
                    Chat.GAMEMODE + this.name);
            return true;
        }
        return false;
    }
    public void unqueuePlayers(ArrayList<DBPlayer> dbps) {
        Iterator<DBPlayer> pit = dbps.iterator();
        while (pit.hasNext()) {
            unqueuePlayer(pit.next());
        }
    }
    
    private Arena lastParam = null;
    public String getLastArenaName() {
        if (lastParam == null) return "";
        return lastParam.getName();
    }
    public DBPlayer getPlayerFirst() {
        return players.get(0).dbp;
    }
    private boolean matchAfter(int partySize, Arena arena, int start, int remaining, ArrayList<QueuePlayer> list) {
        if (remaining <= 0) {
            return true;
        }
        ListIterator<QueuePlayer> pit;
        QueuePlayer qp;
        pit = this.players.listIterator(start);
        while (pit.hasNext()) {
            qp = pit.next();
            CorePlayer cp1 = Core.getInstance().getPlayers().get(qp.dbp);
            Party party = cp1.getParty();
            if (teamQueue && (party == null || party.getPlayers().size() != partySize)) {
                continue;
            }
            if (arena == null || qp.arena == null) {
                if (qp.arena != null) {
                    Arena _arena = qp.arena;
                    list.add(qp);
                    if (matchAfter(partySize, _arena, pit.nextIndex(), remaining-1, list)) {
                        arena = _arena;
                        return true;
                    }
                    return false;
                } else {
                    list.add(qp);
                    return matchAfter(partySize, arena, pit.nextIndex(), remaining-1, list);
                }
            } else {
                if (arena.equals(qp.arena)) {
                    list.add(qp);
                    return matchAfter(partySize, arena, pit.nextIndex(), remaining-1, list);
                }
            }
        }
        return false;
    }
    public ArrayList<DBPlayer> getMatchedPlayers(int count, int teamSize) {
        ArrayList<DBPlayer> dbps = new ArrayList<>();
        ArrayList<QueuePlayer> qplayers = new ArrayList<>();
        ListIterator<QueuePlayer> pit = this.players.listIterator();
        
        while (pit.hasNext()) {
            QueuePlayer qp = pit.next();
            qplayers.clear();
            qplayers.add(qp);
            Arena arena = qp.arena;
            CorePlayer cp1 = Core.getInstance().getPlayers().get(qp.dbp);
            Party party = cp1.getParty();
            if (teamQueue && party != null && party.getPlayers().size() == teamSize) {
                if (matchAfter(teamSize, arena, pit.nextIndex(), count-1, qplayers)) {
                    qplayers.forEach(qp2 -> dbps.add(qp2.dbp));
                    lastParam = arena;
                    return dbps;
                }
            } else {
                if (matchAfter(0, arena, pit.nextIndex(), count-1, qplayers)) {
                    qplayers.forEach(qp2 -> dbps.add(qp2.dbp));
                    lastParam = arena;
                    return dbps;
                }
            }
        }
        return null;
    }
    public List<QueuePlayer> getPlayers() {
        return players;
    }
    
}
