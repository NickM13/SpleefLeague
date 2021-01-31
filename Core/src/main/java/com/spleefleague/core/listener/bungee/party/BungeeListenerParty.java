package com.spleefleague.core.listener.bungee.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import org.bukkit.entity.Player;

public class BungeeListenerParty extends BungeeListener<PacketBungeeParty> {

    @Override
    protected void receive(Player sender, PacketBungeeParty packet) {
        switch (packet.type) {
            case CREATE:
                Core.getInstance().getPartyManager().onCreate(packet.sender);
                break;
            case ADD:
                Core.getInstance().getPartyManager().onAdd(packet.sender, packet.target);
                break;
            case KICK:
                Core.getInstance().getPartyManager().onKick(packet.sender);
                break;
            case TRANSFER:
                Core.getInstance().getPartyManager().onTransfer(packet.sender, packet.target);
                break;
            case LEAVE:
                Core.getInstance().getPartyManager().onLeave(packet.sender);
                break;
            case JOIN:
                Core.getInstance().getPartyManager().onJoin(packet.sender, packet.target);
                break;
            case INVITE:
                Core.getInstance().getPartyManager().onInvite(packet.sender, packet.target);
                break;
        }
    }

}
