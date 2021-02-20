package com.spleefleague.core.listener.bungee.friend;

import com.spleefleague.core.Core;
import com.spleefleague.core.listener.bungee.BungeeListener;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.utils.packet.bungee.friend.PacketBungeeFriend;
import org.bukkit.entity.Player;

public class BungeeListenerFriend extends BungeeListener<PacketBungeeFriend> {

    @Override
    protected void receive(Player sender, PacketBungeeFriend packet) {
        CorePlayer cpSender = Core.getInstance().getPlayers().get(packet.sender);
        CorePlayer cpReceiver = Core.getInstance().getPlayers().get(packet.receiver);
        if (cpReceiver == null || cpSender == null) {
            CoreLogger.logError("Received friend packet of null player " + packet.type + " " + packet.receiver + " " + packet.sender);
            return;
        }
        switch (packet.type) {
            case ADD:
                cpSender.getFriends().syncAdd(cpReceiver);
                break;
            case REMOVE:
                cpSender.getFriends().syncRemove(cpReceiver);
                break;
            case DECLINE_INCOMING:
                cpSender.getFriends().syncDeclineIncoming(cpReceiver);
                break;
            case DECLINE_OUTGOING:
                cpSender.getFriends().syncDeclineOutgoing(cpReceiver);
                break;
            case INCOMING:
                cpSender.getFriends().syncIncoming(cpReceiver);
                break;
            case OUTGOING:
                cpSender.getFriends().syncOutgoing(cpReceiver);
                break;
        }
    }

}
