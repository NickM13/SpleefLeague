package com.spleefleague.proxycore.season;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class SeasonInfo extends DBEntity {

    @DBField Boolean preseason = true;
    @DBField String name = "Preseason 1";
    @DBField Integer seasonId = 0;

    public SeasonInfo() {

    }

    public int getSeasonId() {
        return seasonId;
    }

}
