package com.spleefleague.proxycore.player.ratings;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.player.statistics.Ratings;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.leaderboard.LeaderboardManager;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class ProxyPlayerRatings extends PlayerRatings {

    protected final Map<String, Ratings> modeRatingsMap;
    protected final ProxyCorePlayer owner;

    public ProxyPlayerRatings(ProxyCorePlayer owner) {
        this.modeRatingsMap = new HashMap<>();
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

    /**
     * Add elo to a season's rating, adding a win if the amount is positive and loss if negative
     *
     * @param mode Mode
     * @param season Season
     * @param amt Amount
     */
    public void addElo(String mode, int season, int amt) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }
        modeRatingsMap.get(mode).get(season).addElo(amt);
        ProxyCore.getInstance().getLeaderboards().get(mode).getActive().setPlayerScore(owner.getUniqueId(),
                modeRatingsMap.get(mode).get(season).getElo());
    }

    public boolean checkDecay(String mode, int season) {
        Ratings ratings = modeRatingsMap.get(mode);
        if (ratings != null && ratings.isRanked(season)) {
            return ratings.get(season).checkDecay();
        }
        return false;
    }

}
