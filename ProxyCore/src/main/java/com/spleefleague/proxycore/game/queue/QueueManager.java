package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshQueue;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.utils.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
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

    private final Map<String, QueueContainerDynamic> queueContainerDynamicMap = new HashMap<>();
    private final Map<String, QueueContainerTeam> queueContainerTeamMap = new HashMap<>();
    private final Map<String, QueueContainerSolo> queueContainerSoloMap = new HashMap<>();
    private final Map<String, QueueContainerVersus> queueContainerVersusMap = new HashMap<>();
    private ScheduledTask queueTask;

    public void init() {
        addQueueContainer("spleef:classic", "Classic Spleef", 2, 2, QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("spleef:power", "Power Spleef", 2, 2, QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("spleef:power_training", "Power Training", 1, 1, QueueContainer.TeamStyle.SOLO);
        addQueueContainer("spleef:team", "Team Spleef", 2, 2, QueueContainer.TeamStyle.TEAM);
        addQueueContainer("spleef:multi", "Multispleef", 3, 32, QueueContainer.TeamStyle.DYNAMIC);
        addQueueContainer("spleef:bonanza", "Bonanza Spleef", 1, 1, QueueContainer.TeamStyle.BONANZA);

        addQueueContainer("splegg:versus", "Splegg Versus", 2, 2, QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("splegg:multi", "Multisplegg", 3, 32, QueueContainer.TeamStyle.DYNAMIC);

        addQueueContainer("sj:classic", "SuperJump: Classic", 2, 2, QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("sj:shuffle", "SuperJump: Shuffle", 2, 2, QueueContainer.TeamStyle.VERSUS);
        addQueueContainer("sj:conquest", "SuperJump: Conquest", 1, 1, QueueContainer.TeamStyle.SOLO);
        addQueueContainer("sj:endless", "SuperJump: Endless", 1, 1, QueueContainer.TeamStyle.SOLO);

        queueTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (QueueContainerDynamic queue : queueContainerDynamicMap.values()) {
                queue.checkQueue();
            }
            for (QueueContainerTeam queue : queueContainerTeamMap.values()) {
                queue.checkQueue();
            }
            for (QueueContainerSolo queue : queueContainerSoloMap.values()) {
                queue.checkQueue();
            }
            for (QueueContainerVersus queue : queueContainerVersusMap.values()) {
                queue.checkQueue();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void close() {
        queueTask.cancel();
    }

    public void addQueueContainer(String identifier, String displayName, int reqTeams, int maxTeams, QueueContainer.TeamStyle teamStyle) {
        if (teamStyle == QueueContainer.TeamStyle.DYNAMIC) {
            queueContainerDynamicMap.put(identifier, new QueueContainerDynamic(identifier, displayName, reqTeams, maxTeams));
        } else if (teamStyle == QueueContainer.TeamStyle.TEAM) {
            queueContainerTeamMap.put(identifier, new QueueContainerTeam(identifier, displayName, reqTeams, maxTeams));
        } else if (teamStyle == QueueContainer.TeamStyle.VERSUS) {
            queueContainerVersusMap.put(identifier, new QueueContainerVersus(identifier, displayName, reqTeams, maxTeams));
        } else if (teamStyle == QueueContainer.TeamStyle.SOLO) {
            queueContainerSoloMap.put(identifier, new QueueContainerSolo(identifier, displayName, reqTeams, maxTeams));
        }
    }

    public Map<String, QueueContainerDynamic> getContainerDynamicMap() {
        return queueContainerDynamicMap;
    }

    public Map<String, QueueContainerTeam> getContainerTeamMap() {
        return queueContainerTeamMap;
    }

    public Map<String, QueueContainerVersus> getContainerVersusMap() {
        return queueContainerVersusMap;
    }

    public Map<String, QueueContainerSolo> getContainerSoloMap() {
        return queueContainerSoloMap;
    }

    public boolean forceStart(String mode, String query, List<QueueEntity> players) {
        if (queueContainerSoloMap.containsKey(mode)) {
            queueContainerSoloMap.get(mode).startMatch((QueuePlayer) players.get(0), query);
            return true;
        }
        if (queueContainerVersusMap.containsKey(mode)) {
            queueContainerVersusMap.get(mode).startMatch(players, query);
            return true;
        }
        return true;
    }

    public void joinSolo(UUID uuid, String mode, String query) {
        queueContainerSoloMap.get(mode).join(ProxyCore.getInstance().getPlayers().get(uuid), query);
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
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
        ProxyParty party = pcp.getParty();

        QueueContainerTeam queueContainerTeam = queueContainerTeamMap.get(mode);
        QueueContainerDynamic queueContainerDynamic = queueContainerDynamicMap.get(mode);

        if (party != null) {
            if (queueContainerTeam != null || queueContainerDynamic != null) {
                if (!party.getOwner().equals(uuid)) {
                    ProxyCore.getInstance().sendMessageError(pcp, new TextComponent("You are not the party leader"));
                } else {
                    int result;
                    String displayName;
                    if (queueContainerTeam != null) {
                        result = queueContainerTeam.join(party, query);
                        displayName = queueContainerTeam.getDisplayName();
                    } else {
                        result = queueContainerDynamic.join(party, query);
                        displayName = queueContainerDynamic.getDisplayName();
                    }
                    String arenaAffix = formatArenaQuery(mode, query);
                    TextComponent component = new TextComponent();
                    switch (result) {
                        case -1:
                            component.addExtra("Your party has left the queue for " + displayName);
                            break;
                        case 0:
                            component.addExtra("Your party has joined the queue for " + displayName + arenaAffix);
                            break;
                        case 1:
                            component.addExtra("Your party has rejoined the queue for " + displayName + arenaAffix);
                            break;
                    }
                    party.sendMessage(component);
                }
                return;
            }
        } else if (queueContainerTeam != null) {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(uuid),
                    "You aren't in a party!");
        }

        QueueContainerSolo queueContainerSolo = queueContainerSoloMap.get(mode);
        QueueContainerVersus queueContainerVersus = queueContainerVersusMap.get(mode);

        String arenaAffix = formatArenaQuery(mode, query);
        if (queueContainerSolo != null || queueContainerDynamic != null || queueContainerVersus != null) {
            int result;
            String displayName;
            if (queueContainerSolo != null) {
                result = queueContainerSolo.join(pcp, query);
                displayName = queueContainerSolo.getDisplayName();
            } else if (queueContainerDynamic != null) {
                result = queueContainerDynamic.join(pcp, query);
                displayName = queueContainerDynamic.getDisplayName();
            } else {
                result = queueContainerVersus.join(pcp, query);
                displayName = queueContainerVersus.getDisplayName();
            }

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
        } else {
            System.out.println("Queue doesn't exist");
        }
    }

    public boolean leaveAllQueues(UUID uuid) {
        boolean hasLeft = false;
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
        for (QueueContainerSolo queueContainer : queueContainerSoloMap.values()) {
            hasLeft |= queueContainer.leave(pcp) != null;
        }
        for (QueueContainerVersus queueContainer : queueContainerVersusMap.values()) {
            hasLeft |= queueContainer.leave(pcp) != null;
        }
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
        for (QueueContainerTeam queueContainer : queueContainerTeamMap.values()) {
            hasLeft |= queueContainer.leave(party) != null;
        }
        return hasLeft;
    }

}
