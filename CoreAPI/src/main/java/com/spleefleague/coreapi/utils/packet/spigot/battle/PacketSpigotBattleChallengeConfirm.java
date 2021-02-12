package com.spleefleague.coreapi.utils.packet.spigot.battle;

import com.spleefleague.coreapi.utils.packet.PacketType;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author NickM13
 * @since 9/19/2020
 */
public class PacketSpigotBattleChallengeConfirm extends PacketSpigot {

    public enum Confirmation {
        ACCEPT,
        DECLINE
    }

    public UUID sender;
    public UUID receiver;
    public Confirmation confirmation;

    public PacketSpigotBattleChallengeConfirm() { }

    public PacketSpigotBattleChallengeConfirm(UUID sender, UUID receiver, Confirmation confirmation) {
        this.sender = sender;
        this.receiver = receiver;
        this.confirmation = confirmation;
    }

    @Nonnull
    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.BATTLE_CHALLENGE_CONFIRM;
    }

}
