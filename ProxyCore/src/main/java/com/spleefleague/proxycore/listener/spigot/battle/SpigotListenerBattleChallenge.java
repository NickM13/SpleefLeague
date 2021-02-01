package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.bungee.battle.PacketBungeeBattleChallenge;
import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleChallenge;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattleChallenge extends SpigotListener<PacketSpigotBattleChallenge> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleChallenge packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.receiver);
        if (pcp != null) {
            ProxyCore.getInstance().getPacketManager().sendPacket(pcp.getCurrentServer(), new PacketBungeeBattleChallenge(packet));
        }
    }

}
