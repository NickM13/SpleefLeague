package com.spleefleague.coreapi.utils.packet.bungee.refresh;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.RatedPlayerInfo;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeRefreshScore extends PacketBungee {

    public String mode;
    public String season;
    public List<RatedPlayerInfo> players;

    public PacketBungeeRefreshScore() { }

    public PacketBungeeRefreshScore(String mode, String season, List<RatedPlayerInfo> players) {
        this.mode = mode;
        this.season = season;
        this.players = players;
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.REFRESH_SCORE;
    }

}
