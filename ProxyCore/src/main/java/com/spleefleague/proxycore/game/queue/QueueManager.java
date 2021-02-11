package com.spleefleague.proxycore.game.queue;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.utils.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bson.Document;

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

    private final Map<String, QueueContainerDynamic> queueContainerDynamicMap = new HashMap<>();
    private ScheduledTask queueTask;
    private MongoCollection<Document> queueStatsCol;

    public void init() {
        addQueueContainer("spleef:classic", "Classic Spleef", QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("spleef:power", "Power Spleef", QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("spleef:power_training", "Power Training", QueueContainer.TeamStyle.SOLO);
        addQueueContainer("spleef:team", "Team Spleef", QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("spleef:multi", "Multispleef", QueueContainer.TeamStyle.DYNAMIC);
        addQueueContainer("spleef:bonanza", "Bonanza Spleef", QueueContainer.TeamStyle.BONANZA);

        addQueueContainer("splegg:versus", "Splegg Versus", QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("splegg:multi", "Multisplegg", QueueContainer.TeamStyle.DYNAMIC);

        addQueueContainer("sj:classic", "SuperJump: Classic", QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("sj:shuffle", "SuperJump: Shuffle", QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("sj:conquest", "SuperJump: Conquest", QueueContainer.TeamStyle.SOLO);
        addQueueContainer("sj:endless", "SuperJump: Endless", QueueContainer.TeamStyle.SOLO);

        queueStatsCol = ProxyCore.getInstance().getDatabase().getCollection("QueueStats");

        queueTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (QueueContainerDynamic queue : queueContainerDynamicMap.values()) {
                queue.checkQueue();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void close() {
        queueTask.cancel();
    }

    public void addQueueContainer(String identifier, String displayName, QueueContainer.TeamStyle teamStyle) {
        queueContainerDynamicMap.put(identifier, new QueueContainerDynamic(identifier, displayName, teamStyle));
    }

    public void forceStart(String mode, String query, List<UUID> players) {
        if (queueContainerDynamicMap.containsKey(mode)) {
            queueContainerDynamicMap.get(mode).startMatch(players, query);
        }
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
                    stringBuilder.append(ChatColor.GREEN).append(ProxyCore.getInstance().getArenaManager().getArena(mode, subQuery.values.get(i)).getName());
                }
                return stringBuilder.toString() + ChatColor.GRAY;
            }
        }
        return "";
    }

    public void joinQueue(UUID uuid, String mode, String query) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
        ProxyParty party = pcp.getParty();

        QueueContainerDynamic queueContainerDynamic = queueContainerDynamicMap.get(mode);
        if (queueContainerDynamic == null) {
            System.out.println("Queue doesn't exist!");
            return;
        }

        if (party != null) {
            if (queueContainerDynamic.isValidParty(party)) {
                if (!party.getOwner().equals(uuid)) {
                    ProxyCore.getInstance().sendMessageError(pcp, new TextComponent("You are not the party leader"));
                } else {
                    int result = queueContainerDynamic.join(party, query);
                    String displayName = queueContainerDynamic.getDisplayName();
                    String arenaAffix = formatArenaQuery(mode, query);

                    switch (result) {
                        case -1:
                            party.sendMessage(new TextComponent("Your party has left the queue for "
                                    + displayName));
                            break;
                        case 0:
                            party.sendMessage(new TextComponent("Your party has joined the queue for "
                                    + displayName + arenaAffix));
                            break;
                        case 1:
                            party.sendMessage(new TextComponent("Your party has rejoined the queue for "
                                    + displayName + arenaAffix));
                            break;
                    }
                }
            }
        } else if (!queueContainerDynamic.canQueueSolo()) {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                    "You aren't in a party!");
        } else {
            String arenaAffix = formatArenaQuery(mode, query);
            int result = queueContainerDynamic.join(pcp, query);
            String displayName = queueContainerDynamic.getDisplayName();

            switch (result) {
                case -1:
                    ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                            "You have left the queue for " +
                                    displayName);
                    break;
                case 0:
                    ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                            "You have joined the queue for " +
                                    displayName +
                                    arenaAffix);
                    break;
                case 1:
                    ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                            "You have rejoined the queue for " +
                                    displayName +
                                    arenaAffix);
                    break;
            }
        }
    }

    public boolean leaveAllQueues(UUID uuid) {
        boolean hasLeft = false;
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
        for (QueueContainerDynamic queueContainer : queueContainerDynamicMap.values()) {
            hasLeft |= queueContainer.leave(pcp) != null;
        }
        return hasLeft;
    }

    public boolean leaveAllQueues(ProxyParty party) {
        boolean hasLeft = false;
        for (QueueContainerDynamic queueContainer : queueContainerDynamicMap.values()) {
            hasLeft |= queueContainer.leave(party) != null;
        }
        return hasLeft;
    }

}
