package com.spleefleague.coreapi.utils.packet.bungee;

import com.spleefleague.coreapi.utils.packet.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketConnection extends PacketBungee {

    public enum ConnectionType {
        CONNECT, DISCONNECT
    }

    public ConnectionType type;
    public UUID uuid;

    public PacketConnection() { }

    public PacketConnection(ConnectionType type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    @Override
    public int getTag() {
        return PacketType.Bungee.CONNECTION.ordinal();
    }

}
