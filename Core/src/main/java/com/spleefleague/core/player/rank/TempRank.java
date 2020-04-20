/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.rank;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;

/**
 * @author NickM13
 */
public class TempRank extends DBEntity {

    @DBField
    private String rankName = null;
    @DBField
    private Long expireTime = 0L;

    public TempRank() { }

    public TempRank(String rankName, Long expireTime) {
        this.rankName = rankName;
        this.expireTime = expireTime;
    }

    public Rank getRank() {
        return Rank.getRank(rankName);
    }

    public long getExpireTime() {
        return expireTime;
    }

}
