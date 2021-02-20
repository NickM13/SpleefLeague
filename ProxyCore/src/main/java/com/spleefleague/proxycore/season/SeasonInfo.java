package com.spleefleague.proxycore.season;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class SeasonInfo extends DBEntity {

    @DBField private Boolean preseason = true;
    @DBField private String name = "Preseason NULL";

    public SeasonInfo() {

    }

    public String getDisplayName() {
        return name;
    }

    public boolean isPreseason() {
        return preseason;
    }

}
