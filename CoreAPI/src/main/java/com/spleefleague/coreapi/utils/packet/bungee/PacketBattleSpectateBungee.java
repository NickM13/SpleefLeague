package com.spleefleague.coreapi.utils.packet.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketBattleSpectateSpigot;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBattleSpectateBungee extends PacketBungee {

    public UUID spectator;
    public UUID target;

    public PacketBattleSpectateBungee() { }

    public PacketBattleSpectateBungee(PacketBattleSpectateSpigot packet) {
        this.spectator = packet.spectator;
        this.target = packet.target;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.BATTLE_SPECTATE.ordinal();
    }

}
