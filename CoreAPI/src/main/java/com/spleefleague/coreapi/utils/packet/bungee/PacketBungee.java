package com.spleefleague.coreapi.utils.packet.bungee;

import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketType;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public abstract class PacketBungee extends Packet {

    public abstract PacketType.Bungee getBungeeTag();

    public final int getTag() {
        return getBungeeTag().ordinal();
    }

}
