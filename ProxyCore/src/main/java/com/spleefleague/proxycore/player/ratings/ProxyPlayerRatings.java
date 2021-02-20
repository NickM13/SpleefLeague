package com.spleefleague.proxycore.player.ratings;

import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.player.statistics.Ratings;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;

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

    public void setRating(String mode, String season, int elo) {
        super.setRating(mode, season, elo);
    }

    /**
     * Add elo to a season's rating, adding a win if the amount is positive and loss if negative
     *
     * @param mode Mode
     * @param season Season
     * @param amt Amount
     */
    @Override
    public int addRating(String mode, String season, int amt) {
        if (!modeRatingsMap.containsKey(mode)) {
            modeRatingsMap.put(mode, new Ratings(mode));
        }

        int elo = modeRatingsMap.get(mode).get(season).addElo(amt);

        ProxyCore.getInstance().getLeaderboards().get(mode).setPlayerScore(owner.getUniqueId(), elo);

        return elo;
    }

    public boolean checkDecay(String mode, String season) {
        Ratings ratings = modeRatingsMap.get(mode);
        if (ratings != null && ratings.isRanked(season)) {
            return ratings.get(season).checkDecay();
        }
        return false;
    }

    public int getElo(String mode, String season) {
        return super.getElo(mode, season);
    }

}
