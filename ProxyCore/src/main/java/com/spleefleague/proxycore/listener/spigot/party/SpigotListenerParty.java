package com.spleefleague.proxycore.listener.spigot.party;

import com.spleefleague.coreapi.utils.packet.spigot.party.PacketSpigotParty;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerParty extends SpigotListener<PacketSpigotParty> {

    @Override
    protected void receive(Connection sender, PacketSpigotParty packet) {
        switch (packet.type) {
            case INVITE:
                ProxyCore.getInstance().getPartyManager().onInvite(packet.sender, packet.target);
                break;
            case TRANSFER:
                ProxyCore.getInstance().getPartyManager().onTransfer(packet.sender, packet.target);
                break;
            case LEAVE:
                ProxyCore.getInstance().getPartyManager().onLeave(packet.sender);
                break;
            case ACCEPT:
                ProxyCore.getInstance().getPartyManager().onAccept(packet.sender, packet.target);
                break;
            case DECLINE:
                ProxyCore.getInstance().getPartyManager().onDecline(packet.sender, packet.target);
                break;
            case DISBAND:
                ProxyCore.getInstance().getPartyManager().onDisband(packet.sender);
                break;
            case KICK:
                ProxyCore.getInstance().getPartyManager().onKick(packet.sender, packet.target);
                break;
        }
    }

}
