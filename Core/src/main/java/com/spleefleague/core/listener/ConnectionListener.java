/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.music.NoteBlockMusic;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import com.spleefleague.core.player.scoreboard.PersonalScoreboard;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.world.FakeWorld;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.UUID;

/**
 * @author NickM13
 */
public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        FakeWorld.onPlayerJoin(event.getPlayer().getUniqueId());
        event.setJoinMessage("");
        Player player = event.getPlayer();
        CorePlugin.onPlayerJoin(player);

        CorePlayer cp = Core.getInstance().getPlayers().get(player);
        cp.gotoSpawn();
        PersonalScoreboard.initPlayerScoreboard(cp);
        Core.getInstance().applyVisibilities(cp);
        Core.getInstance().getPartyManager().onConnect(cp);
        NoteBlockMusic.onPlayerJoin(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        CorePlugin.onPlayerQuit(uuid);
        event.setQuitMessage("");
        Core.getInstance().getPartyManager().onDisconnect(uuid);
        NoteBlockMusic.onPlayerQuit(uuid);
        FakeWorld.onPlayerQuit(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (event == null) {
            CoreLogger.logError(null, new NullPointerException("PlayerResourcePackStatusEvent null"));
            return;
        }
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer().getUniqueId());
        if (cp == null || !cp.getRank().hasPermission(CoreRank.TEMP_MOD)) {
            switch (event.getStatus()) {
                case DECLINED:
                    event.getPlayer().kickPlayer("Allow the SpleefLeague resource pack to be used in order to log in!");
                    break;
                case FAILED_DOWNLOAD:
                    event.getPlayer().kickPlayer("There was an issue while downloading the resource pack, try logging out and back in");
                    break;
            }
        }
    }

}
