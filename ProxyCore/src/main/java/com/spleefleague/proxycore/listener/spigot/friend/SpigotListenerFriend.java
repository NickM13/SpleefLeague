package com.spleefleague.proxycore.listener.spigot.friend;

import com.spleefleague.coreapi.utils.packet.bungee.friend.PacketBungeeFriend;
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
        ProxyCorePlayer pcpSender = ProxyCore.getInstance().getPlayers().get(packet.sender);
        ProxyCorePlayer pcpReceiver = null;
        if (packet.receiver != null)
            pcpReceiver = ProxyCore.getInstance().getPlayers().get(packet.receiver);
        switch (packet.type) {
            case ADD:
                ProxyCore.getInstance().getFriendManager().onPlayerAdd(pcpSender, pcpReceiver);
                break;
            case REMOVE:

                break;
        }
        //ProxyCore.getInstance().sendPacket(packet.receiver, new PacketBungeeFriend(packet.type, packet.sender, packet.receiver));
    }

}
