package com.spleefleague.proxycore.game.queue;

import com.spleefleague.proxycore.player.ProxyCorePlayer;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public class QueuePlayer {

    ProxyCorePlayer pcp;
    String query;
    long joinTime;

    public QueuePlayer(ProxyCorePlayer pcp, String query) {
        if (query == null || query.equals("")) query = "*";
        this.pcp = pcp;
        this.query = query;
        this.joinTime = System.currentTimeMillis();
    }

    public boolean equals(ProxyCorePlayer pcp) {
        return this.pcp.equals(pcp);
    }

    public long getJoinTime() {
        return joinTime;
    }

}
