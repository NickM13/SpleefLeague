package com.spleefleague.core.listener;

import com.spleefleague.core.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ChatPluginListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equalsIgnoreCase("slcore:chat")) {
            Chat.sendMessage(bytes);
        }
    }

}
