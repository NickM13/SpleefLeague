package com.spleefleague.coreapi.utils.packet.bungee.refresh;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.PartyContainerInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeRefreshParty extends PacketBungee {

    public PartyContainerInfo partyInfo;

    public PacketBungeeRefreshParty() { }

    public PacketBungeeRefreshParty(PartyContainerInfo partyInfo) {
        this.partyInfo = partyInfo;
    }

    public PacketBungeeRefreshParty(List<UUID> players) {
        this.partyInfo = new PartyContainerInfo(players);
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.REFRESH_PARTY;
    }

}
