package com.spleefleague.coreapi.game.leaderboard;

import com.spleefleague.coreapi.database.annotation.DBField;

import java.util.Date;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class ArchivedLeaderboard extends Leaderboard {

    @DBField
    Long archiveTime;

    public ArchivedLeaderboard() {
        super();
        active = false;
    }

    @Override
    public String getDescription() {
        return "Start Date: " + dateFormat.format(new Date(createTime)) + "\n"
                + "End Date: " + dateFormat.format(new Date(archiveTime));
    }

    public ArchivedLeaderboard(ActiveLeaderboard leaderboard) {
        super(leaderboard.getName(), leaderboard.getSeason());
        leaderboard.getPlayerScoreMap().forEach(this::setPlayerScore);
        active = false;
        createTime = leaderboard.getCreateTime();
        archiveTime = System.currentTimeMillis();
    }

}
