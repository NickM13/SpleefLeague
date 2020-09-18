package com.spleefleague.coreapi.player;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.database.variable.DBVariable;
import com.spleefleague.coreapi.player.statistics.Ratings;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class PlayerRatings extends DBVariable<Document> {

    protected final Map<String, Ratings> modeRatingsMap = new HashMap<>();
    protected RatedPlayer owner = null;

    public PlayerRatings() {

    }

    public void setOwner(RatedPlayer owner) {
        this.owner = owner;
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

    public boolean isRanked(String mode, int season) {
        return modeRatingsMap.containsKey(mode)
                && modeRatingsMap.get(mode).isRanked(season);
    }

    public int getElo(String mode, int season) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }
        return modeRatingsMap.get(mode).get(season).getElo();
    }

    public String getDisplayElo(String mode, int season) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }
        return modeRatingsMap.get(mode).get(season).getDisplayElo();
    }

    public String getDisplayDivision(String mode, int season) {
        return modeRatingsMap.get(mode).get(season).getDivision().getDisplayName();
    }

    public int getWins(String mode, int season) {
        return modeRatingsMap.get(mode).get(season).getWins();
    }

    public int getLosses(String mode, int season) {
        return modeRatingsMap.get(mode).get(season).getLosses();
    }

    public String getWinPercent(String mode, int season) {
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

    public void setRating(String mode, int season, int elo) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings());
        }
        if (modeRatingsMap.get(mode).get(season).setElo(elo)) {
            addWin(mode, season);
        } else {
            addLoss(mode, season);
        }
    }

    public boolean addRating(String mode, int season, int change) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings());
        }
        if (change > 0) {
            addWin(mode, season);
        } else {
            addLoss(mode, season);
        }
        return modeRatingsMap.get(mode).get(season).addElo(owner, change);
    }

    public void addWin(String mode, int season) {
        modeRatingsMap.get(mode).get(season).addWin();
    }

    public void addLoss(String mode, int season) {
        modeRatingsMap.get(mode).get(season).addLoss();
    }

}
