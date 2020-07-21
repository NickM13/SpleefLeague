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
        queueTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (QueueContainer queue : queueContainerMap.values()) {

            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public static void close() {
        queueTask.cancel();
    }

    public static void joinQueue(UUID uuid, String mode, String param) {
        if (!queueContainerMap.containsKey(mode)) {
            queueContainerMap.put(mode, new QueueContainer(mode));
        }
        if (queueContainerMap.get(mode).join(ProxyCore.getInstance().getPlayers().get(uuid), param)) {
            // You have joined the queue for (mode)
        }
    }

    public static void leaveQueue(UUID uuid, String mode) {
        if (queueContainerMap.containsKey(mode)) {
            if (queueContainerMap.get(mode).leave(ProxyCore.getInstance().getPlayers().getOffline(uuid))) {
                // You have successfully left the queue for (mode)
            }
        }
    }

    public static void leaveAllQueues(UUID uuid) {
        queueContainerMap.keySet().forEach(mode -> leaveQueue(uuid, mode));
    }

}
