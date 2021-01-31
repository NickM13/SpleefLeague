package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.player.PlayerStatistics;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
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

    @DBField private PermRank permRank;
    @DBField private List<TempRank> tempRanks;

    @DBField private Boolean vanished;

    @DBField private CorePlayerPurse purse = new CorePlayerPurse();

    @DBField private final CorePlayerOptions options = new CorePlayerOptions();
    @DBField private final CorePlayerCollectibles collectibles = new CorePlayerCollectibles();
    @DBField private long lastOnline = -1;

    private final ProxyPlayerRatings proxyRatings = new ProxyPlayerRatings();
    @DBField private final ProxyPlayerStatistics statistics = new ProxyPlayerStatistics();

    public ProxyCorePlayer() {

    }

    @Override
    public void init() {
        proxyRatings.setOwner(this);
        statistics.setOwner(this);
    }

    @Override
    public void initOffline() {
        super.initOffline();
        proxyRatings.setOwner(this);
        statistics.setOwner(this);
    }

    @Override
    public void close() {

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

    public void setLastQueueRequest(PacketSpigotQueueJoin packet) {
        lastQueueRequest = packet;
    }

    public PacketSpigotQueueJoin getLastQueueRequest() {
        return lastQueueRequest;
    }

}
