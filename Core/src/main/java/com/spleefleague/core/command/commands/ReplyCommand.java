/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.coreapi.utils.packet.spigot.chat.PacketSpigotChatTell;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 */
public class ReplyCommand extends CoreCommand {

    public ReplyCommand() {
        super("reply", CoreRank.DEFAULT);
        addAlias("r");
        setUsage("/reply <message>");
    }

    @CommandAnnotation
    public void reply(CorePlayer sender, String message) {
        if (sender.isMuted()) {
            Core.getInstance().sendMessage(sender, "You're muted!");
            return;
        }
        Core.getInstance().sendPacket(new PacketSpigotChatTell(sender.getUniqueId(), null, message));
    }

}
