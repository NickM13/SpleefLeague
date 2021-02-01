package com.spleefleague.coreapi.utils.packet.spigot;

import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketType;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public abstract class PacketSpigot extends Packet {

    public abstract PacketType.Spigot getSpigotTag();

    public final int getTag() {
        return getSpigotTag().ordinal();
    }

}
