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
public class PacketSpigotChatPlayer extends PacketSpigot {

    public UUID sender;
    public String channel;
    public String message;

    public PacketSpigotChatPlayer() { }

    public PacketSpigotChatPlayer(UUID sender, String message) {
        this.sender = sender;
        this.channel = "";
        this.message = message;
    }

    public PacketSpigotChatPlayer(UUID sender, String channel, String message) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT_PLAYER;
    }

}
