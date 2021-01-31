package com.spleefleague.proxycore.player;

import com.spleefleague.coreapi.player.PlayerStatistics;

/**
 * @author NickM13
 */
public class ProxyPlayerStatistics extends PlayerStatistics {

    private ProxyCorePlayer owner = null;

    public ProxyPlayerStatistics() {
        super();
    }

    public void setOwner(ProxyCorePlayer owner) {
        this.owner = owner;
    }

}
