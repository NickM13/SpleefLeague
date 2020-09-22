package com.spleefleague.proxycore.listener.spigot.listener;

import com.spleefleague.coreapi.utils.packet.bungee.PacketChallengeBungee;
import com.spleefleague.coreapi.utils.packet.spigot.PacketChallengeSpigot;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class ChallengeSpigotListener extends SpigotListener<PacketChallengeSpigot> {

    @Override
    protected void receive(Connection sender, PacketChallengeSpigot packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.receiver);
        if (pcp != null) {
            ProxyCore.getInstance().sendPacket(pcp.getCurrentServer(), new PacketChallengeBungee(packet));
        }
    }

}
