package com.spleefleague.core.listener.bungee.friend;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.friend.PacketBungeeFriend;
import org.bukkit.entity.Player;

public class BungeeListenerFriend extends BungeeListener<PacketBungeeFriend> {

    @Override
    protected void receive(Player sender, PacketBungeeFriend packet) {
        CorePlayer cpReceiver = Core.getInstance().getPlayers().getOffline(packet.receiver);
        CorePlayer cpSender = Core.getInstance().getPlayers().getOffline(packet.sender);
        if (cpReceiver == null || cpSender == null) {
            CoreLogger.logError("Received friend packet of null player " + packet.type + " " + packet.receiver + " " + packet.sender);
            return;
        }
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
