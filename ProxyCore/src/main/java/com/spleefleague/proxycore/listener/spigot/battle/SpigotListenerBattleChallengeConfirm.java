package com.spleefleague.proxycore.listener.spigot.battle;

import com.spleefleague.coreapi.utils.packet.spigot.battle.PacketSpigotBattleChallengeConfirm;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerBattleChallengeConfirm extends SpigotListener<PacketSpigotBattleChallengeConfirm> {

    @Override
    protected void receive(Connection sender, PacketSpigotBattleChallengeConfirm packet) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(packet.receiver);
        if (pcp != null) {
            if (packet.confirmation == PacketSpigotBattleChallengeConfirm.Confirmation.ACCEPT) {
                ProxyCore.getInstance().getChallengeManager().onAccept(packet.sender, packet.receiver);
            } else if (packet.confirmation == PacketSpigotBattleChallengeConfirm.Confirmation.DECLINE) {
                ProxyCore.getInstance().getChallengeManager().onDecline(packet.sender, packet.receiver);
            }
        }
    }

}
