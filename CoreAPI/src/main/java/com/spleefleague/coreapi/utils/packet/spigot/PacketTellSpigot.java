package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketTellSpigot extends PacketSpigot {

    public UUID sender;
    public UUID target;
    public String message;

    public PacketTellSpigot() { }

    public PacketTellSpigot(UUID sender, UUID target, String message) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.TELL.ordinal();
    }

    @Override
    public void fromByteArray(ByteArrayDataInput input) {
        sender = UUID.fromString(input.readUTF());
        target = UUID.fromString(input.readUTF());
        message = input.readUTF();
    }

    @Override
    protected void toByteArray(ByteArrayDataOutput output) {
        output.writeUTF(sender.toString());
        output.writeUTF(target.toString());
        output.writeUTF(message);
    }

}
