package com.spleefleague.coreapi.utils.packet.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketSpigotBattleChallenge extends PacketSpigot {

    public UUID sender;
    public UUID receiver;
    public String mode;
    public String query;

    public PacketSpigotBattleChallenge() { }

    public PacketSpigotBattleChallenge(UUID sender, UUID receiver, String mode, String query) {
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
