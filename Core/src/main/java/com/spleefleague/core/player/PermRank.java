package com.spleefleague.core.player;

import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.database.variable.DBEntity;

/**
 * @author NickM
 * @since 4/14/2020
 */
public class PermRank extends DBEntity {

    private Rank rank;

    @DBLoad(fieldName="rankName")
    private void loadRank(String rankName) {
        rank = Rank.getRank(rankName);
    }

    @DBSave(fieldName="rankName")
    private String saveRank() {
        return rank.getName();
    }

    public PermRank() {
        rank = Rank.getDefaultRank();
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Rank getRank() {
        return rank;
    }

}
