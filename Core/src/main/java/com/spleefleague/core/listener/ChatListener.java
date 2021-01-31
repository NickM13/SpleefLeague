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
import com.spleefleague.core.player.rank.Rank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
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

        String formattedMessage = e.getMessage();
        boolean url = false;
        Matcher urlMatcher = Chat.getUrlPattern().matcher(formattedMessage);
        String message = e.getMessage();
        for (int i = 0; i < message.length(); i++) {
            int pos = e.getMessage().indexOf(32, i);
            if (pos == -1) {
                pos = message.length();
            }
            if (urlMatcher.region(i, pos).find()) {
                url = true;
                break;
            }
        }
        if (url) {
            if (!cp.canSendUrl() && !cp.getRank().hasPermission(Rank.MODERATOR, Lists.newArrayList(Rank.BUILDER))) {
                e.setCancelled(true);
                Core.getInstance().sendMessage(cp, "Please ask for permission to send a link");
                Core.getInstance().sendMessage(ChatChannel.getChannel(ChatChannel.Channel.STAFF), cp.getPlayer().getName() + " tried to send a url: " + e.getMessage());
                CoreLogger.logInfo(cp.getPlayer().getName() + " tried to send a url: " + e.getMessage());
                return;
            }
        } else {
            if (CAPS_PATTERN.matcher(e.getMessage()).matches() &&
                    !cp.getRank().hasPermission(Rank.MODERATOR, Lists.newArrayList(Rank.BUILDER))) {
                formattedMessage = e.getMessage().toLowerCase().trim();
                formattedMessage = formattedMessage.substring(0, 1).toUpperCase() + formattedMessage.substring(1);
                if (!formattedMessage.endsWith(".")
                        && !formattedMessage.endsWith("!")
                        && !formattedMessage.endsWith("?")) {
                    formattedMessage += "!";
                }
            }
        }
        
        Chat.sendMessage(cp, formattedMessage, url);
        CoreLogger.logInfo("<" + cp.getPlayer().getName() + "> " + e.getMessage());
        
        e.setCancelled(true);
    }
}
