package com.spleefleague.proxycore.game.queue;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ProxyParty;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public class QueueContainer {

    public enum TeamStyle {
        SOLO,
        VERSUS,
        TEAM,
        DYNAMIC,
        BONANZA
    }

    private final String identifier;
    private final List<QueuePlayer> queuedPlayers;
    private final Set<Integer> teamSizes;
    private int reqTeams;
    private int maxTeams;
    TeamStyle teamStyle;
    private boolean joinOngoing;

    public QueueContainer(String identifier, String displayName, int reqTeams, int maxTeams, TeamStyle teamStyle, boolean joinOngoing) {
        this.identifier = identifier;
        this.queuedPlayers = new ArrayList<>();
        this.teamSizes = new HashSet<>();
        if (teamStyle != TeamStyle.TEAM) {
            teamSizes.add(1);
        }
        this.reqTeams = reqTeams;
        this.maxTeams = maxTeams;
        this.joinOngoing = joinOngoing;
    }

    public void addTeamSize(int size) {
        teamSizes.add(size);
    }

    public int getQueueSize() {
        return queuedPlayers.size();
    }

    /**
     * Adds a player to the queue, returning true if the player did not previously exist, or false if the player
     * was already in the list, updating the queue parameters
     *
     * @param pcp Proxy Core Player
     * @param query Query
     * @return Player In Queue State (0 or 1 = in queue, -1 = left)
     */
    public int join(ProxyCorePlayer pcp, String query) {
        QueuePlayer replaced = leave(pcp);
        QueuePlayer qp = new QueuePlayer(pcp, query);
        if (qp.query.equals("arena:*") && replaced != null) return -1;
        queuedPlayers.add(qp);
        ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::checkQueue, 500, TimeUnit.MILLISECONDS);
        return replaced != null ? 1 : 0;
    }

    /**
     * Removes a player from the queue, returning true if a player was successfully removed
     *
     * @param pcp Proxy Core Player
     * @return
     */
    public QueuePlayer leave(ProxyCorePlayer pcp) {
        Iterator<QueuePlayer> qit = queuedPlayers.iterator();
        while (qit.hasNext()) {
            QueuePlayer qp = qit.next();
            if (qp == null) {
                qit.remove();
            } else if (qp.pcp.equals(pcp)) {
                qit.remove();
                return qp;
            }
        }
        return null;
    }

    private String lastQuery = "";
    public String getLastQuery() {
        return lastQuery;
    }

    public QueuePlayer getPlayerFirst() {
        return queuedPlayers.get(0);
    }

    private static class SubQuery {

        String type;
        List<String> values;
        boolean hasStar;

        SubQuery(String section) {
            String[] splits = section.split(":");
            this.type = splits[0];
            this.values = new ArrayList<>();
            for (String s : splits[1].split(",")) {
                if (s.equals("*")) {
                    this.hasStar = true;
                } else {
                    this.values.add(s);
                }
            }
        }

        /**
         * Both sub queries should be either whitelist or blacklist (with or without star)
         *
         * @param query1 Sub Query
         * @param query2 Sub Query
         * @return
         */
        private static String mergeSame(SubQuery query1, SubQuery query2) {
            StringBuilder merged = new StringBuilder();
            for (String v : query1.values) {
                if (merged.length() > 0) {
                    merged.append(",");
                }
                merged.append(v);
            }
            for (String v : query2.values) {
                if (merged.length() > 0) {
                    merged.append(",");
                }
                merged.append(v);
            }
            return merged.toString();
        }

        /**
         * Takes a whitelist and blacklist of values and produces a new string
         *
         * @param whitelist Whitelist Sub Query
         * @param blacklist Blacklist Sub Query
         * @return
         */
        private static String mergeOther(SubQuery whitelist, SubQuery blacklist) {
            StringBuilder merged = new StringBuilder();
            for (String v1 : whitelist.values) {
                boolean ignored = false;
                for (String v2 : blacklist.values) {
                    if (v1.equals(v2)) {
                        ignored = true;
                        break;
                    }
                }
                if (!ignored) {
                    merged.append(v1);
                }
            }
            return merged.toString();
        }

        /**
         * Compare two sub queries of the same type, returning a new query string
         * (empty string should exit player match)
         *
         * @param that Sub Query
         * @return New Query String
         */
        public String compareValue(SubQuery that) {
            String newVal;
            if (hasStar) {
                if (that.hasStar) {
                    newVal = "*," + mergeSame(this, that);
                } else {
                    newVal = mergeOther(that, this);
                }
            } else {
                if (that.hasStar) {
                    newVal = mergeOther(this, that);
                } else {
                    newVal = mergeSame(this, that);
                }
            }
            return newVal;
        }

    }

    private SubQuery[] splitQuery(String query) {
        String[] sections = query.split(";");
        SubQuery[] subQueries = new SubQuery[sections.length];
        for (int i = 0; i < sections.length; i++) {
            subQueries[i] = new SubQuery(sections[i]);
        }
        return subQueries;
    }

    /**
     * Attemps to match two query searches, returning null if any sub queries return an empty string
     *
     * @param query1 Query String
     * @param query2 Query String
     * @return
     */
    private String matchQueries(String query1, String query2) {
        StringBuilder matchedQuery = new StringBuilder();
        SubQuery[] subQueries1 = splitQuery(query1);
        SubQuery[] subQueries2 = splitQuery(query2);
        boolean matched;
        for (SubQuery sq1 : subQueries1) {
            matched = false;
            for (SubQuery sq2 : subQueries2) {
                if (sq2.type.equals(sq1.type)) {
                    String newQuery = sq1.compareValue(sq2);
                    if (newQuery.isEmpty()) return null;
                    matchedQuery.append(matchedQuery.length() > 0 ? ";" : "").append(sq1.type).append(":").append(newQuery);
                    matched = true;
                }
            }
            if (!matched) {
                ProxyCore.getInstance().getLogger().severe("QueueContainer.java: Something went wrong with matching queries!");
                return null;
            }
        }
        return matchedQuery.toString();
    }

    private boolean matchAfter(int teamSize, StringBuilder mainQuery, int start, int remaining, List<QueuePlayer> list) {
        if (remaining <= 0) {
            return true;
        }
        ListIterator<QueuePlayer> pit;
        QueuePlayer qp;
        pit = queuedPlayers.listIterator(start);
        while (pit.hasNext()) {
            qp = pit.next();
            ProxyCorePlayer pcp1 = qp.pcp;
            ProxyParty party = pcp1.getParty();
            if (teamSize > 1 && (party == null || party.getPlayers().size() != teamSize)) {
                continue;
            }
            String newQuery = matchQueries(mainQuery.toString(), qp.query);
            if (newQuery == null) {
                return false;
            }

            if (mainQuery.toString().equals("") || qp.query.equals("*")) {
                if (!qp.query.equals("*")) {
                    StringBuilder _arena = new StringBuilder(qp.query);
                    list.add(qp);
                    if (matchAfter(teamSize, _arena, pit.nextIndex(), remaining-1, list)) {
                        mainQuery.delete(0, mainQuery.length());
                        mainQuery.append(_arena);
                        return true;
                    }
                    return false;
                } else {
                    list.add(qp);
                    return matchAfter(teamSize, mainQuery, pit.nextIndex(), remaining-1, list);
                }
            } else {
                if (mainQuery.toString().equalsIgnoreCase(qp.query)) {
                    list.add(qp);
                    return matchAfter(teamSize, mainQuery, pit.nextIndex(), remaining-1, list);
                }
            }
        }
        return false;
    }

    public List<QueuePlayer> getMatchedPlayers(int count, int teamSize) {
        List<QueuePlayer> queuePlayers = new ArrayList<>();
        ListIterator<QueuePlayer> pit = queuedPlayers.listIterator();

        while (pit.hasNext()) {
            QueuePlayer qp = pit.next();
            queuePlayers.clear();
            queuePlayers.add(qp);
            StringBuilder mainQuery = new StringBuilder(qp.query);
            ProxyCorePlayer pcp1 = qp.pcp;
            if (pcp1 == null) continue;
            ProxyParty party = pcp1.getParty();
            if (teamSize <= 1 || (party != null && party.getPlayers().size() == teamSize)) {
                if (matchAfter(teamSize, mainQuery, pit.nextIndex(), count-1, queuePlayers)) {
                    lastQuery = mainQuery.toString();
                    return queuePlayers;
                }
            }
        }
        return null;
    }

    private List<QueuePlayer> gatherPlayers(int count, int size) {
        List<QueuePlayer> gatheredPlayers = new ArrayList<>();
        List<QueuePlayer> cplayers = getMatchedPlayers(count, size);
        if (cplayers == null) return null;
        for (QueuePlayer cp : cplayers) {
            gatheredPlayers.add(cp);
            if (gatheredPlayers.size() == count) {
                return gatheredPlayers;
            }
        }
        return null;
    }

    public void checkQueue() {
        for (int size : teamSizes) {
            if (getQueueSize() >= reqTeams) {
                List<QueuePlayer> players = gatherPlayers(reqTeams, size);
                if (players != null) {
                    startMatch(players, getLastQuery());
                }
            }
        }
    }

    public void startMatch(List<QueuePlayer> players, String query) {
        ServerInfo minigameServer = ProxyCore.getInstance().getMinigameServers().get(0);
        if (minigameServer == null) {
            ProxyCore.getInstance().getLogger().warning("There are no minigame servers available right now!");
            return;
        }

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(identifier);
        output.writeUTF(query);
        output.writeUTF(""); // Arena name, temporarily empty for all arenas
        output.writeInt(players.size());
        for (QueuePlayer qp : players) {
            if (qp.pcp.isInBattle()) {
                return;
            }
            output.writeUTF(qp.pcp.getUniqueId().toString());
        }
        for (QueuePlayer qp : players) {
            QueueManager.leaveAllQueues(qp.pcp.getUniqueId());
            qp.pcp.setInBattle(true);
            qp.pcp.getPlayer().connect(minigameServer);
        }
        minigameServer.sendData("battle:start", output.toByteArray());
    }

}
