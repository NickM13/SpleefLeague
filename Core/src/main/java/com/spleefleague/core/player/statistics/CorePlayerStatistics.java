package com.spleefleague.core.player.statistics;

import com.spleefleague.coreapi.player.PlayerStatistics;

/**
 * @author NickM13
 * @since 2/3/2021
 */
public class CorePlayerStatistics extends PlayerStatistics {

    @Override
    public long add(String parent, String statName, long value) {
        return super.add(parent, statName, value);
    }

    @Override
    public void set(String parent, String statName, long value) {
        super.set(parent, statName, value);
    }

    @Override
    public long setHigher(String parent, String statName, long value) {
        return super.setHigher(parent, statName, value);
    }

    @Override
    public long setHigher(String parent, String statName, String compare) {
        return super.setHigher(parent, statName, compare);
    }

}
