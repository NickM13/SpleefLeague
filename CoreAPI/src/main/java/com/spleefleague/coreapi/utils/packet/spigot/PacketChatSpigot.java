package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketChatSpigot extends PacketSpigot {

    public UUID sender;
    public String channel;
    public String message;

    public PacketChatSpigot() { }

    public PacketChatSpigot(UUID sender, String channel, String message) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.CHAT.ordinal();
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
