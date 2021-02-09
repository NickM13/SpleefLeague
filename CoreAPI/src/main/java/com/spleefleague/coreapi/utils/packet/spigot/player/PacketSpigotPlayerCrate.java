package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class PacketSpigotPlayerCrate extends PacketSpigot {

    public UUID uuid;
    public String crateName;
    public int amount;
    public boolean opened;

    public PacketSpigotPlayerCrate() { }

    public PacketSpigotPlayerCrate(UUID uuid, String crateName, int amount, boolean opened) {
        this.uuid = uuid;
        this.crateName = crateName;
        this.amount = amount;
        this.opened = opened;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_CRATE;
    }

}
