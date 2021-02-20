package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerOptions;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerPlayerOptions extends SpigotListener<PacketSpigotPlayerOptions> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerOptions packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        switch (packet.type) {
            case BOOLEAN:
                pcp.getOptions().setBoolean(packet.optionName, packet.boolOption);
                break;
            case INTEGER:
                pcp.getOptions().setInteger(packet.optionName, packet.intOption);
                break;
            case DOUBLE:
                pcp.getOptions().setDouble(packet.optionName, packet.doubleOption);
                break;
            case STRING:
                pcp.getOptions().setString(packet.optionName, packet.strOption);
                break;
        }
    }

}
