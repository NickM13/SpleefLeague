package com.spleefleague.coreapi.utils.packet.spigot.chat;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotChatGroup extends PacketSpigot {

    public UUID groupUid;
    public String message;
    public boolean url;

    public PacketSpigotChatGroup() { }

    public PacketSpigotChatGroup(UUID groupUid, String message, boolean url) {
        this.groupUid = groupUid;
        this.message = message;
        this.url = url;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT_GROUP;
    }

}
