package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketChallengeSpigot;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketChallengeBungee extends PacketBungee {

    public UUID sender;
    public UUID receiver;
    public String mode;
    public String query;

    public PacketChallengeBungee() { }

    public PacketChallengeBungee(PacketChallengeSpigot packet) {
        this.sender = packet.sender;
        this.receiver = packet.receiver;
        this.mode = packet.mode;
        this.query = packet.query;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.CHALLENGE.ordinal();
    }
    
}
