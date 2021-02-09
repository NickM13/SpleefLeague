package com.spleefleague.coreapi.utils.packet.bungee.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class PacketBungeePlayerKick extends PacketBungee {

    public UUID target;
    public String reason;

    public PacketBungeePlayerKick() {

    }

    public PacketBungeePlayerKick(UUID target, String reason) {
        this.target = target;
        this.reason = reason;
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.PLAYER_KICK;
    }

}
