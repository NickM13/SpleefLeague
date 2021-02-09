package com.spleefleague.proxycore.game.queue;

import com.spleefleague.proxycore.player.ProxyCorePlayer;

/**
 * @author NickM13
 * @since 6/23/2020
 */
public class QueuePlayer extends QueueEntity {

    ProxyCorePlayer pcp;

    public QueuePlayer(ProxyCorePlayer pcp, String query, int rating) {
        super(query, rating);
        this.pcp = pcp;
        this.size = 1;
    }

    public boolean equals(ProxyCorePlayer pcp) {
        return this.pcp.equals(pcp);
    }

}
