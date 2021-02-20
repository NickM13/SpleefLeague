package com.spleefleague.proxycore.player.statistics;

import com.spleefleague.coreapi.player.PlayerStatistics;
import com.spleefleague.proxycore.player.ProxyCorePlayer;

/**
 * @author NickM13
 */
public class ProxyPlayerStatistics extends PlayerStatistics {

    private ProxyCorePlayer owner = null;

    public ProxyPlayerStatistics(ProxyCorePlayer owner) {
        super();
        this.owner = owner;
    }

}
