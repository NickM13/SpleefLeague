package com.spleefleague.proxycore.player.ranks;

import com.spleefleague.coreapi.player.ranks.PermanentRank;
import com.spleefleague.coreapi.player.ranks.Rank;
import com.spleefleague.proxycore.ProxyCore;

/**
 * @author NickM13
 */
public class ProxyPermanentRank extends PermanentRank<ProxyRank> {

    @Override
    public void afterLoad() {
        this.rank = ProxyCore.getInstance().getRankManager().getRank(this.rankName);
    }

}
