/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.queue;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.party.CoreParty;

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

    private static class QueuePlayer {
        CorePlayer cp;
        Arena arena;
        long joinTime;

        public QueuePlayer(CorePlayer dbp, Arena arena) {
            this.cp = dbp;
            this.arena = arena;
            this.joinTime = System.currentTimeMillis();
        }

        public boolean equals(CorePlayer dbp) {
            return this.cp.equals(dbp);
        }
    }

    private boolean teamQueue;
    private List<QueuePlayer> players;

    public PlayerQueue() {
    }

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

    private int findPlayer(CorePlayer cp) {
        // TODO: Probably rework this at some point?
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).cp.getPlayer().getName().equals(cp.getPlayer().getName())) {
                return i;
            }
        }
        return -1;
    }

    public boolean queuePlayer(CorePlayer cp) {
        if (cp.isInBattle()) {
            Core.getInstance().sendMessage(cp, "Please leave your current game to queue");
            return false;
        }
        int id;
        if ((id = findPlayer(cp)) != -1) {
            players.remove(id);
            Core.getInstance().sendMessage(cp, "You have left the queue for " +
                    Chat.GAMEMODE + this.name);
            return false;
        }
        Core.getInstance().sendMessage(cp, "You have joined the queue for " +
                Chat.GAMEMODE + this.name);
        players.add(new QueuePlayer(cp, null));
        checkQueue();
        return true;
    }

    public boolean queuePlayer(CorePlayer cp, Arena arena) {
        if (arena == null) {
            return queuePlayer(cp);
        }
        if (cp.isInBattle()) {
            Core.getInstance().sendMessage(cp, "Please leave your current battle to queue");
            return false;
        }
        int id;
        if ((id = findPlayer(cp)) != -1) {
            if (players.get(id).arena == arena) {
                Core.getInstance().sendMessage(cp, "You are already in queue for " +
                        Chat.GAMEMODE + this.name +
                        Chat.TAG_BRACE + " (" +
                        Chat.GAMEMAP + arena.getName() +
                        Chat.TAG_BRACE + ")");
            } else {
                if (players.get(id).arena != null)
                    players.get(id).arena.decrementQueues();
                players.get(id).arena = arena;
                players.get(id).arena.incrementQueues();
                Core.getInstance().sendMessage(cp, "You have joined the queue for " +
                        Chat.GAMEMODE + this.name +
                        Chat.TAG_BRACE + " (" +
                        Chat.GAMEMAP + arena.getName() +
                        Chat.TAG_BRACE + ")");
            }
            return false;
        }
        Core.getInstance().sendMessage(cp, "You have joined the queue for " +
                Chat.GAMEMODE + this.name +
                Chat.TAG_BRACE + " (" +
                Chat.GAMEMAP + arena.getName() +
                Chat.TAG_BRACE + ")");
        players.add(new QueuePlayer(cp, arena));
        arena.incrementQueues();
        checkQueue();
        return true;
    }

    public boolean unqueuePlayer(CorePlayer cp) {
        int id;
        if ((id = findPlayer(cp)) != -1) {
            QueuePlayer qp = players.get(id);
            if (qp.arena != null) {
                qp.arena.decrementQueues();
            }
            players.remove(id);
            return true;
        }
        return false;
    }

    public void unqueuePlayers(List<CorePlayer> cps) {
        Iterator<CorePlayer> pit = cps.iterator();
        while (pit.hasNext()) {
            unqueuePlayer(pit.next());
        }
    }

    private String lastParam = "";

    public String getLastArenaName() {
        return lastParam;
    }

    public CorePlayer getPlayerFirst() {
        return players.get(0).cp;
    }

    private boolean matchAfter(int partySize, StringBuilder arenaName, int start, int remaining, List<QueuePlayer> list) {
        if (remaining <= 0) {
            return true;
        }
        ListIterator<QueuePlayer> pit;
        QueuePlayer qp;
        pit = this.players.listIterator(start);
        while (pit.hasNext()) {
            qp = pit.next();
            CorePlayer cp1 = Core.getInstance().getPlayers().get(qp.cp);
            CoreParty party = cp1.getParty();
            if (teamQueue && (party == null || party.getPlayerSet().size() != partySize)) {
                continue;
            }
            if (arenaName.toString().equals("") || qp.arena == null) {
                if (qp.arena != null) {
                    StringBuilder _arena = new StringBuilder(qp.arena.getName());
                    list.add(qp);
                    if (matchAfter(partySize, _arena, pit.nextIndex(), remaining - 1, list)) {
                        arenaName.delete(0, arenaName.length());
                        arenaName.append(_arena);
                        return true;
                    }
                    return false;
                } else {
                    list.add(qp);
                    return matchAfter(partySize, arenaName, pit.nextIndex(), remaining - 1, list);
                }
            } else {
                if (arenaName.toString().equalsIgnoreCase(qp.arena.getName())) {
                    list.add(qp);
                    return matchAfter(partySize, arenaName, pit.nextIndex(), remaining - 1, list);
                }
            }
        }
        return false;
    }

    public List<CorePlayer> getMatchedPlayers(int count, int teamSize) {
        List<CorePlayer> cps = new ArrayList<>();
        List<QueuePlayer> queuePlayers = new ArrayList<>();
        ListIterator<QueuePlayer> pit = this.players.listIterator();

        while (pit.hasNext()) {
            QueuePlayer qp = pit.next();
            queuePlayers.clear();
            queuePlayers.add(qp);
            StringBuilder arenaName = new StringBuilder(qp.arena == null ? "" : qp.arena.getName());
            CorePlayer cp1 = qp.cp;
            CoreParty party = cp1.getParty();
            if (teamQueue && party != null && party.getPlayerSet().size() == teamSize) {
                if (matchAfter(teamSize, arenaName, pit.nextIndex(), count - 1, queuePlayers)) {
                    queuePlayers.forEach(qp2 -> cps.add(qp2.cp));
                    lastParam = arenaName.toString();
                    return cps;
                }
            } else {
                if (matchAfter(0, arenaName, pit.nextIndex(), count - 1, queuePlayers)) {
                    queuePlayers.forEach(qp2 -> cps.add(qp2.cp));
                    lastParam = arenaName.toString();
                    return cps;
                }
            }
        }
        return null;
    }

    public List<QueuePlayer> getPlayers() {
        return players;
    }

}
