package com.spleefleague.proxycore.game.queue;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleStart;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.droplet.Droplet;
import com.spleefleague.proxycore.droplet.DropletType;
import com.spleefleague.proxycore.game.session.BattleSessionManager;
import com.spleefleague.proxycore.game.arena.Arena;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class QueueContainerDynamic extends QueueContainer {

    protected long delayStart = -1;

    protected final List<Integer> teamSizes = new ArrayList<>();
    protected final Map<Integer, Integer> sizeIndexHash = new HashMap<>();

    protected int minPartySize = 1;
    protected int maxPartySize = 1;
    protected boolean allowSolo = true;
    protected boolean allowPartySplit = true;
    protected boolean spectatable = true;
    protected boolean enabled = true;

    protected final TeamStyle teamStyle;

    public QueueContainerDynamic(String identifier, String displayName, TeamStyle teamStyle, int minTeams, int maxTeams, boolean spectatable, boolean enabled) {
        super(identifier, displayName, minTeams, maxTeams);
        this.teamStyle = teamStyle;
        this.spectatable = spectatable;
        this.enabled = enabled;
        initTeamSizes();
    }

    public QueueContainerDynamic(String identifier, String displayName, TeamStyle teamStyle, boolean spectatable, boolean enabled) {
        super(identifier, displayName, teamStyle.minSize, teamStyle.maxSize);
        this.teamStyle = teamStyle;
        this.spectatable = spectatable;
        this.enabled = enabled;
        initTeamSizes();
    }

    public void initTeamSizes() {
        teamSizes.clear();
        sizeIndexHash.clear();

        allowSolo = false;
        minPartySize = Integer.MAX_VALUE;
        maxPartySize = Integer.MIN_VALUE;

        Set<Integer> sizes = new HashSet<>();
        for (Arena arena : ProxyCore.getInstance().getArenaManager().getArenas(identifier)) {
            sizes.add(arena.getTeamSize());
        }
        for (Integer size : sizes) {
            teamSizes.add(size);
            sizeIndexHash.put(size, teamSizes.size() - 1);
            minPartySize = Math.min(minPartySize, size);
            maxPartySize = Math.max(maxPartySize, size);
        }
        //allowSolo = minPartySize == 1;
        allowSolo = true;
        allowPartySplit = maxPartySize == 1;
        if (teamStyle.allowPartySplit) {
            maxPartySize *= maxTeams;
        }
    }

    public boolean canQueueSolo() {
        return allowSolo;
    }

    public boolean isTeamQueue() {
        return maxPartySize > 2;
    }

    public int isValidParty(ProxyParty party) {
        if (allowPartySplit) {
            return party.getPlayerCount() <= maxPartySize ? 0 : 1;
        } else {
            return sizeIndexHash.containsKey(party.getPlayerCount()) ? 0 : 2;
        }
    }

    public boolean isEnabled() {
        return enabled;
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
        if (pcp.getParty() != null && contains(pcp.getParty())) {
            return 2;
        }
        QueuePlayer replaced = leave(pcp);
        QueuePlayer queuePlayer = new QueuePlayer(pcp, query, ProxyCore.getInstance().getPlayers().get(pcp.getUniqueId()).getRatings().getElo(identifier, SEASON));
        if (queuePlayer.query.equals("arena:*") && replaced != null) return -1;
        queuedEntities.add(queuePlayer);
        queueSize++;
        checkDelayedStart();
        return replaced != null ? 1 : 0;
    }

    public int join(ProxyParty party, String query) {
        QueueParty replaced = leave(party);
        QueueParty queueParty = new QueueParty(party, query, party.getAvgRating(identifier, SEASON));
        if (queueParty.query.equals("arena:*") && replaced != null) return -1;
        queuedEntities.add(queueParty);
        queueSize += queueParty.size;
        for (UUID uuid : party.getPlayerList()) {
            leave(ProxyCore.getInstance().getPlayers().getOffline(uuid));
        }
        checkDelayedStart();
        return replaced != null ? 1 : 0;
    }

    private void checkDelayedStart() {
        if (teamStyle.delayStart) {
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
        } else {
            delayStart = 0;
        }
    }

    /**
     * Removes a player from the queue, returning true if a player was successfully removed
     *
     * @param pcp Proxy Core Player
     * @return Player
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
     * @return Party
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

    public boolean contains(ProxyParty party) {
        Iterator<QueueEntity> qit = queuedEntities.iterator();
        while (qit.hasNext()) {
            QueueEntity qe = qit.next();
            if (qe instanceof QueueParty && ((QueueParty) qe).party == party) {
                qit.remove();
                queueSize -= qe.size;
                return true;
            }
        }
        return false;
    }

    protected QueuedChunk matchAfter(int start, QueuedChunk queueChunk) {
        if (queueChunk.filledTeams.size() >= maxTeams) return queueChunk;
        if (start >= queuedEntities.size()) {
            if (queueChunk.filledTeams.size() >= reqTeams) {
                return queueChunk;
            } else {
                return null;
            }
        }
        ListIterator<QueueEntity> pit = queuedEntities.listIterator(start);
        while (pit.hasNext()) {
            QueueEntity queueEntity = pit.next();
            if (!queueChunk.join(queueEntity, teamStyle.compareRating)) {
                continue;
            }
            QueuedChunk result;
            if ((result = matchAfter(pit.nextIndex(), new QueuedChunk(queueChunk))) != null) {
                return result;
            }
        }
        if (queueChunk.filledTeams.size() >= reqTeams) {
            return queueChunk;
        }
        return null;
    }

    public QueuedChunk getMatchedPlayers() {
        ListIterator<QueueEntity> pit = queuedEntities.listIterator();
        while (pit.hasNext()) {
            QueueEntity queueEntity = pit.next();
            int startIndex = -1;
            if (allowPartySplit) {
                if (sizeIndexHash.containsKey(1)) {
                    startIndex = sizeIndexHash.get(1);
                }
            } else {
                if (sizeIndexHash.containsKey(queueEntity.size)) {
                    startIndex = sizeIndexHash.get(queueEntity.size);
                } else {
                    for (int i = 0; i < teamSizes.size(); i++) {
                        if (queueEntity.size < teamSizes.get(i)) {
                            startIndex = i;
                            break;
                        }
                    }
                }
            }
            if (startIndex < 0) continue;
            for (int i = startIndex; i < teamSizes.size(); i++) {
                QueuedChunk result;
                if ((result = matchAfter(pit.nextIndex(), new QueuedChunk(queueEntity, teamSizes.get(i), teamStyle.maxSize))) != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public void checkQueue() {
        queuedEntities.forEach(QueueEntity::calcRatings);
        while (delayStart < System.currentTimeMillis() && delayStart >= 0) {
            if (getQueueSize() > 0) {
                System.out.println("Checking queue: " + getDisplayName() + " [" + getQueueSize() + "]");
            }
            QueuedChunk chunk = getMatchedPlayers();
            if (chunk == null || !startMatch(chunk)) break;
            delayStart = -1;
            checkDelayedStart();
        }
    }

    private boolean startMatch(QueuedChunk chunk) {
        List<UUID> players = new ArrayList<>();
        Set<UUID> used = new HashSet<>();
        boolean playerReused = false;
        for (QueueTeam team : chunk.filledTeams) {
            for (UUID uuid : team.players) {
                if (!used.add(uuid)) {
                    playerReused = true;
                    ProxyCore.getInstance().getQueueManager().leaveAllQueues(uuid);
                    ProxyParty party = ProxyCore.getInstance().getPartyManager().getParty(uuid);
                    if (party != null) {
                        ProxyCore.getInstance().getQueueManager().leaveAllQueues(party);
                        party.sendMessage(new TextComponent("Your party has been removed from all queues! Please try again!"));
                    }
                }
            }
            players.addAll(team.players);
        }
        if (playerReused) {
            return true;
        }
        return startMatch(players, chunk.teamSize, chunk.query.toString(), false);
    }

    public boolean startMatch(List<UUID> players, int teamSize, String query, boolean challenge) {
        Droplet droplet = ProxyCore.getInstance().getDropletManager().getAvailable(DropletType.MINIGAME);
        if (droplet == null) {
            ProxyCore.getInstance().getLogger().warning("There are no minigame servers available right now!");
            for (UUID uuid : players) {
                ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
                ProxyCore.getInstance().sendMessage(pcp, ChatColor.RED + "No available minigame servers!");
            }
            return false;
        }

        boolean failed = false;
        for (UUID uuid : players) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            if (pcp == null || pcp.isBattling()) {
                ProxyCore.getInstance().getQueueManager().leaveAllQueues(uuid);
                failed = true;
            }
        }
        if (failed) return true;

        UUID battleId = UUID.randomUUID();

        Set<ProxyParty> parties = new HashSet<>();

        for (UUID uuid : players) {
            ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(uuid);
            ProxyCore.getInstance().getQueueManager().leaveAllQueues(pcp.getUniqueId());
            pcp.setCurrentBattle(battleId);
            pcp.setBattling(true);
            playing.add(pcp.getUniqueId());
            pcp.connect(droplet);
            pcp.setLastQueueRequest(new PacketSpigotQueueJoin(pcp.getUniqueId(), identifier, query));
            ProxyParty party = pcp.getParty();
            if (party != null) {
                parties.add(pcp.getParty());
            }
        }

        for (ProxyParty party : parties) {
            ProxyCore.getInstance().getQueueManager().leaveAllQueues(party);
        }

        BattleSessionManager.createBattleSession(battleId, identifier, droplet, players, spectatable);

        ProxyCore.getInstance().getPacketManager().sendPacket(droplet.getInfo(), new PacketBungeeBattleStart(battleId, identifier, query, players, teamSize, challenge));

        return true;
    }

}
