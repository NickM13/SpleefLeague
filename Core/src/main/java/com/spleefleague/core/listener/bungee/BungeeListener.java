package com.spleefleague.core.listener.bungee;

import com.spleefleague.coreapi.utils.packet.PacketBungee;
import org.bukkit.entity.Player;

public abstract class BungeeListener <P extends PacketBungee> {

    public void receivePacket(Player sender, PacketBungee packet) {
        if (packet != null) {
            receive(sender, (P) packet);
        }
    }

    protected abstract void receive(Player sender, P packet);

}
