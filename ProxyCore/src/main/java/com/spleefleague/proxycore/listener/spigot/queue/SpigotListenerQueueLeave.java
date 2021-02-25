package com.spleefleague.proxycore.listener.spigot.queue;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.utils.packet.spigot.queue.PacketSpigotQueueLeave;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.party.ProxyParty;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class SpigotListenerQueueLeave extends SpigotListener<PacketSpigotQueueLeave> {

    @Override
    protected void receive(Connection sender, PacketSpigotQueueLeave packet) {
        boolean hasLeft = false;
        if (ProxyCore.getInstance().getQueueManager().leaveAllQueues(packet.player)) {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(packet.player), ChatColor.GRAY + "You have left all queues");
            hasLeft = true;
        }
        if (!hasLeft) {
            ProxyParty party = ProxyCore.getInstance().getPartyManager().getParty(packet.player);
            if (party != null) {
                if (party.getOwner().equals(packet.player) && ProxyCore.getInstance().getQueueManager().leaveAllQueues(party)) {
                    party.sendMessage(new TextComponent("Your party has left all queues"));
                } else {
                    ProxyCore.getInstance().getPartyManager().onLeave(packet.player);
                }
                hasLeft = true;
            }
        }
        if (!hasLeft) {
            ProxyCore.getInstance().sendMessage(ProxyCore.getInstance().getPlayers().get(packet.player), ChatColor.RED + "You have nothing to leave!");
        }
    }

}
