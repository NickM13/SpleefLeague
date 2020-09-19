package com.spleefleague.proxycore.listener.spigot;

import com.spleefleague.coreapi.utils.packet.Packet;
import com.spleefleague.coreapi.utils.packet.PacketSpigot;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public abstract class SpigotListener <P extends PacketSpigot> {

    public void receivePacket(Connection sender, PacketSpigot packet) {
        if (packet != null) {
            receive(sender, (P) packet);
        }
    }

    protected abstract void receive(Connection sender, P packet);

}
