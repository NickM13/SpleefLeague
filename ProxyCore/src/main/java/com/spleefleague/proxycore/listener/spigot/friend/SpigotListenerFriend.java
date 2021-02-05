package com.spleefleague.proxycore.listener.spigot.friend;

import com.spleefleague.coreapi.utils.packet.spigot.friend.PacketSpigotFriend;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerFriend extends SpigotListener<PacketSpigotFriend> {

    @Override
    protected void receive(Connection sender, PacketSpigotFriend packet) {
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().getOffline(packet.sender);
        ProxyCorePlayer pcpReceiver = ProxyCore.getInstance().getPlayers().getOffline(packet.receiver);
        switch (packet.type) {
            case ADD:
                pcpSender.getFriends().onFriendRequest(pcpReceiver);
                break;
            case REMOVE:
                pcpSender.getFriends().onFriendRemove(pcpReceiver);
                break;
            case DECLINE_INCOMING:
                pcpSender.getFriends().onFriendDecline(pcpReceiver);
                break;
        }
        //ProxyCore.getInstance().sendPacket(packet.receiver, new PacketBungeeFriend(packet.type, packet.sender, packet.receiver));
    }

}
