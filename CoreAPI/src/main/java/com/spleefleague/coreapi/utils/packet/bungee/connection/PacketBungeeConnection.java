package com.spleefleague.coreapi.utils.packet.bungee.connection;

import com.spleefleague.coreapi.utils.packet.bungee.PacketBungee;
import com.spleefleague.coreapi.utils.packet.PacketType;

import java.util.UUID;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PacketBungeeConnection extends PacketBungee {

    public enum ConnectionType {
        FIRST_CONNECT, CONNECT, DISCONNECT
    }

    public ConnectionType type;
    public UUID uuid;

    public PacketBungeeConnection() { }

    public PacketBungeeConnection(ConnectionType type, UUID uuid) {
        this.type = type;
        this.uuid = uuid;
    }

    @Override
    public PacketType.Bungee getBungeeTag() {
        return PacketType.Bungee.CONNECTION;
    }

}
