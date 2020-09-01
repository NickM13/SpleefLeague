package com.spleefleague.proxycore.game.queue;

import com.spleefleague.proxycore.ProxyCore;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.HashMap;
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
        //addQueueContainer("spleef:multi", "Multispleef", 2, 32, QueueContainer.TeamStyle.DYNAMIC, true);
        //addQueueContainer("spleef:bonanza", "Bonanza Spleef", 1, 1, QueueContainer.TeamStyle.BONANZA, true);

        addQueueContainer("splegg:classic", "Classic Splegg", 2, 2, QueueContainer.TeamStyle.VERSUS, false);

        queueTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (QueueContainer queue : queueContainerMap.values()) {
                queue.checkQueue();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static void addQueueContainer(String identifier, String displayName, int reqTeams, int maxTeams, QueueContainer.TeamStyle teamStyle, boolean joinOngoing) {
        queueContainerMap.put(identifier, new QueueContainer(identifier, displayName, reqTeams, maxTeams, teamStyle, joinOngoing));
    }

    public static void close() {
        queueTask.cancel();
    }

    public static void joinSolo(UUID uuid, String mode, String param) {
        if (!queueContainerMap.containsKey(mode)) {
            // Just a workaround to avoid setting up training modes
            addQueueContainer(mode, mode, 1, 1, QueueContainer.TeamStyle.SOLO, false);
        }
        queueContainerMap.get(mode).join(ProxyCore.getInstance().getPlayers().get(uuid), param);
    }

    public static void joinQueue(UUID uuid, String mode, String param) {
        if (!queueContainerMap.containsKey(mode)) {
            System.out.println("Queue doesn't exist");
            return;
            //queueContainerMap.put(mode, new QueueContainer(mode));
        }
        switch (queueContainerMap.get(mode).join(ProxyCore.getInstance().getPlayers().get(uuid), param)) {
            case -1:
                ProxyCore.sendMessage(ProxyCore.getInstance().getPlayers().get(uuid), "You have left the queue for " + mode);
                break;
            case 0:
                ProxyCore.sendMessage(ProxyCore.getInstance().getPlayers().get(uuid), "You have joined the queue for " + mode + " (" + param + ")");
                break;
            case 1:
                ProxyCore.sendMessage(ProxyCore.getInstance().getPlayers().get(uuid), "You have rejoined the queue for " + mode + " (" + param + ")");
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
