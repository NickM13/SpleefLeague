package com.spleefleague.core.player.rank;

import com.spleefleague.core.Core;
import com.spleefleague.coreapi.player.ranks.PermanentRank;
import com.spleefleague.coreapi.player.ranks.Rank;

/**
 * @author NickM13
 */
public class CorePermanentRank extends PermanentRank<CoreRank> {

    @Override
    public void afterLoad() {
        this.rank = Core.getInstance().getRankManager().getRank(rankName);
    }

}
