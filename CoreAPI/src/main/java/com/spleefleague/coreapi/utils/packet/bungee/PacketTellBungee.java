package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketTellSpigot;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketTellBungee extends PacketBungee {

    public UUID sender;
    public UUID target;
    public String message;

    public PacketTellBungee() { }

    public PacketTellBungee(UUID sender, UUID target, String message) {
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    public PacketTellBungee(PacketTellSpigot packet) {
        this.sender = packet.sender;
        this.target = packet.target;
        this.message = packet.message;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.TELL.ordinal();
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
