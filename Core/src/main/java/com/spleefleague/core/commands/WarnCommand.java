/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class WarnCommand extends CommandTemplate {
    
    public WarnCommand() {
        super(WarnCommand.class, "warn", Rank.MODERATOR);
    }
    
    @CommandAnnotation
    public void warn(CorePlayer sender, OfflinePlayer op, String reason) {
        Core.getInstance().warn(sender.getName(), op, reason);
    }
    @CommandAnnotation
    public void warn(CorePlayer sender, OfflinePlayer op) {
        Core.getInstance().warn(sender.getName(), op, "");
    }
    @CommandAnnotation
    public void warn(CommandSender sender, OfflinePlayer op, String reason) {
        Core.getInstance().warn(sender.getName(), op, reason);
    }
    @CommandAnnotation
    public void warn(CommandSender sender, OfflinePlayer op) {
        Core.getInstance().warn(sender.getName(), op, "");
    }
    
}
