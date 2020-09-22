package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBattleSpectateSpigot extends PacketSpigot {

    public UUID spectator;
    public UUID target;

    public PacketBattleSpectateSpigot() { }

    public PacketBattleSpectateSpigot(DBPlayer spectator, DBPlayer target) {
        this.spectator = spectator.getUniqueId();
        this.target = target.getUniqueId();
    }

    @Override
    public int getTag() {
        return PacketType.Spigot.BATTLE_SPECTATE.ordinal();
    }

}
