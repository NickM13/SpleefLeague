package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleStart;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshQueue;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class QueueContainerSolo extends QueueContainer {

    public QueueContainerSolo(String identifier, String displayName, int reqTeams, int maxTeams) {
        super(identifier, displayName, reqTeams, maxTeams);
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
        QueuePlayer qp = new QueuePlayer(pcp, query, pcp.getRatings().getElo(identifier, SEASON));
        if (qp.query.equals("arena:*") && replaced != null) return -1;
        queuedEntities.add(qp);
        queueSize += qp.size;
        return replaced != null ? 1 : 0;
    }

    /**
     * Removes a player from the queue, returning true if a player was successfully removed
     *
     * @param pcp Proxy Core Player
     * @return
     */
    public QueuePlayer leave(ProxyCorePlayer pcp) {
        Iterator<QueueEntity> qit = queuedEntities.iterator();
        while (qit.hasNext()) {
            QueuePlayer qe = (QueuePlayer) qit.next();
            if (qe.pcp.equals(pcp)) {
                qit.remove();
                queueSize -= qe.size;
                return qe;
            }
        }
        return null;
    }

    public void checkQueue() {
        for (QueueEntity qp : queuedEntities) {
            startMatch((QueuePlayer) qp, qp.query);
        }
    }

    public boolean startMatch(QueuePlayer qp, String query) {
        ServerInfo minigameServer = ProxyCore.getInstance().getMinigameServers().get(0);
        if (minigameServer == null) {
            ProxyCore.getInstance().getLogger().warning("There are no minigame servers available right now!");
            ProxyCore.getInstance().sendMessage(qp.pcp, ChatColor.RED + "No available minigame servers!");
            return false;
        }

        List<UUID> playerUuids = new ArrayList<>();

        if (qp.pcp.isBattling()) return false;
        playerUuids.add(qp.pcp.getUniqueId());

        ProxyCore.getInstance().getQueueManager().leaveAllQueues(qp.pcp.getUniqueId());
        qp.pcp.setBattleContainer(this);
        qp.pcp.setBattling(true);
        playing.add(qp.pcp.getUniqueId());
        qp.pcp.getPlayer().connect(minigameServer);
        qp.pcp.setLastQueueRequest(new PacketSpigotQueueJoin(qp.pcp.getUniqueId(), identifier, qp.query));

        ProxyCore.getInstance().getPacketManager().sendPacket(minigameServer, new PacketBungeeBattleStart(identifier, query, playerUuids));
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeRefreshQueue(new QueueContainerInfo(identifier, queuedEntities.size(), playing.size(), spectating.size())));
        return true;
    }

}
