package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.player.RatedPlayer;
import com.spleefleague.coreapi.utils.packet.spigot.PacketQueueJoin;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.queue.QueueContainer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ProxyCorePlayer extends RatedPlayer {

    private ServerInfo currentServer = null;
    private ProxyParty party = null;
    private ProxyPlayerRatings proxyRatings = new ProxyPlayerRatings();
    private boolean battling = false;
    private QueueContainer battleContainer = null;
    private PacketQueueJoin lastQueueRequest = null;

    public ProxyCorePlayer() {

    }

    @Override
    public void init() {
        proxyRatings.setOwner(this);
        super.init();
    }

    @Override
    public void initOffline() {
        proxyRatings.setOwner(this);
        super.initOffline();
    }

    @Override
    public void close() {

    }

    public void transfer(ServerInfo server) {
        getPlayer().connect(server);
        this.currentServer = server;
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

    public void setLastQueueRequest(PacketQueueJoin packet) {
        lastQueueRequest = packet;
    }

    public PacketQueueJoin getLastQueueRequest() {
        return lastQueueRequest;
    }

}
