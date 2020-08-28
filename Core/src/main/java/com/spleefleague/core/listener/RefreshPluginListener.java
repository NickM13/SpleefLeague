package com.spleefleague.core.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class RefreshPluginListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (s.equals("slcore:refresh")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            String type = input.readUTF();
            if (type.equals("players")) {
                String pluginName = input.readUTF();
                int count = input.readInt();
                for (int i = 0; i < count; i++) {
                    UUID uuid = UUID.fromString(input.readUTF());
                    for (CorePlugin<?> plugin : CorePlugin.getAllPlugins()) {
                        if (pluginName.equals(plugin.getName())) {
                            plugin.getPlayers().refresh(uuid);
                        }
                    }
                }
            } else if (type.equals("leaderboard")) {
                String name = input.readUTF();
            }
        } else if (s.equals("slcore:score")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            String mode = input.readUTF();
            int season = input.readInt();
            int playerCount = input.readInt();
            for (int i = 0; i < playerCount; i++) {
                CorePlayer cp = Core.getInstance().getPlayers().get(UUID.fromString(input.readUTF()));
                int score = input.readInt();
                cp.getRatings().setRating(mode, season, score);
                Leaderboards.get(mode).getLeaderboards().get(season).setPlayerScore(cp.getUniqueId(), score);
                System.out.println(cp.getDisplayName() + " " + score);
            }
        }
    }

}
