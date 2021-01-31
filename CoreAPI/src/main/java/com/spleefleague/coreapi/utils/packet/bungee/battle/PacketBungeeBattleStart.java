package com.spleefleague.coreapi.utils.packet.bungee.battle;

import com.google.common.io.ByteArrayDataInput;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeBattleStart extends PacketBungee {

    public String mode;
    public String query;
    public List<UUID> players;

    public PacketBungeeBattleStart() { }

    public PacketBungeeBattleStart(String mode, String query, List<UUID> players) {
        this.mode = mode;
        this.query = query;
        this.players = players;
    }

    public PacketBungeeBattleStart(ByteArrayDataInput input) {
        fromByteArray(input);
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.BATTLE_START.ordinal();
    }

}
