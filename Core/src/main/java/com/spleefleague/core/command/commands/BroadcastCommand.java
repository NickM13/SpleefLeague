/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatBroadcast;

/**
 * @author NickM13
 */
public class BroadcastCommand extends CoreCommand {

    public BroadcastCommand() {
        super("broadcast", CoreRank.DEVELOPER);
        setUsage("/broadcast <message>");
        setDescription("Send a message through the broadcast channel");
    }

    @CommandAnnotation
    public void broadcast(CorePlayer sender, @HelperArg(value = "<title \\n subtitle>") String message) {
        Core.getInstance().sendPacket(new PacketSpigotChatBroadcast(message));
    }

}
