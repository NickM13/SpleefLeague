package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketChatSpigot;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketChatBungee extends PacketBungee {

    public UUID sender;
    public String channel;
    public String message;

    public PacketChatBungee() { }

    public PacketChatBungee(UUID sender, String channel, String message) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
    }

    public PacketChatBungee(PacketChatSpigot packet) {
        this.sender = packet.sender;
        this.channel = packet.channel;
        this.message = packet.message;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.CHAT.ordinal();
    }

    @Override
    public void fromByteArray(ByteArrayDataInput input) {
        String uuidStr = input.readUTF();
        sender = uuidStr.isEmpty() ? null : UUID.fromString(uuidStr);
        channel = input.readUTF();
        message = input.readUTF();
    }

    @Override
    protected void toByteArray(ByteArrayDataOutput output) {
        output.writeUTF(sender == null ? "" : sender.toString());
        output.writeUTF(channel);
        output.writeUTF(message);
    }

}
