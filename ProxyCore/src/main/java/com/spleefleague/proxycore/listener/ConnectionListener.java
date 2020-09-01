package com.spleefleague.proxycore.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;

/**
 * @author NickM13
 * @since 6/6/2020
 */
public class ConnectionListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeUTF("connect");
        output.writeUTF(event.getPlayer().getUniqueId().toString());

        for (Map.Entry<String, ServerInfo> server : ProxyCore.getInstance().getProxy().getServers().entrySet()) {
            if (!server.getValue().getPlayers().isEmpty()) {
                server.getValue().sendData("slcore:connection", output.toByteArray());
            }
        }

        ProxyCore.getInstance().getPlayers().onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeUTF("disconnect");
        output.writeUTF(event.getPlayer().getUniqueId().toString());

        for (Map.Entry<String, ServerInfo> server : ProxyCore.getInstance().getProxy().getServers().entrySet()) {
            if (!server.getValue().getPlayers().isEmpty()) {
                server.getValue().sendData("slcore:connection", output.toByteArray());
            }
        }

        ProxyCore.getInstance().getPlayers().onPlayerQuit(event.getPlayer());
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        ByteArrayDataInput input;
        String type;
        switch (event.getTag()) {
            case "slcore:connection":
                input = ByteStreams.newDataInput(event.getData());
                type = input.readUTF();
                switch (type) {
                    case "lobby":
                        if (event.getSender() instanceof ProxiedPlayer) {
                            ((ProxiedPlayer) event.getSender()).connect(ProxyCore.getInstance().getProxy().getServers().get("lobby"));
                        }
                        break;
                }
                break;
        }
    }

}
