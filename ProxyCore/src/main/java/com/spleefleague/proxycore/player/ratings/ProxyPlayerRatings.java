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

    protected final ProxyCorePlayer owner;

    public ProxyPlayerRatings(ProxyCorePlayer owner) {
        super();
        this.owner = owner;
    }

    /**
     * Add elo to a season's rating, adding a win if the amount is positive and loss if negative
     *
     * @param mode Mode
     * @param season Season
     * @param amt Amount
     */
    @Override
    public boolean addRating(String mode, int season, int amt) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }

        boolean divChange = modeRatingsMap.get(mode).get(season).addElo(amt);

        ProxyCore.getInstance().getLeaderboards().get(mode).getActive().setPlayerScore(owner.getUniqueId(),
                modeRatingsMap.get(mode).get(season).getElo());

        return divChange;
    }

    public boolean checkDecay(String mode, int season) {
        Ratings ratings = modeRatingsMap.get(mode);
        if (ratings != null && ratings.isRanked(season)) {
            return ratings.get(season).checkDecay();
        }
        return false;
    }

}
