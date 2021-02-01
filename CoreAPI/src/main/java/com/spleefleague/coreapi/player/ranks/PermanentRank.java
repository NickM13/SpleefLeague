package com.spleefleague.coreapi.player.ranks;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.player.ranks.Rank;

/**
 * @author NickM
 * @since 4/14/2020
 */
public abstract class PermanentRank <R extends Rank> extends DBEntity {

    @DBField protected String rankName = "";
    protected R rank = null;

    public PermanentRank() {
        rankName = "DEFAULT";
        afterLoad();
    }

    @Override
    public abstract void afterLoad();

    public void setRank(R rank) {
        this.rank = rank;
        this.rankName = rank.getIdentifier();
    }

    public R getRank() {
        return rank;
    }

}
