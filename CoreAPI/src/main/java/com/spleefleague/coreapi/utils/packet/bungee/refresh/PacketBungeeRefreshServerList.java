package com.spleefleague.coreapi.utils.packet.bungee.refresh;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeRefreshServerList extends PacketBungee {

    public List<String> lobbyServers, minigameServers;

    public PacketBungeeRefreshServerList() { }

    public PacketBungeeRefreshServerList(List<String> lobbyServers, List<String> minigameServers) {
        this.lobbyServers = lobbyServers;
        this.minigameServers = minigameServers;
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.REFRESH_SERVER_LIST;
    }

}
