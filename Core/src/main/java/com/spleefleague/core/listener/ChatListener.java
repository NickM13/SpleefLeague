/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author NickM13
 */
public class ChatListener implements Listener {
    
    private static final Pattern CAPS_PATTERN = Pattern.compile(".*[A-Z]{4}.*");
    //private static final Pattern URL_PATTERN = Pattern.compile("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$");
    private static final String GOGOGADGET = "go go gadget, ";
    
    @EventHandler
    public void onChatMessageSend(AsyncPlayerChatEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());

        if (e.getMessage().length() > GOGOGADGET.length() + 1 && e.getMessage().substring(0, GOGOGADGET.length()).equalsIgnoreCase(GOGOGADGET)) {
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                Bukkit.dispatchCommand(e.getPlayer(), e.getMessage().substring(GOGOGADGET.length()));
            });
        }

        Chat.sendMessage(cp, e.getMessage());

        CoreLogger.logInfo("<" + cp.getPlayer().getName() + "> " + e.getMessage());
        
        e.setCancelled(true);
    }
}
