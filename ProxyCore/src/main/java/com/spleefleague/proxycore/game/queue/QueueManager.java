package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.queue.SubQuery;
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
    private static ScheduledTask queueTask;

    public static void init() {
        addQueueContainer("spleef:classic", "Classic Spleef", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        addQueueContainer("spleef:power", "Power Spleef", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        //addQueueContainer("spleef:team", "Team Spleef", 2, 2, QueueContainer.TeamStyle.TEAM, false);
        //addQueueContainer("spleef:multi", "Multispleef", 3, 32, QueueContainer.TeamStyle.DYNAMIC, true);
        //addQueueContainer("spleef:bonanza", "Bonanza Spleef", 1, 1, QueueContainer.TeamStyle.BONANZA, true);

        addQueueContainer("splegg:classic", "Classic Splegg", 2, 2, QueueContainer.TeamStyle.VERSUS, false);
        addQueueContainer("splegg:multi", "Multisplegg", 3, 32, QueueContainer.TeamStyle.DYNAMIC, true);

        queueTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (QueueContainer queue : queueContainerMap.values()) {
                //queue.checkQueue();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static void addQueueContainer(String identifier, String displayName, int reqTeams, int maxTeams, QueueContainer.TeamStyle teamStyle, boolean joinOngoing) {
        queueContainerMap.put(identifier, new QueueContainer(identifier, displayName, reqTeams, maxTeams, teamStyle, joinOngoing));
    }

    public static void close() {
        queueTask.cancel();
    }

    public static boolean forceStart(String mode, String query, List<QueuePlayer> players) {
        if (!queueContainerMap.containsKey(mode)) {
            return false;
        }
        queueContainerMap.get(mode).startMatch(players, query);
        return true;
    }

    public static void joinSolo(UUID uuid, String mode, String query) {
        if (!queueContainerMap.containsKey(mode)) {
            // Just a workaround to avoid setting up training modes
            addQueueContainer(mode, mode, 1, 1, QueueContainer.TeamStyle.SOLO, false);
        }
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
        if (!queueContainerMap.containsKey(mode)) {
            System.out.println("Queue doesn't exist");
            return;
            //queueContainerMap.put(mode, new QueueContainer(mode));
        }
        QueueContainer queueContainer = queueContainerMap.get(mode);
        switch (queueContainer.join(ProxyCore.getInstance().getPlayers().get(uuid), query)) {
            case -1:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have left the queue for " +
                                queueContainer.getDisplayName());
                break;
            case 0:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have joined the queue for " +
                                queueContainer.getDisplayName() +
                                ChatColor.GRAY + " (" + query + ")");
                break;
            case 1:
                ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                        "You have rejoined the queue for " +
                                queueContainer.getDisplayName() +
                                ChatColor.GRAY + " (" + query + ")");
                break;
        }
    }

    public static void leaveQueue(UUID uuid, String mode) {
        if (queueContainerMap.containsKey(mode)) {
            if (queueContainerMap.get(mode).leave(ProxyCore.getInstance().getPlayers().get(uuid)) != null) {
                // You have successfully left the queue for (mode)
                //ProxyCore.sendMessage(ProxyCore.getInstance().getPlayers().get(uuid), "You have left the queue for " + mode);
            }
        }
    }

    public static void leaveAllQueues(UUID uuid) {
        queueContainerMap.keySet().forEach(mode -> leaveQueue(uuid, mode));
    }

}
