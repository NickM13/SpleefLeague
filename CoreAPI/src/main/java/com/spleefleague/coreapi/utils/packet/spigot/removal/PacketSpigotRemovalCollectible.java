package com.spleefleague.coreapi.utils.packet.spigot.removal;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/26/2021
 */
public class PacketSpigotRemovalCollectible extends PacketSpigot {

    public UUID sender;
    public String parent;
    public String identifier;
    public String crate;

    public PacketSpigotRemovalCollectible() {

    }

    public PacketSpigotRemovalCollectible(UUID sender, String parent, String identifier, String crate) {
        this.sender = sender;
        this.parent = parent;
        this.identifier = identifier;
        this.crate = crate;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.REMOVAL_COLLECTIBLE;
    }

}
