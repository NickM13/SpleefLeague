package com.spleefleague.proxycore.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.game.BattleManager;
import com.spleefleague.proxycore.game.leaderboard.Leaderboards;
import com.spleefleague.proxycore.game.queue.QueueManager;
import com.spleefleague.proxycore.game.queue.QueuePlayer;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 6/11/2020
 */
public class BattleListener implements Listener {

    public BattleListener() {
        super();
        ProxyCore.getInstance().getProxy().registerChannel("battle:forcestart");
        ProxyCore.getInstance().getProxy().registerChannel("battle:end");
        ProxyCore.getInstance().getProxy().registerChannel("battle:spectate");
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        String server = BattleManager.getInstance().getPlayerBattleServer(event.getConnection().getUniqueId());
        if (server != null) {
            // Connect player to that server, for rejoining teamspleef or something? Probably will just be a /rejoin command though
        }
    }

    @EventHandler
    public void onBattleMessage(PluginMessageEvent event) {
        switch (event.getTag()) {
            case "battle:forcestart": {
                ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
                List<QueuePlayer> players = new ArrayList<>();
                String mode = input.readUTF();
                String param = input.readUTF();
                int playerCount = input.readInt();
                for (int i = 0; i < playerCount; i++) {
                    players.add(new QueuePlayer(ProxyCore.getInstance().getPlayers().get(UUID.fromString(input.readUTF())), param));
                }
                QueueManager.forceStart(mode, param, players);
            }
                break;
            case "battle:end": {
                ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
                List<ProxyCorePlayer> players = new ArrayList<>();
                String modeName = input.readUTF();
                boolean rated = input.readBoolean();
                if (rated) {
                    int season = input.readInt();
                    int playerCount = input.readInt();
                    ByteArrayDataOutput scoreOut = ByteStreams.newDataOutput();
                    scoreOut.writeUTF(modeName);
                    scoreOut.writeInt(season);
                    scoreOut.writeInt(playerCount);
                    for (int i = 0; i < playerCount; i++) {
                        ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().get(UUID.fromString(input.readUTF()));
                        players.add(pcp);
                        int score = input.readInt();
                        Leaderboards.get(modeName).getLeaderboards().get(season).setPlayerScore(pcp.getUniqueId(), score);
                        scoreOut.writeUTF(pcp.getUniqueId().toString());
                        scoreOut.writeInt(score);
                    }
                    for (Map.Entry<String, ServerInfo> entry : ProxyCore.getInstance().getProxy().getServers().entrySet()) {
                        entry.getValue().sendData("slcore:score", scoreOut.toByteArray());
                    }
                } else {
                    int playerCount = input.readInt();
                    for (int i = 0; i < playerCount; i++) {
                        players.add(ProxyCore.getInstance().getPlayers().get(UUID.fromString(input.readUTF())));
                    }
                }
                for (ProxyCorePlayer pcp : players) {
                    if (pcp != null && pcp.getPlayer() != null) {
                        //pcp.getPlayer().connect(lobby);
                        pcp.setInBattle(false);
                    }
                }
            }
                break;
            case "battle:spectate": {
                ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
                ProxyCorePlayer spectator = ProxyCore.getInstance().getPlayers().get(UUID.fromString(input.readUTF()));
                ProxyCorePlayer target = ProxyCore.getInstance().getPlayers().get(UUID.fromString(input.readUTF()));
                if (target.isInBattle()) {
                    spectator.transfer(target.getCurrentServer());
                    ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), () -> {
                        target.getCurrentServer().sendData("battle:spectate", event.getData());
                    }, 500, TimeUnit.MILLISECONDS);
                } else {
                    spectator.getPlayer().sendMessage(new TextComponent(ChatColor.YELLOW + target.getName() + ChatColor.RED + "'s game cannot be spectated"));
                }
            }
                break;
        }
    }

}
