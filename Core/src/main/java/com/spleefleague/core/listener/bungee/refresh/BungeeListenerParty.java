package com.spleefleague.core.listener.bungee.refresh;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshParty;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class BungeeListenerParty extends BungeeListener<PacketBungeeRefreshParty> {

    @Override
    protected void receive(Player sender, PacketBungeeRefreshParty packet) {
        List<UUID> players = packet.partyInfo.players;
        CorePlayer owner = Core.getInstance().getPlayers().get(players.get(0));
        Core.getInstance().getPartyManager().onRefresh(owner, players);
    }

}
