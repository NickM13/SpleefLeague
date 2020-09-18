package com.spleefleague.core.listener;

import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.leaderboard.Leaderboards;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * slcore:refresh called whenever a player joins a server that was previously empty
 */
public class RefreshPluginListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String s, @NotNull Player player, @NotNull byte[] bytes) {
        if (s.equals("slcore:refresh")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            int count = input.readInt();
            Set<UUID> players = new HashSet<>();
            for (int i = 0; i < count; i++)
                players.add(UUID.fromString(input.readUTF()));
            for (CorePlugin<?> plugin : CorePlugin.getAllPlugins())
                plugin.getPlayers().refresh(Sets.newHashSet(players));
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
            }
        }
    }

}
