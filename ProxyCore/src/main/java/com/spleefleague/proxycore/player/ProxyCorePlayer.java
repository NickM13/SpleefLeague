package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.player.PlayerStatistics;
import com.spleefleague.coreapi.player.collectibles.PlayerCollectibles;
import com.spleefleague.coreapi.player.options.PlayerOptions;
import com.spleefleague.coreapi.player.purse.PlayerPurse;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import com.spleefleague.proxycore.party.ProxyParty;
import com.spleefleague.proxycore.player.friends.ProxyFriendsList;
import com.spleefleague.proxycore.player.ranks.ProxyPermanentRank;
import com.spleefleague.proxycore.player.ranks.ProxyRank;
import com.spleefleague.proxycore.player.ranks.ProxyTempRank;
import com.spleefleague.proxycore.player.ratings.ProxyPlayerRatings;
import com.spleefleague.proxycore.player.statistics.ProxyPlayerStatistics;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ProxyCorePlayer extends DBPlayer {

    private ServerInfo currentServer = null;
    private ProxyParty party = null;
    private boolean battling = false;
    private QueueContainer battleContainer = null;
    private PacketSpigotQueueJoin lastQueueRequest = null;

    @DBField private String nickname = null;
    @DBField private UUID disguise = null;

    @DBField private ProxyPermanentRank permRank;
    @DBField private List<ProxyTempRank> tempRanks;

    @DBField private Boolean vanished;

    @DBField private final PlayerPurse purse = new PlayerPurse();
    @DBField private final PlayerOptions options = new PlayerOptions();
    @DBField private final PlayerCollectibles collectibles = new PlayerCollectibles();

    @DBField private long lastOnline = -1;

    @DBField private final ProxyPlayerRatings proxyRatings = new ProxyPlayerRatings(this);
    @DBField private final ProxyPlayerStatistics statistics = new ProxyPlayerStatistics(this);

    @DBField private final ProxyFriendsList friends = new ProxyFriendsList(this);

    private boolean online = false;

    public ProxyCorePlayer() {

    }

    @Override
    public void init() {
        online = true;
    }

    @Override
    public void initOffline() {
        super.initOffline();
        online = false;
    }

    @Override
    public void close() {

    }

    /**
     * @return Name of player as TextComponent to allow for quick /tell
     */
    public TextComponent getChatName() {
        TextComponent text = new TextComponent(getRank().getColor() + nickname);

        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to send a message").create()));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + nickname));

        return text;
    }

    /**
     * Returns highest TempRank, if no TempRanks available then Permanent Rank
     * This allows us to set Admins to Default for a set time
     *
     * @return Highest TempRank, if no TempRanks available then Permanent Rank
     */
    public ProxyRank getRank() {
        if (tempRanks.size() > 0) {
            ProxyTempRank highestRank = null;
            for (ProxyTempRank ctr : tempRanks) {
                if (highestRank == null ||
                        ctr.getRank().getLadder() > highestRank.getRank().getLadder()) {
                    highestRank = ctr;
                }
            }
            if (highestRank != null) {
                return highestRank.getRank();
            }
        }
        return permRank.getRank();
    }

    public void setLastOnline() {
        lastOnline = System.currentTimeMillis();
    }

    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public void transfer(ServerInfo server) {
        getPlayer().connect(server);
        currentServer = server;
    }

    public void setCurrentServer(ServerInfo currentServer) {
        this.currentServer = currentServer;
    }

    public ServerInfo getCurrentServer() {
        return currentServer;
    }

    public ProxiedPlayer getPlayer() {
        return ProxyCore.getInstance().getProxy().getPlayer(uuid);
    }

    public boolean isBattling() {
        return battling;
    }

    public void setBattling(boolean state) {
        battling = state;
    }

    public QueueContainer getBattleContainer() {
        return battleContainer;
    }

    public void setBattleContainer(QueueContainer battleContainer) {
        if (this.battleContainer != null) {
            this.battleContainer.removePlayer(getUniqueId());
        }
        this.battleContainer = battleContainer;
        if (battleContainer == null) {
            this.battling = false;
        }
    }

    public void setParty(ProxyParty party) {
        this.party = party;
    }

    public ProxyParty getParty() {
        return party;
    }

    public ProxyPlayerRatings getProxyRatings() {
        return proxyRatings;
    }

    public ProxyFriendsList getFriends() {
        return friends;
    }

    public void setLastQueueRequest(PacketSpigotQueueJoin packet) {
        lastQueueRequest = packet;
    }

    public PacketSpigotQueueJoin getLastQueueRequest() {
        return lastQueueRequest;
    }

}
