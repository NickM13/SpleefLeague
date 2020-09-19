package com.spleefleague.proxycore.listener.spigot.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.coreapi.utils.packet.bungee.PacketBattleSpectateBungee;
import com.spleefleague.coreapi.utils.packet.spigot.PacketBattleSpectateSpigot;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.listener.spigot.SpigotListener;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 9/18/2020
 */
public class BattleSpectateSpigotListener extends SpigotListener<PacketBattleSpectateSpigot> {

    @Override
    protected void receive(Connection sender, PacketBattleSpectateSpigot packet) {
        ProxyCorePlayer spectator = ProxyCore.getInstance().getPlayers().get(packet.spectator);
        ProxyCorePlayer target = ProxyCore.getInstance().getPlayers().get(packet.target);
        if (target.getBattleContainer() != null) {
            spectator.transfer(target.getCurrentServer());
            ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
                ProxyCore.getInstance().sendPacket(target.getCurrentServer(), new PacketBattleSpectateBungee(packet));
            }, 500, TimeUnit.MILLISECONDS);
            target.getBattleContainer().addSpectator(spectator.getUniqueId());
            spectator.setBattleContainer(target.getBattleContainer());
        } else {
            spectator.getPlayer().sendMessage(new TextComponent(ChatColor.YELLOW + target.getName() + ChatColor.RED + "'s game cannot be spectated"));
        }
    }

}
