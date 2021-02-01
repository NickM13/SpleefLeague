package com.spleefleague.coreapi.utils.packet.bungee.chat;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatTell;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeChatTell extends PacketBungee {

    public UUID sender;
    public UUID target;
    public String message;

    public PacketBungeeChatTell() { }

    public PacketBungeeChatTell(UUID sender, UUID target, String message) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    public PacketBungeeChatTell(PacketSpigotChatTell packet) {
        this.sender = packet.sender;
        this.target = packet.target;
        this.message = packet.message;
    }

    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.CHAT_TELL;
    }

}
