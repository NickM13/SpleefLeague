package com.spleefleague.proxycore.player.ranks;

import com.spleefleague.coreapi.player.ranks.TempRank;
import com.spleefleague.proxycore.ProxyCore;

/**
 * @author NickM13
 */
public class ProxyTempRank extends TempRank<ProxyRank> {

    public ProxyTempRank() {
        super();
    }

    public ProxyTempRank(ProxyRank rank, long duration) {
        super(rank, duration);
    }

    @Override
    public void afterLoad() {
        this.rank = ProxyCore.getInstance().getRankManager().getRank(this.rankName);
    }

}
