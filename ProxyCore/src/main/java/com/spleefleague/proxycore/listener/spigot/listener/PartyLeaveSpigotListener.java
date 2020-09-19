package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.spigot.PacketPartyLeave;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class PartyLeaveSpigotListener extends SpigotListener<PacketPartyLeave> {

    @Override
    protected void receive(Connection sender, PacketPartyLeave packet) {

    }

}
