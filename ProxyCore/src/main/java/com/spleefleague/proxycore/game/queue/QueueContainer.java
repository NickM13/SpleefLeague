package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBattleStart;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshQueue;
import com.spleefleague.coreapi.utils.packet.spigot.PacketQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ProxyParty;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;
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
    private final String displayName;
    private final List<QueuePlayer> queuedPlayers;
    private final Set<Integer> teamSizes;
    private final int reqTeams;
    private final int maxTeams;
    TeamStyle teamStyle;
    private boolean joinOngoing;
    private ScheduledTask nextCheck = null;
    private final Set<UUID> spectating;
    private final Set<UUID> playing;

    private final int DYNAMIC_DELAY_START = 3;

    public QueueContainer(String identifier, String displayName, int reqTeams, int maxTeams, TeamStyle teamStyle, boolean joinOngoing) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.queuedPlayers = new ArrayList<>();
        this.teamSizes = new HashSet<>();
        if (teamStyle != TeamStyle.TEAM) {
            teamSizes.add(1);
        }
        this.reqTeams = reqTeams;
        this.maxTeams = maxTeams;
        this.joinOngoing = joinOngoing;
        this.spectating = new HashSet<>();
        this.playing = new HashSet<>();
    }

    public String getDisplayName() {
        return ChatColor.GOLD + displayName + ChatColor.GRAY;
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
        if (nextCheck == null) {
            if (queuedPlayers.size() >= this.maxTeams) {
                nextCheck = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::checkQueue, 500, TimeUnit.MILLISECONDS);
            } else if (queuedPlayers.size() >= this.reqTeams) {
                TextComponent accept = new TextComponent(Chat.TAG_BRACE + "[" + Chat.SUCCESS + "Queue Now" + Chat.TAG_BRACE + "]");
                accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join!").create()));
                accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request queue " + identifier));
                TextComponent text = new TextComponent(ProxyCore.getChatTag() + getDisplayName() + " match starting in " + DYNAMIC_DELAY_START + " seconds ");
                text.addExtra(accept);
                ProxyCore.getInstance().sendMessage(text);
                nextCheck = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::checkQueue, DYNAMIC_DELAY_START * 1000L, TimeUnit.MILLISECONDS);
            }
        }
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

    /**
     * Attemps to match two query searches, returning null if any sub queries return an empty string
     *
     * @param query1 Query String
     * @param query2 Query String
     * @return
     */
    private String matchQueries(String query1, String query2) {
        StringBuilder matchedQuery = new StringBuilder();
        SubQuery[] subQueries1 = SubQuery.splitQuery(query1);
        SubQuery[] subQueries2 = SubQuery.splitQuery(query2);
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

    private boolean matchAfter(int teamSize, StringBuilder mainQuery, int start, int remaining, QueuedChunk list) {
        if (remaining <= 0) return true;
        ListIterator<QueuePlayer> pit = queuedPlayers.listIterator(start);
        while (pit.hasNext()) {
            QueuePlayer qp = pit.next();
            ProxyCorePlayer pcp1 = qp.pcp;
            ProxyParty party = pcp1.getParty();
            if (teamSize > 1 && (party == null || party.getPlayers().size() != teamSize)) {
                continue;
            }
            String newQuery = matchQueries(mainQuery.toString(), qp.query);
            if (newQuery == null) {
                return false;
            } else {
                StringBuilder queryBuilder = new StringBuilder(newQuery);
                list.players.add(qp);
                if (matchAfter(teamSize, queryBuilder, pit.nextIndex(), remaining-1, list)) {
                    mainQuery.delete(0, mainQuery.length());
                    mainQuery.append(queryBuilder.toString());
                    return true;
                }
            }
        }
        return false;
    }

    public QueuedChunk getMatchedPlayers(int count, int teamSize) {
        QueuedChunk queuePlayers = new QueuedChunk();
        ListIterator<QueuePlayer> pit = queuedPlayers.listIterator();

        while (pit.hasNext()) {
            QueuePlayer qp = pit.next();
            queuePlayers.players.clear();
            queuePlayers.players.add(qp);
            StringBuilder mainQuery = new StringBuilder(qp.query);
            ProxyCorePlayer pcp1 = qp.pcp;
            if (pcp1 == null) continue;
            ProxyParty party = pcp1.getParty();
            if (teamSize <= 1 || (party != null && party.getPlayers().size() == teamSize)) {
                if (matchAfter(teamSize, mainQuery, pit.nextIndex(), count-1, queuePlayers)) {
                    queuePlayers.query = mainQuery.toString();
                    return queuePlayers;
                }
            }
        }
        return null;
    }

    private static class QueuedChunk {
        List<QueuePlayer> players = new ArrayList<>();
        String query;
    }

    private QueuedChunk gatherPlayers(int count, int size) {
        QueuedChunk cchunk = getMatchedPlayers(count, size);
        if (cchunk == null) return null;
        QueuedChunk gatheredChunk = new QueuedChunk();
        gatheredChunk.query = cchunk.query;
        for (QueuePlayer cp : cchunk.players) {
            gatheredChunk.players.add(cp);
            if (gatheredChunk.players.size() == count) {
                return gatheredChunk;
            }
        }
        return null;
    }

    public void checkQueue() {
        for (int size : teamSizes) {
            while (getQueueSize() >= reqTeams) {
                QueuedChunk chunk = gatherPlayers(Math.max(Math.min(getQueueSize(), maxTeams), reqTeams), size);
                if (chunk != null) {
                    if (!startMatch(chunk.players, chunk.query)) {
                        break;
                    }
                    if (getQueueSize() < maxTeams) {
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        nextCheck = null;
    }

    public boolean startMatch(List<QueuePlayer> players, String query) {
        ServerInfo minigameServer = ProxyCore.getInstance().getMinigameServers().get(0);
        if (minigameServer == null) {
            ProxyCore.getInstance().getLogger().warning("There are no minigame servers available right now!");
            for (QueuePlayer qp : players) {
                ProxyCore.getInstance().sendMessage(qp.pcp, ChatColor.RED + "No available minigame servers!");
            }
            return false;
        }

        List<UUID> playerUuids = new ArrayList<>();

        for (QueuePlayer qp : players) {
            if (qp.pcp.isBattling()) return false;
            playerUuids.add(qp.pcp.getUniqueId());
        }
        for (QueuePlayer qp : players) {
            QueueManager.leaveAllQueues(qp.pcp.getUniqueId(), false);
            qp.pcp.setBattleContainer(this);
            qp.pcp.setBattling(true);
            playing.add(qp.pcp.getUniqueId());
            qp.pcp.getPlayer().connect(minigameServer);
            qp.pcp.setLastQueueRequest(new PacketQueueJoin(qp.pcp.getUniqueId(), identifier, qp.query));
        }
        ProxyCore.getInstance().sendPacket(minigameServer, new PacketBattleStart(identifier, query, playerUuids));
        ProxyCore.getInstance().sendPacket(new PacketRefreshQueue(new QueueContainerInfo(identifier, queuedPlayers.size(), playing.size(), spectating.size())));
        return true;
    }

    public void removePlayer(UUID uuid) {
        spectating.remove(uuid);
        playing.remove(uuid);
    }

    public Set<UUID> getSpectating() {
        return spectating;
    }

    public void addSpectator(UUID uuid) {
        spectating.add(uuid);
    }

    public Set<UUID> getPlaying() {
        return playing;
    }

}
