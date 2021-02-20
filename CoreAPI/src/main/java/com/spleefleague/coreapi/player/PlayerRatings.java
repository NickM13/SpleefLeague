package com.spleefleague.coreapi.player;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.database.variable.DBVariable;
import com.spleefleague.coreapi.player.statistics.Rating;
import com.spleefleague.coreapi.player.statistics.Ratings;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public abstract class PlayerRatings extends DBVariable<Document> {

    protected final Map<String, Ratings> modeRatingsMap = new HashMap<>();

    protected PlayerRatings() {

    }

    @Override
    public void load(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            Ratings ratings = new Ratings();
            ratings.load((Document) entry.getValue());
            modeRatingsMap.put(entry.getKey(), ratings);
        }
    }

    @Override
    public Document save() {
        Document doc = new Document();
        for (Map.Entry<String, Ratings> ratingsEntry : modeRatingsMap.entrySet()) {
            doc.append(ratingsEntry.getKey(), ratingsEntry.getValue().toDocument());
        }
        return doc;
    }

    protected boolean isRanked(String mode, String season) {
        return modeRatingsMap.containsKey(mode)
                && modeRatingsMap.get(mode).isRanked(season);
    }

    protected int getElo(String mode, String season) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }
        return modeRatingsMap.get(mode).get(season).getElo();
    }

    protected String getDisplayElo(String mode, String season) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }
        return modeRatingsMap.get(mode).get(season).getDisplayElo();
    }

    protected String getDisplayDivision(String mode, String season) {
        return modeRatingsMap.get(mode).get(season).getDivision().getDisplayName();
    }

    protected int getWins(String mode, String season) {
        return modeRatingsMap.get(mode).get(season).getWins();
    }

    protected int getLosses(String mode, String season) {
        return modeRatingsMap.get(mode).get(season).getLosses();
    }

    protected int getGamesPlayed(String mode, String season) {
        Rating rating = modeRatingsMap.get(mode).get(season);
        if (rating != null) {
            return rating.getWins() + rating.getLosses();
        }
        return 0;
    }

    protected String getWinPercent(String mode, String season) {
        float percent = Math.round((float) modeRatingsMap.get(mode).get(season).getWins()
                / (modeRatingsMap.get(mode).get(season).getWins()
                + modeRatingsMap.get(mode).get(season).getLosses()) * 1000.f) / 10.f;
        if (percent >= 0.6) {
            return ChatColor.GREEN + "" + percent + "%";
        } else if (percent >= 0.4) {
            return ChatColor.YELLOW + "" + percent + "%";
        } else {
            return ChatColor.RED + "" + percent + "%";
        }
    }

    protected void setRating(String mode, String season, int elo) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings());
        }
        if (modeRatingsMap.get(mode).get(season).setElo(elo)) {
            addWin(mode, season);
        } else {
            addLoss(mode, season);
        }
    }

    protected int addRating(String mode, String season, int change) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings());
        }
        if (change > 0) {
            addWin(mode, season);
        } else {
            addLoss(mode, season);
        }
        return modeRatingsMap.get(mode).get(season).addElo(change);
    }

    protected void addWin(String mode, String season) {
        modeRatingsMap.get(mode).get(season).addWin();
    }

    protected void addLoss(String mode, String season) {
        modeRatingsMap.get(mode).get(season).addLoss();
    }

}
