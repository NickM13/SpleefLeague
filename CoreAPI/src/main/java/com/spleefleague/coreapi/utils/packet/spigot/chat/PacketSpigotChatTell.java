package com.spleefleague.coreapi.utils.packet.spigot.chat;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotChatTell extends PacketSpigot {

    public UUID sender;
    public UUID target;
    public String message;

    public PacketSpigotChatTell() { }

    public PacketSpigotChatTell(UUID sender, UUID target, String message) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.CHAT_TELL;
    }

}
