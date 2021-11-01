/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CoreOfflinePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

import java.util.regex.Pattern;

import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionType;
import net.md_5.bungee.api.chat.TextComponent;
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
    public void onChatMessageSend(AsyncPlayerChatEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());

        if (cp == null) {
            event.getPlayer().sendMessage("Error sending your message");
            event.setCancelled(true);
            return;
        }

        if (cp.isMuted()) {
            Infraction infraction = cp.getMute();
            if (infraction.getType() == InfractionType.MUTE_SECRET) {
                Chat.sendFakeMessage(cp, cp.getChatChannel(), event.getMessage());
            } else {
                Core.getInstance().sendMessage(cp, "You're muted!");
            }
            event.setCancelled(true);
            return;
        }

        for (char c : event.getMessage().toCharArray()) {
            if (c >= 20000 && c < 30000) {
                if (!cp.getRank().hasPermission(CoreRank.DEVELOPER)) {
                    Chat.sendMessageToPlayerError(cp, new TextComponent("You can't send that!"));

                    event.setCancelled(true);
                }
            }
        }

        if (event.getMessage().length() > GOGOGADGET.length() + 1 && event.getMessage().substring(0, GOGOGADGET.length()).equalsIgnoreCase(GOGOGADGET)) {
            Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                Bukkit.dispatchCommand(event.getPlayer(), event.getMessage().substring(GOGOGADGET.length()));
            });
        }

        Chat.sendMessage(cp, event.getMessage());

        CoreLogger.logInfo("<" + cp.getPlayer().getName() + "> " + event.getMessage());

        event.setCancelled(true);
    }
}
