package com.spleefleague.core.player.ratings;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.utils.packet.shared.NumAction;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;
import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRating;

/**
 * @author NickM13
 * @since 2/19/2021
 */
public class CorePlayerRatings extends PlayerRatings {

    private final CorePlayer owner;

    public CorePlayerRatings(CorePlayer owner) {
        this.owner = owner;
    }

    public int getElo(String mode, String season) {
        return super.getElo(mode, season);
    }

    public int addRating(String mode, String season, int change) {
        int elo = super.addRating(mode, season, change);
        Core.getInstance().sendPacket(new PacketSpigotPlayerRating(mode, season, new RatedPlayerInfo(NumAction.CHANGE, owner.getUniqueId(), change)));
        return elo;
    }

    public void setRating(String mode, String season, int elo) {
        super.setRating(mode, season, elo);
        Core.getInstance().sendPacket(new PacketSpigotPlayerRating(mode, season, new RatedPlayerInfo(NumAction.SET, owner.getUniqueId(), elo)));
    }

    public String getDisplayElo(String mode, String season) {
        return super.getDisplayElo(mode, season);
    }

    public boolean isRanked(String mode, String season) {
        return super.isRanked(mode, season);
    }

    public int getGamesPlayed(String mode, String season) {
        return super.getGamesPlayed(mode, season);
    }

    public String getDisplayDivision(String mode, String season) {
        return super.getDisplayDivision(mode, season);
    }

    public int getWins(String mode, String season) {
        return super.getWins(mode, season);
    }

    public int getLosses(String mode, String season) {
        return super.getLosses(mode, season);
    }

    public String getWinPercent(String mode, String season) {
        return super.getWinPercent(mode, season);
    }

}
