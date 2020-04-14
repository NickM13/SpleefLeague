/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player;

/**
 * @author NickM13
 */
public class TempRank {

    public Rank rank = null;
    public Long expireTime = 0L;

    public TempRank(String rank, Long expireTime) {
        this.rank = Rank.getRank(rank);
        this.expireTime = expireTime;
    }
        
}
