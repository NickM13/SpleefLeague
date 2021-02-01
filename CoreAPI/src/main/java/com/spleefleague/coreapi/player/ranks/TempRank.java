/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.player.ranks;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.player.ranks.Rank;

/**
 * @author NickM13
 */
public abstract class TempRank <R extends Rank> extends DBEntity {

    @DBField protected String rankName = "";
    @DBField protected Long expireTime = 0L;
    protected R rank = null;

    public TempRank() {
        afterLoad();
    }

    public TempRank(R rank, Long expireTime) {
        this.rank = rank;
        this.rankName = rank.getIdentifier();
        this.expireTime = expireTime;
        afterLoad();
    }

    @Override
    public abstract void afterLoad();

    public R getRank() {
        return rank;
    }

    public long getExpireTime() {
        return expireTime;
    }

}
