package com.spleefleague.coreapi.utils.packet.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import com.spleefleague.coreapi.utils.packet.PacketType;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketPartyJoin extends PacketSpigot {

    public PacketPartyJoin() { }

    @Override
    public int getTag() {
        return PacketType.Spigot.PARTY_JOIN.ordinal();
    }

    @Override
    public void fromByteArray(ByteArrayDataInput input) {

    }

    @Override
    protected void toByteArray(ByteArrayDataOutput output) {

    }
}
