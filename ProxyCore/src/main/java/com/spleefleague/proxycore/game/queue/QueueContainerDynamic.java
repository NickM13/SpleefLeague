package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleStart;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshQueue;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class QueueContainerDynamic extends QueueContainer {

    protected long delayStart = -1;

    public QueueContainerDynamic(String identifier, String displayName, int reqTeams, int maxTeams) {
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
        queueSize++;
        checkDelayedStart();
        return replaced != null ? 1 : 0;
    }

    public int join(ProxyParty party, String query) {
        QueueParty replaced = leave(party);
        QueueParty qp = new QueueParty(party, query, party.getAvgRating(identifier, SEASON));
        if (qp.query.equals("arena:*") && replaced != null) return -1;
        queueSize += qp.size;
        checkDelayedStart();
        return replaced != null ? 1 : 0;
    }

    private void checkDelayedStart() {
        if (getQueueSize() >= this.maxTeams) {
            delayStart = 0;
        } else {
            if (delayStart < 0) {
                if (getQueueSize() >= this.reqTeams) {
                    TextComponent text = new TextComponent(getDisplayName() + " match starting in " + DYNAMIC_DELAY_START + " seconds ");
                    TextComponent accept = new TextComponent(Chat.TAG_BRACE + "[" + Chat.SUCCESS + "Queue Now" + Chat.TAG_BRACE + "]");
                    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join!").create()));
                    accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/request queue " + identifier));
                    text.addExtra(accept);
                    ProxyCore.getInstance().sendMessage(text);
                    delayStart = System.currentTimeMillis() + DYNAMIC_DELAY_START * 1000L;
                }
            }
        }
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
            QueueEntity qe = qit.next();
            if (qe instanceof QueuePlayer && ((QueuePlayer) qe).pcp.equals(pcp)) {
                qit.remove();
                queueSize -= qe.size;
                return (QueuePlayer) qe;
            }
        }
        return null;
    }

    /**
     * Removes a player from the queue, returning true if a player was successfully removed
     *
     * @param party Proxy Party
     * @return
     */
    public QueueParty leave(ProxyParty party) {
        Iterator<QueueEntity> qit = queuedEntities.iterator();
        while (qit.hasNext()) {
            QueueEntity qe = qit.next();
            if (qe instanceof QueueParty && ((QueueParty) qe).party == party) {
                qit.remove();
                queueSize -= qe.size;
                return (QueueParty) qe;
            }
        }
        return null;
    }

    protected boolean matchAfter(int start, int maxCount, QueuedChunk queueChunk) {
        if (maxCount <= 0) return true;
        ListIterator<QueueEntity> pit = queuedEntities.listIterator(start);
        while (pit.hasNext()) {
            QueueEntity qp = pit.next();
            if (!queueChunk.joinNoRating(qp)) {
                continue;
            }
            if (matchAfter(pit.nextIndex(), maxCount - 1, queueChunk)) {
                return true;
            }
        }
        return false;
    }

    public QueuedChunk getMatchedPlayers(int minCount, int maxCount) {
        QueuedChunk queueChunk = new QueuedChunk();
        ListIterator<QueueEntity> pit = queuedEntities.listIterator();
        while (pit.hasNext()) {
            QueueEntity queueEntity = pit.next();
            queueChunk.start(queueEntity);
            if (matchAfter(pit.nextIndex(), maxCount - 1, queueChunk)) {
                return queueChunk;
            }
            if (queueChunk.total >= minCount) {
                return queueChunk;
            }
        }
        return null;
    }


    public void checkQueue() {
        if (delayStart < System.currentTimeMillis() && delayStart >= 0) {
            QueuedChunk chunk = getMatchedPlayers(reqTeams, maxTeams);
            if (chunk != null) startMatch(chunk.entities, chunk.query.toString());
            delayStart = -1;
            checkDelayedStart();
        }
    }

    public boolean startMatch(List<QueueEntity> entities, String query) {
        ServerInfo minigameServer = ProxyCore.getInstance().getMinigameServers().get(0);
        if (minigameServer == null) {
            ProxyCore.getInstance().getLogger().warning("There are no minigame servers available right now!");
            for (QueueEntity qp : entities) {
                if (qp instanceof QueuePlayer) {
                    ProxyCore.getInstance().sendMessage(((QueuePlayer) qp).pcp, ChatColor.RED + "No available minigame servers!");
                }
            }
            return false;
        }

        List<UUID> playerUuids = new ArrayList<>();

        for (QueueEntity queueEntity : entities) {
            if (queueEntity instanceof QueuePlayer) {
                QueuePlayer qp = (QueuePlayer) queueEntity;
                ProxyCore.getInstance().getQueueManager().leaveAllQueues(qp.pcp.getUniqueId());
                qp.pcp.setBattleContainer(this);
                qp.pcp.setBattling(true);
                playing.add(qp.pcp.getUniqueId());
                qp.pcp.getPlayer().connect(minigameServer);
                qp.pcp.setLastQueueRequest(new PacketSpigotQueueJoin(qp.pcp.getUniqueId(), identifier, queueEntity.query));
                playerUuids.add(qp.pcp.getUniqueId());
            } else if (queueEntity instanceof QueueParty) {
                QueueParty qp = (QueueParty) queueEntity;
                for (ProxyCorePlayer pcp : qp.party.getPlayerSet()) {
                    ProxyCore.getInstance().getQueueManager().leaveAllQueues(pcp.getUniqueId());
                    pcp.setBattleContainer(this);
                    pcp.setBattling(true);
                    playing.add(pcp.getUniqueId());
                    pcp.getPlayer().connect(minigameServer);
                    pcp.setLastQueueRequest(new PacketSpigotQueueJoin(pcp.getUniqueId(), identifier, queueEntity.query));
                    playerUuids.add(pcp.getUniqueId());
                }
            }
        }
        ProxyCore.getInstance().getPacketManager().sendPacket(minigameServer, new PacketBungeeBattleStart(identifier, query, playerUuids));
        return true;
    }

}
