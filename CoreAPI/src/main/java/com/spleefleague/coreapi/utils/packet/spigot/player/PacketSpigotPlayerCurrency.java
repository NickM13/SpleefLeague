package com.spleefleague.coreapi.utils.packet.spigot.player;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.shared.NumAction;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import java.util.UUID;

/**
 * @author NickM13
 */
public class PacketSpigotPlayerCurrency extends PacketSpigot {

    public enum Type {

        COIN,
        ORE_COMMON,
        ORE_EPIC,
        ORE_LEGENDARY,
        ORE_RARE;

    }

    public UUID uuid;
    public NumAction action;
    public Type type;
    public Integer amount;

    public PacketSpigotPlayerCurrency() {

    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PLAYER_CURRENCY;
    }

    public PacketSpigotPlayerCurrency(UUID uuid, NumAction action, Type type, Integer amount) {
        this.uuid = uuid;
        this.action = action;
        this.type = type;
        this.amount = amount;
    }

}
