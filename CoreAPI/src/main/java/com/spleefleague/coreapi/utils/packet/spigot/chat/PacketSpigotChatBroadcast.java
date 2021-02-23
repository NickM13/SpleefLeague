package com.spleefleague.coreapi.utils.packet.spigot.chat;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;

/**
 * @author NickM13
 * @since 2/22/2021
 */
public class PacketSpigotChatBroadcast extends PacketSpigot {

    public String message;

    public PacketSpigotChatBroadcast() {

    }

    public PacketSpigotChatBroadcast(String message) {
        this.message = message;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT_BROADCAST;
    }

}
