package com.spleefleague.core.listener.bungee.party;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.coreapi.utils.packet.bungee.party.PacketBungeeParty;
import org.bukkit.entity.Player;

public class BungeeListenerParty extends BungeeListener<PacketBungeeParty> {

    @Override
    protected void receive(Player sender, PacketBungeeParty packet) {
        switch (packet.type) {
            case DISBAND:
                Core.getInstance().getPartyManager().onDisband(packet.sender);
                break;
            case LEAVE:
                Core.getInstance().getPartyManager().onLeave(packet.sender);
                break;
        }
    }

}
