package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.queue.SubQuery;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.arena.Arena;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.utils.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public class QueueManager {

    private final Map<String, QueueContainerDynamic> queueContainerDynamicMap = new HashMap<>();
    private ScheduledTask queueTask;

    public void init() {
        addQueueContainer("spleef:classic",         "Classic Spleef",       QueueContainer.TeamStyle.VERSUS, true);
        addQueueContainer("spleef:power",           "Power Spleef",         QueueContainer.TeamStyle.VERSUS, true);
        addQueueContainer("spleef:power_training",  "Power Training",       QueueContainer.TeamStyle.SOLO, false);
        addQueueContainer("spleef:team",            "Team Spleef",          QueueContainer.TeamStyle.VERSUS, true);
        addQueueContainer("spleef:power_team",      "Team Power Spleef",    QueueContainer.TeamStyle.VERSUS, true);
        addQueueContainer("spleef:multi",           "Multispleef",          QueueContainer.TeamStyle.DYNAMIC_12, true);
        addQueueContainer("spleef:bonanza",         "Bonanza Spleef",       QueueContainer.TeamStyle.BONANZA, false);

        addQueueContainer("splegg:versus",          "Splegg Versus",        QueueContainer.TeamStyle.VERSUS, true);
        addQueueContainer("splegg:multi",           "Multisplegg",          QueueContainer.TeamStyle.DYNAMIC_8, true);

        addQueueContainer("sj:classic",             "SuperJump: Classic",   QueueContainer.TeamStyle.VERSUS, true);
        addQueueContainer("sj:shuffle",             "SuperJump: Shuffle",   QueueContainer.TeamStyle.VERSUS, true);
        addQueueContainer("sj:conquest",            "SuperJump: Conquest",  QueueContainer.TeamStyle.SOLO, true);
        addQueueContainer("sj:endless",             "SuperJump: Endless",   QueueContainer.TeamStyle.SOLO, true);

        queueTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            for (QueueContainerDynamic queue : queueContainerDynamicMap.values()) {
                queue.checkQueue();
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    public void close() {
        queueTask.cancel();
    }

    public void addQueueContainer(String identifier, String displayName, QueueContainer.TeamStyle teamStyle, boolean spectatable) {
        queueContainerDynamicMap.put(identifier, new QueueContainerDynamic(identifier, displayName, teamStyle, spectatable, true));
    }

    public void addQueueContainer(String identifier, String displayName, QueueContainer.TeamStyle teamStyle, boolean spectatable, boolean enabled) {
        queueContainerDynamicMap.put(identifier, new QueueContainerDynamic(identifier, displayName, teamStyle, spectatable, enabled));
    }

    public QueueContainerDynamic getQueue(String identifier) {
        return queueContainerDynamicMap.get(identifier);
    }

    public void forceStart(String mode, String query, List<UUID> players) {
        if (queueContainerDynamicMap.containsKey(mode)) {
            queueContainerDynamicMap.get(mode).startMatch(players, players.size() / 2, query, true);
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

    public boolean isSizeValid(int size, String mode, String query) {
        for (SubQuery subQuery : SubQuery.splitQuery(query)) {
            if (subQuery.type.equalsIgnoreCase("arena")) {
                if (subQuery.hasStar) {
                    List<Arena> arenas = new ArrayList<>();
                    for (int i = 0; i < subQuery.values.size(); i++) {
                        arenas.add(ProxyCore.getInstance().getArenaManager().getArena(mode, subQuery.values.get(i)));
                    }
                    Set<Arena> blacklist = new HashSet<>(arenas);
                    for (Arena arena : ProxyCore.getInstance().getArenaManager().getArenas(mode)) {
                        if (!blacklist.contains(arena)) {
                            if (arena.getTeamSize() == size) {
                                return true;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < subQuery.values.size(); i++) {
                        if (ProxyCore.getInstance().getArenaManager().getArena(mode, subQuery.values.get(i)).getTeamSize() == size) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String formatArenaQuery(String mode, String query) {
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

        if (!queueContainerDynamic.isEnabled()) {
            ProxyCore.getInstance().sendMessageError(pcp, new TextComponent("Sorry, that queue is temporarily disabled"));
            return;
        }

        if (party != null) {
            if (!party.getOwner().equals(uuid)) {
                if (!queueContainerDynamic.canQueueSolo() || queueContainerDynamic.isTeamQueue()) {
                    ProxyCore.getInstance().sendMessageError(pcp, new TextComponent("You are not the party leader"));
                    return;
                }
            } else {
                if (!party.isQueueReady()) {
                    ProxyCore.getInstance().sendMessageError(pcp, new TextComponent("Someone from your party is currently unavailable"));
                    return;
                }
                switch (queueContainerDynamic.isValidParty(party)) {
                    case 0:
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
                        return;
                    case 1:
                        if (!queueContainerDynamic.canQueueSolo()) {
                            ProxyCore.getInstance().sendMessageError(pcp, new TextComponent("Your party is too big for that gamemode"));
                            return;
                        }
                        break;
                    case 2:
                        if (!queueContainerDynamic.canQueueSolo()) {
                            ProxyCore.getInstance().sendMessageError(pcp, new TextComponent("TODO: Party size does not match any arena team sizes"));
                            return;
                        }
                        break;
                }
            }
        } else if (!queueContainerDynamic.canQueueSolo()) {
            ProxyCore.getInstance().sendMessage(pcp,
                    "You aren't in a party!");
            return;
        }
        String arenaAffix = formatArenaQuery(mode, query);
        int result = queueContainerDynamic.join(pcp, query);
        String displayName = queueContainerDynamic.getDisplayName();

        if (pcp.isBattling()) {
            ProxyCore.getInstance().sendMessageError(pcp,
                    new TextComponent("You can't do that while ingame"));
            return;
        }
        switch (result) {
            case -1:
                ProxyCore.getInstance().sendMessage(pcp,
                        "You have left the queue for " +
                                displayName);
                break;
            case 0:
                ProxyCore.getInstance().sendMessage(pcp,
                        "You have joined the queue for " +
                                displayName +
                                arenaAffix);
                break;
            case 1:
                ProxyCore.getInstance().sendMessage(pcp,
                        "You have rejoined the queue for " +
                                displayName +
                                arenaAffix);
                break;
            case 2:
                ProxyCore.getInstance().sendMessage(pcp,
                        "Your party is already in queue for " + displayName);
                break;
        }
    }

    public boolean leaveAllQueues(UUID uuid) {
        boolean hasLeft = false;
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(uuid);
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
