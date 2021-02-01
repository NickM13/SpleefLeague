package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshQueue;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.utils.CoreUtils;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public class QueueManager {

    private final Map<String, QueueContainer> queueContainerMap = new HashMap<>();
    //private ScheduledTask queueTask;

    public void init() {
        addQueueContainer("spleef:classic", "Classic Spleef", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        addQueueContainer("spleef:power", "Power Spleef", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        addQueueContainer("spleef:power_training", "Power Training", 1, 1, QueueContainer.TeamStyle.SOLO, false);
        addQueueContainer("spleef:team", "Team Spleef", 2, 2, QueueContainer.TeamStyle.TEAM, false);
        addQueueContainer("spleef:multi", "Multispleef", 3, 32, QueueContainer.TeamStyle.DYNAMIC, false);
        addQueueContainer("spleef:bonanza", "Bonanza Spleef", 1, 1, QueueContainer.TeamStyle.BONANZA, true);

        addQueueContainer("splegg:versus", "Splegg Versus", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        addQueueContainer("splegg:multi", "Multisplegg", 3, 32, QueueContainer.TeamStyle.DYNAMIC, false);

        addQueueContainer("sj:classic", "SuperJump: Classic", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        addQueueContainer("sj:shuffle", "SuperJump: Shuffle", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        addQueueContainer("sj:conquest", "SuperJump: Conquest", 1, 1, QueueContainer.TeamStyle.SOLO, false);
        addQueueContainer("sj:endless", "SuperJump: Endless", 1, 1, QueueContainer.TeamStyle.SOLO, false);

        /*
        queueTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (QueueContainer queue : queueContainerMap.values()) {
                queue.checkQueue();
            }
        }, 5, 5, TimeUnit.SECONDS);
        */
    }

    public void addQueueContainer(String identifier, String displayName, int reqTeams, int maxTeams, QueueContainer.TeamStyle teamStyle, boolean joinOngoing) {
        queueContainerMap.put(identifier, new QueueContainer(identifier, displayName, reqTeams, maxTeams, teamStyle, joinOngoing));
    }

    public Map<String, QueueContainer> getContainerMap() {
        return queueContainerMap;
    }

    public void close() {
        //queueTask.cancel();
    }

    public boolean forceStart(String mode, String query, List<QueuePlayer> players) {
        if (!queueContainerMap.containsKey(mode)) {
            return false;
        }
        queueContainerMap.get(mode).startMatch(players, query);
        return true;
    }

    public void joinSolo(UUID uuid, String mode, String query) {
        queueContainerMap.get(mode).join(ProxyCore.getInstance().getPlayers().get(uuid), query);
    }

    private String formatQuery(String query) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SubQuery subQuery : SubQuery.splitQuery(query)) {
            if (!subQuery.hasStar) {
                stringBuilder.append(CoreUtils.mergeSetString(subQuery.values));
            }
        }
        return stringBuilder.toString();
    }

    private String formatArenaQuery(String mode, String query) {
        for (SubQuery subQuery : SubQuery.splitQuery(query)) {
            if (subQuery.type.equalsIgnoreCase("arena")) {
                if (subQuery.hasStar) {
                    return "";
                }
                StringBuilder stringBuilder = new StringBuilder(ChatColor.GOLD + ": ");
                for (int i = 0; i < subQuery.values.size(); i++) {
                    if (i > 0) {
                        stringBuilder.append(", ");
                    }
                    if (i > 3) {
                        stringBuilder.append("...");
                        break;
                    }
                    stringBuilder.append(ProxyCore.getInstance().getArenaManager().getArena(mode, subQuery.values.get(i)).getName());
                }
                return stringBuilder.toString() + ChatColor.GRAY;
            }
        }
        return "";
    }

    public void joinQueue(UUID uuid, String mode, String query) {
        QueueContainer queueContainer = queueContainerMap.get(mode);
        if (queueContainer == null) {
            System.out.println("Queue doesn't exist");
            return;
            //queueContainerMap.put(mode, new QueueContainer(mode));
        } else if (queueContainer.teamStyle == QueueContainer.TeamStyle.SOLO) {
            joinSolo(uuid, mode, query);
            return;
        }
        String arenaAffix = formatArenaQuery(mode, query);
        switch (queueContainer.join(ProxyCore.getInstance().getPlayers().get(uuid), query)) {
            case -1:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have left the queue for " +
                                queueContainer.getDisplayName());
                ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeRefreshQueue(
                        mode, queueContainer.getQueueSize(), queueContainer.getPlaying().size(), queueContainer.getSpectating().size()));
                break;
            case 0:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have joined the queue for " +
                                queueContainer.getDisplayName() +
                                arenaAffix);
                ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeRefreshQueue(
                        mode, queueContainer.getQueueSize(), queueContainer.getPlaying().size(), queueContainer.getSpectating().size()));
                break;
            case 1:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have rejoined the queue for " +
                                queueContainer.getDisplayName() +
                                arenaAffix);
                break;
        }
    }

    public boolean leaveQueue(UUID uuid, String mode, boolean sendPacketUpdate) {
        QueueContainer queueContainer = queueContainerMap.get(mode);
        if (queueContainer != null && queueContainerMap.get(mode).leave(ProxyCore.getInstance().getPlayers().get(uuid)) != null) {
            // You have successfully left the queue for (mode)
            //ProxyCore.sendMessage(ProxyCore.getInstance().getPlayers().get(uuid), "You have left the queue for " + mode);
            if (sendPacketUpdate) {
                ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeRefreshQueue(
                        mode, queueContainer.getQueueSize(), queueContainer.getPlaying().size(), queueContainer.getSpectating().size()));
            }
            return true;
        }
        return false;
    }

    public boolean leaveAllQueues(UUID uuid, boolean sendPacketUpdate) {
        boolean hasLeft = false;
        for (String mode : queueContainerMap.keySet()) {
            hasLeft |= leaveQueue(uuid, mode, sendPacketUpdate);
        }
        return hasLeft;
    }

}
