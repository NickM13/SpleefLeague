package com.spleefleague.coreapi.utils.packet.bungee.battle;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleChallenge;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketBungeeBattleChallenge extends PacketBungee {

    public UUID sender;
    public UUID receiver;
    public String mode;
    public String query;

    public PacketBungeeBattleChallenge() { }

    public PacketBungeeBattleChallenge(PacketSpigotBattleChallenge packet) {
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
