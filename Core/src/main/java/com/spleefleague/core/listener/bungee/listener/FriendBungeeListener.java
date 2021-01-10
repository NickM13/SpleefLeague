package com.spleefleague.core.listener.bungee.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.PacketFriendBungee;
import org.bukkit.entity.Player;

public class FriendBungeeListener extends BungeeListener<PacketFriendBungee> {

    @Override
    protected void receive(Player sender, PacketFriendBungee packet) {
        CorePlayer cpReceiver = Core.getInstance().getPlayers().get(packet.receiver);
        CorePlayer cpSender = Core.getInstance().getPlayers().get(packet.sender);
        switch (packet.type) {
            case ADD:
                cpReceiver.getFriends().receiveFriendRequest(cpSender);
                break;
            case REMOVE:
                cpReceiver.getFriends().receiveFriendRemove(cpSender);
                break;
        }
    }

}
