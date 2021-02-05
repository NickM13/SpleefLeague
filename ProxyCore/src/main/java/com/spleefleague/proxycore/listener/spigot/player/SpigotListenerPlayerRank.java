package com.spleefleague.proxycore.listener.spigot.player;

import com.spleefleague.coreapi.utils.packet.spigot.player.PacketSpigotPlayerRank;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class SpigotListenerPlayerRank extends SpigotListener<PacketSpigotPlayerRank> {

    @Override
    protected void receive(Connection sender, PacketSpigotPlayerRank packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.uuid);
        if (packet.rankName.length() > 0) {
            if (packet.duration <= 0) {
                pcp.setPermRank(ProxyCore.getInstance().getRankManager().getRank(packet.rankName));
            } else {
                pcp.addTempRank(packet.rankName, packet.duration);
            }
        } else {
            pcp.clearTempRanks();
        }
    }

}
