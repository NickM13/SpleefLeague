package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleStart;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.arena.Arena;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class QueueContainerTeam extends QueueContainer {

    protected final Set<Integer> teamSizes = new HashSet<>();

    public QueueContainerTeam(String identifier, String displayName, int reqTeams, int maxTeams) {
        super(identifier, displayName, reqTeams, maxTeams);
        initTeamSizes();
    }

    public void initTeamSizes() {
        teamSizes.clear();
        for (Arena arena : ProxyCore.getInstance().getArenaManager().getArenas(identifier)) {
            teamSizes.add(arena.getTeamSize());
        }
    }

    /**
     * @param party Proxy Party
     * @param query Query
     * @return Player In Queue State (0 or 1 = in queue, -1 = left)
     */
    public int join(ProxyParty party, String query) {
        QueueParty replaced = leave(party);
        QueueParty queueParty = new QueueParty(party, query, party.getAvgRating(identifier, SEASON));
        if (queueParty.query.equals("arena:*") && replaced != null) return -1;
        queuedEntities.add(queueParty);
        queueSize++;
        return replaced != null ? 1 : 0;
    }

    /*
     * @param pcp Proxy Party
     * @return
     */
    public QueueParty leave(ProxyParty party) {
        Iterator<QueueEntity> qit = queuedEntities.iterator();
        while (qit.hasNext()) {
            QueueParty queueParty = (QueueParty) qit.next();
            if (queueParty.party.equals(party)) {
                qit.remove();
                queueSize--;
                return queueParty;
            }
        }
        return null;
    }

    protected boolean matchAfter(int teamSize, int start, int remaining, QueuedChunk queueChunk) {
        if (remaining <= 0) return true;
        ListIterator<QueueEntity> pit = queuedEntities.listIterator(start);
        while (pit.hasNext()) {
            QueueParty qp = (QueueParty) pit.next();
            if (qp.size != teamSize) {
                continue;
            }
            if (!queueChunk.join(qp)) {
                continue;
            }
            if (matchAfter(teamSize, pit.nextIndex(), remaining - 1, queueChunk)) {
                return true;
            }
        }
        return false;
    }

    public QueuedChunk getMatchedPlayers(int count, int teamSize) {
        QueuedChunk queueChunk = new QueuedChunk();
        ListIterator<QueueEntity> pit = queuedEntities.listIterator();

        while (pit.hasNext()) {
            QueueParty qp = (QueueParty) pit.next();
            queueChunk.start(qp);
            if (qp.size != teamSize) continue;
            if (matchAfter(teamSize, pit.nextIndex(), count - 1, queueChunk)) {
                return queueChunk;
            }
        }
        return null;
    }

    public void checkQueue() {
        for (int size : teamSizes) {
            while (getQueueSize() >= reqTeams) {
                QueuedChunk queueChunk = getMatchedPlayers(Math.max(Math.min(getQueueSize(), maxTeams), reqTeams), size);
                if (queueChunk != null) {
                    if (!startMatch(queueChunk.entities, queueChunk.query.toString())) {
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
    }

    public boolean startMatch(List<QueueEntity> parties, String query) {
        ServerInfo minigameServer = ProxyCore.getInstance().getMinigameServers().get(0);
        if (minigameServer == null) {
            ProxyCore.getInstance().getLogger().warning("There are no minigame servers available right now!");
            for (QueueEntity party : parties) {
                ((QueueParty) party).party.sendMessage(new TextComponent(ChatColor.RED + "No available minigame servers!"));
            }
            return false;
        }

        List<UUID> playerUuids = new ArrayList<>();

        for (QueueEntity party : parties) {
            for (UUID uuid : ((QueueParty) party).party.getPlayerList()) {
                ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
                pcp.setBattleContainer(this);
                pcp.setBattling(true);
                playing.add(uuid);
                pcp.getPlayer().connect(minigameServer);
                pcp.setLastQueueRequest(new PacketSpigotQueueJoin(pcp.getUniqueId(), identifier, party.query));
                playerUuids.add(uuid);
            }
            ProxyCore.getInstance().getQueueManager().leaveAllQueues(((QueueParty) party).party);
        }
        ProxyCore.getInstance().getPacketManager().sendPacket(minigameServer, new PacketBungeeBattleStart(identifier, query, playerUuids));
        return true;
    }

}
