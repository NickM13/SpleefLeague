package com.spleefleague.proxycore.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.leaderboard.LeaderboardManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 6/9/2020
 */
public class CoreListener implements Listener {

    public CoreListener() {
        super();
        ProxyCore.getInstance().getProxy().registerChannel("slcore:chat");
        ProxyCore.getInstance().getProxy().registerChannel("slcore:connection");
        ProxyCore.getInstance().getProxy().registerChannel("slcore:refresh");
        ProxyCore.getInstance().getProxy().registerChannel("slcore:score");
    }

    @EventHandler
    public void onCoreMessage(PluginMessageEvent event) {
        switch (event.getTag()) {
            case "slcore:chat":
                for (Map.Entry<String, ServerInfo> entry : ProxyCore.getInstance().getProxy().getServers().entrySet()) {
                    if (!entry.getValue().getPlayers().isEmpty()) {
                        entry.getValue().sendData("slcore:chat", event.getData());
                    }
                }
                break;
            case "slcore:connection":

                break;
            case "slcore:refresh":
                for (Map.Entry<String, ServerInfo> entry : ProxyCore.getInstance().getProxy().getServers().entrySet()) {
                    entry.getValue().sendData("slcore:refresh", event.getData());
                }
                break;
            case "slcore:score":
                ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
                String mode = input.readUTF();
                int season = input.readInt();
                int playerCount = input.readInt();
                for (int i = 0; i < playerCount; i++) {
                    UUID player = UUID.fromString(input.readUTF());
                    int score = input.readInt();
                    LeaderboardManager.get(mode).getLeaderboards().get(season).setPlayerScore(player, score);
                }
                for (Map.Entry<String, ServerInfo> entry : ProxyCore.getInstance().getProxy().getServers().entrySet()) {
                    entry.getValue().sendData("slcore:score", event.getData());
                }
                break;
        }
    }

}
