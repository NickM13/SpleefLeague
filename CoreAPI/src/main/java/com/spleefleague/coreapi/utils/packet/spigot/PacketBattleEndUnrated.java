package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBattleEndUnrated extends PacketSpigot {

    public String mode;
    public List<UUID> players;

    public PacketBattleEndUnrated() { }

    public PacketBattleEndUnrated(String mode, List<UUID> players) {
        this.mode = mode;
        this.players = players;
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.BATTLE_END_UNRATED.ordinal();
    }

}
