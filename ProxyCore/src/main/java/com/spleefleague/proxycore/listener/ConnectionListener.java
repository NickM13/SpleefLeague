package com.spleefleague.proxycore.listener;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionType;
import com.spleefleague.coreapi.utils.TimeUtils;
import com.spleefleague.coreapi.utils.packet.bungee.connection.PacketBungeeConnection;
import com.spleefleague.coreapi.utils.packet.bungee.refresh.PacketBungeeRefreshAll;
import com.spleefleague.coreapi.utils.packet.shared.QueueContainerInfo;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.droplet.Droplet;
import com.spleefleague.proxycore.droplet.DropletManager;
import com.spleefleague.proxycore.game.session.BattleSessionManager;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ConnectionListener implements Listener {

    @EventHandler
    public void onPreLogin(LoginEvent event) {
        // Save if loaded here, in case any offline player data was changed recently
        ProxyCore.getInstance().getPlayers().saveIfLoaded(event.getConnection().getUniqueId());
        Infraction infraction = ProxyCore.getInstance().getInfractions().isBanned(event.getConnection().getUniqueId());
        if (infraction != null) {
            if (infraction.getType() == InfractionType.TEMPBAN) {
                long remaining = infraction.getRemainingTime();
                if (remaining > 0) {
                    TextComponent component = new TextComponent();
                    component.addExtra("You are temp-banned!");
                    component.addExtra("\n\n");
                    component.addExtra("Reason: " + infraction.getReason());
                    component.addExtra("\n\n");
                    component.addExtra("Remaining time: " + TimeUtils.timeToString(remaining));
                    event.setCancelReason(component);
                    event.setCancelled(true);
                }
            } else {
                TextComponent component = new TextComponent();
                component.addExtra("You are banned!");
                component.addExtra("\n\n");
                component.addExtra("Reason: " + infraction.getReason());
                event.setCancelReason(component);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        Droplet droplet = ProxyCore.getInstance().getDropletManager().getBestLobby(ProxyCore.getInstance().getPlayers().getOffline(event.getPlayer().getUniqueId()));
        if (droplet == null) {
            if (!ProxyCore.getInstance().getDropletManager().isEnabled()) {
                return;
            }
            System.out.println("Droplet was null!");
            event.setCancelled(true);
        } else {
            System.out.println("Player was redirected!");
            event.setTarget(droplet.getInfo());
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        PacketBungeeConnection packetConnection = new PacketBungeeConnection(PacketBungeeConnection.ConnectionType.CONNECT, event.getPlayer().getUniqueId());
        ProxyCore.getInstance().getPacketManager().sendPacket(packetConnection);

        ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
            ProxyCore.getInstance().getPacketManager().sendPacket(event.getPlayer().getUniqueId(), new PacketBungeeConnection(PacketBungeeConnection.ConnectionType.FIRST_CONNECT, event.getPlayer().getUniqueId()));
        }, 1000, TimeUnit.MILLISECONDS);

        ProxyCore.getInstance().getPartyManager().onConnect(event.getPlayer().getUniqueId());
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().onPlayerJoin(event.getPlayer());

        TextComponent textComponent = new TextComponent("Welcome to SpleefLeague, ");
        textComponent.addExtra(pcp.getChatName());
        textComponent.addExtra("!");
        ProxyCore.getInstance().sendMessage(pcp, textComponent);

        if (pcp.getFriends().getIncoming().size() > 0) {
            textComponent = new TextComponent("You have " + pcp.getFriends().getIncoming().size() + " friend requests ");
            TextComponent click = new TextComponent();
            click.addExtra(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Click to view" + ChatColor.DARK_GRAY + "]");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests"));
            textComponent.addExtra(click);
            ProxyCore.getInstance().sendMessage(pcp, textComponent);
        }
        if (pcp.getCurrentBattle() != null && BattleSessionManager.isOngoing(pcp.getCurrentBattle())) {
            textComponent = new TextComponent("Looks like you disconnected from your last battle! Would you like to rejoin?");
            TextComponent click = new TextComponent();
            click.addExtra(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Rejoin battle" + ChatColor.DARK_GRAY + "]");
            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rejoin"));
            textComponent.addExtra(click);
            ProxyCore.getInstance().sendMessage(pcp, textComponent);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxyCore.getInstance().getPacketManager().sendPacket(new PacketBungeeConnection(PacketBungeeConnection.ConnectionType.DISCONNECT, event.getPlayer().getUniqueId()));

        ProxyCore.getInstance().getPlayers().onPlayerQuit(event.getPlayer());

        ProxyCore.getInstance().getPartyManager().onDisconnect(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxyCorePlayer pcp  = ProxyCore.getInstance().getPlayers().get(event.getPlayer().getUniqueId());
        ServerInfo info = event.getPlayer().getServer().getInfo();
        pcp.setCurrentServer(info);
        pcp.setCurrentDroplet(ProxyCore.getInstance().getDropletManager().getDropletByName(info.getName()));

        if (event.getPlayer().getServer().getInfo().getPlayers().size() == 1) {
            List<QueueContainerInfo> queueInfoList = new ArrayList<>();

            ProxyCore.getInstance().getPacketManager().sendPacket(
                    event.getPlayer().getServer().getInfo(),
                    new PacketBungeeRefreshAll(ProxyCore.getInstance().getPlayers().getAll(), queueInfoList));
        }

        ProxyCore.getInstance().getPartyManager().onServerSwap(pcp);
    }

}
