/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class TeleportCommand extends CoreCommand {

    public TeleportCommand() {
        super("tp", CoreRank.TEMP_MOD);
        setUsage("/tp <player> [player2]");
    }

    @CommandAnnotation
    public void tp(CorePlayer sender, CorePlayer cp) {
        sender.teleport(cp.getLocation());
        TextComponent text = new TextComponent("Teleported to ");
        text.addExtra(cp.getChatName());
        success(sender, text);
    }

    @CommandAnnotation
    public void tp(CommandSender sender, CorePlayer cp1, CorePlayer cp2) {
        cp1.teleport(cp2.getLocation());
        success(sender, "Teleported " + cp1.getDisplayName() + " to " + cp2.getDisplayName());
        TextComponent text = new TextComponent("Teleported to ");
        text.addExtra(cp2.getChatName());
        success(cp1, text);
    }

}
