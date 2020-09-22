package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketChallengeSpigot extends PacketSpigot {

    public UUID sender;
    public UUID receiver;
    public String mode;
    public String query;

    public PacketChallengeSpigot() { }

    public PacketChallengeSpigot(UUID sender, UUID receiver, String mode, String query) {
        this.sender = sender;
        this.receiver = receiver;
        this.mode = mode;
        this.query = query;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.CHALLENGE.ordinal();
    }

}
