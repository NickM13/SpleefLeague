package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBattleStart extends PacketBungee {

    public String mode;
    public String query;
    public List<UUID> players;

    public PacketBattleStart() { }

    public PacketBattleStart(String mode, String query, List<UUID> players) {
        this.mode = mode;
        this.query = query;
        this.players = players;
    }

    public PacketBattleStart(ByteArrayDataInput input) {
        fromByteArray(input);
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.BATTLE_START.ordinal();
    }

}
