package com.spleefleague.coreapi.utils.packet.bungee.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class PacketBungeePlayerMute extends PacketBungee {

    public UUID target;

    public PacketBungeePlayerMute() {

    }

    public PacketBungeePlayerMute(UUID target) {
        this.target = target;
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.PLAYER_MUTE;
    }

}
