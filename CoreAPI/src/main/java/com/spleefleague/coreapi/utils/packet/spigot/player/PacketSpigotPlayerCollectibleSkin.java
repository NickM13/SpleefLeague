package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.CollectibleAction;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import java.util.UUID;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class PacketSpigotPlayerCollectibleSkin extends PacketSpigot {

    public UUID uuid;
    public String parent;
    public String identifier;
    public String skin;
    public CollectibleAction action;
    public String affix;

    public PacketSpigotPlayerCollectibleSkin() { }

    public PacketSpigotPlayerCollectibleSkin(UUID uuid, String parent, String identifier, String skin, CollectibleAction action, String affix) {
        this.uuid = uuid;
        this.parent = parent;
        this.identifier = identifier;
        this.skin = skin;
        this.action = action;
        this.affix = affix;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_COLLECTIBLE_SKIN;
    }

}
