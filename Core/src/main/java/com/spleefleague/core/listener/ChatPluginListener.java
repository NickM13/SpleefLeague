package com.spleefleague.core.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class ChatPluginListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equalsIgnoreCase("slcore:chat")) {
            Chat.sendMessage(bytes);
        } else if (channel.equalsIgnoreCase("slcore:tell")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            CorePlayer sender = Core.getInstance().getPlayers().get(UUID.fromString(input.readUTF()));
            CorePlayer target = Core.getInstance().getPlayers().get(UUID.fromString(input.readUTF()));
            String msg = input.readUTF();
            Core.getInstance().receiveTell(sender, target, msg);
        }
    }

}
