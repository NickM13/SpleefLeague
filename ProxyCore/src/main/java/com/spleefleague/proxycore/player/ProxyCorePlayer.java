package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.player.PlayerStatistics;
import com.spleefleague.coreapi.player.RatedPlayer;
import com.spleefleague.proxycore.ProxyCore;
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
    private boolean inBattle = false;

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

    public void setCurrentServer(ServerInfo currentServer) {
        this.currentServer = currentServer;
    }

    public ServerInfo getCurrentServer() {
        return currentServer;
    }

    public ProxiedPlayer getPlayer() {
        return ProxyCore.getInstance().getProxy().getPlayer(uuid);
    }

    public boolean isInBattle() {
        return inBattle;
    }

    public void setInBattle(boolean inBattle) {
        this.inBattle = inBattle;
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

}
