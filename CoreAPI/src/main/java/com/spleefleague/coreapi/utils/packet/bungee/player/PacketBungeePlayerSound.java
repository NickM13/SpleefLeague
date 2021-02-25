package com.spleefleague.coreapi.utils.packet.bungee.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class PacketBungeePlayerSound extends PacketBungee {

    public UUID target;
    public String sound;

    public PacketBungeePlayerSound() {

    }

    public PacketBungeePlayerSound(UUID target, String sound) {
        this.target = target;
        this.sound = sound;
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.PLAYER_SOUND;
    }

}
