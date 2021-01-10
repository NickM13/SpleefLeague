package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.PacketRefreshQueue;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.utils.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public class QueueManager {

    private static final Map<String, QueueContainer> queueContainerMap = new HashMap<>();
    //private static ScheduledTask queueTask;

    public static void init() {
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

    public static void addQueueContainer(String identifier, String displayName, int reqTeams, int maxTeams, QueueContainer.TeamStyle teamStyle, boolean joinOngoing) {
        queueContainerMap.put(identifier, new QueueContainer(identifier, displayName, reqTeams, maxTeams, teamStyle, joinOngoing));
    }

    public static Map<String, QueueContainer> getContainerMap() {
        return queueContainerMap;
    }

    public static void close() {
        //queueTask.cancel();
    }

    public static boolean forceStart(String mode, String query, List<QueuePlayer> players) {
        if (!queueContainerMap.containsKey(mode)) {
            return false;
        }
        queueContainerMap.get(mode).startMatch(players, query);
        return true;
    }

    public static void joinSolo(UUID uuid, String mode, String query) {
        queueContainerMap.get(mode).join(ProxyCore.getInstance().getPlayers().get(uuid), query);
    }

    private static String formatQuery(String query) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SubQuery subQuery : SubQuery.splitQuery(query)) {
            if (!subQuery.hasStar) {
                stringBuilder.append(CoreUtils.mergeSetString(subQuery.values));
            }
        }
        return stringBuilder.toString();
    }

    public static void joinQueue(UUID uuid, String mode, String query) {
        QueueContainer queueContainer = queueContainerMap.get(mode);
        if (queueContainer == null) {
            System.out.println("Queue doesn't exist");
            return;
            //queueContainerMap.put(mode, new QueueContainer(mode));
        } else if (queueContainer.teamStyle == QueueContainer.TeamStyle.SOLO) {
            joinSolo(uuid, mode, query);
            return;
        }
        switch (queueContainer.join(ProxyCore.getInstance().getPlayers().get(uuid), query)) {
            case -1:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have left the queue for " +
                                queueContainer.getDisplayName());
                ProxyCore.getInstance().sendPacket(new PacketRefreshQueue(
                        mode, queueContainer.getQueueSize(), queueContainer.getPlaying().size(), queueContainer.getSpectating().size()));
                break;
            case 0:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have joined the queue for " +
                                queueContainer.getDisplayName()
                                /*ChatColor.GRAY + " (" + query + ")"*/);
                ProxyCore.getInstance().sendPacket(new PacketRefreshQueue(
                        mode, queueContainer.getQueueSize(), queueContainer.getPlaying().size(), queueContainer.getSpectating().size()));
                break;
            case 1:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have rejoined the queue for " +
                                queueContainer.getDisplayName()
                                /*ChatColor.GRAY + " (" + query + ")"*/);
                break;
        }
    }

    public static boolean leaveQueue(UUID uuid, String mode, boolean sendPacketUpdate) {
        QueueContainer queueContainer = queueContainerMap.get(mode);
        if (queueContainer != null && queueContainerMap.get(mode).leave(ProxyCore.getInstance().getPlayers().get(uuid)) != null) {
            // You have successfully left the queue for (mode)
            //ProxyCore.sendMessage(ProxyCore.getInstance().getPlayers().get(uuid), "You have left the queue for " + mode);
            if (sendPacketUpdate) {
                ProxyCore.getInstance().sendPacket(new PacketRefreshQueue(
                        mode, queueContainer.getQueueSize(), queueContainer.getPlaying().size(), queueContainer.getSpectating().size()));
            }
            return true;
        }
        return false;
    }

    public static boolean leaveAllQueues(UUID uuid, boolean sendPacketUpdate) {
        boolean hasLeft = false;
        for (String mode : queueContainerMap.keySet()) {
            hasLeft |= leaveQueue(uuid, mode, sendPacketUpdate);
        }
        return hasLeft;
    }

}
