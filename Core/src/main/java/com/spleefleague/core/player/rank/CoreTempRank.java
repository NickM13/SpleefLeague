package com.spleefleague.core.player.rank;

import com.spleefleague.core.Core;
import com.spleefleague.coreapi.player.ranks.TempRank;

/**
 * @author NickM13
 */
public class CoreTempRank extends TempRank<CoreRank> {

    public CoreTempRank() {

    }

    public CoreTempRank(CoreRank rank, Long time) {
        super(rank, time);
    }

    @Override
    public void afterLoad() {
        this.rank = Core.getInstance().getRankManager().getRank(this.rankName);
    }

}
