package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.proxycore.droplet.Droplet;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author NickM13
 * @since 2/19/2021
 */
public abstract class ProxyDBPlayer extends DBPlayer {

    private ProxiedPlayer player;
    private Droplet droplet = null;
    private ServerInfo currentServer = null;

    public void init(ProxiedPlayer player) {
        this.player = player;
        init();
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public void connect(ServerInfo server) {
        getPlayer().connect(server);
    }

    public void connect(Droplet droplet) {
        getPlayer().connect(droplet.getInfo());
    }

    public void setCurrentServer(ServerInfo currentServer) {
        this.currentServer = currentServer;
    }

    public void setCurrentDroplet(Droplet droplet) {
        this.droplet = droplet;
    }

    public ServerInfo getCurrentServer() {
        return currentServer;
    }

    public Droplet getCurrentDroplet() {
        return droplet;
    }

}
