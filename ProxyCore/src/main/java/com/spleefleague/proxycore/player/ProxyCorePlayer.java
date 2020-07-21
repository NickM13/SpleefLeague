package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.player.RatedPlayer;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.leaderboard.LeaderboardManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ProxyCorePlayer extends RatedPlayer {

    private ServerInfo currentServer = null;
    private ProxyParty party = null;

    public ProxyCorePlayer() {
        ratings = new ProxyPlayerRatings();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void initOffline() {
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

    public boolean canJoinBattle() {
        return true;
    }

    public void setParty(ProxyParty party) {
        this.party = party;
    }

    public ProxyParty getParty() {
        return party;
    }

}
