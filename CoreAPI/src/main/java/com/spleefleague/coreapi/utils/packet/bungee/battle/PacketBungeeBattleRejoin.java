package com.spleefleague.coreapi.utils.packet.bungee.battle;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketBungeeBattleRejoin extends PacketBungee {

    public UUID sender;
    public String mode;
    public UUID battleId;

    public PacketBungeeBattleRejoin() { }

    public PacketBungeeBattleRejoin(UUID sender, String mode, UUID battleId) {
        this.sender = sender;
        this.mode = mode;
        this.battleId = battleId;
    }

    @Nonnull
    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.BATTLE_REJOIN;
    }
    
}
