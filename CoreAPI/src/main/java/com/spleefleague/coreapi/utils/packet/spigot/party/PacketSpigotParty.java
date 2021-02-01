package com.spleefleague.coreapi.utils.packet.spigot.party;

import com.spleefleague.coreapi.party.PartyAction;
import com.spleefleague.coreapi.utils.packet.spigot.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketSpigotParty extends PacketSpigot {

    public PartyAction type;
    public UUID sender, target;

    public PacketSpigotParty() {

    }

    public PacketSpigotParty(PartyAction type, UUID sender) {
        this.type = type;
        this.sender = sender;
        this.target = null;
    }

    public PacketSpigotParty(PartyAction type, UUID sender, UUID target) {
        this.type = type;
        this.sender = sender;
        this.target = target;
    }

    @Override
    public PacketType.Spigot getSpigotTag() {
        return PacketType.Spigot.PARTY;
    }

}
