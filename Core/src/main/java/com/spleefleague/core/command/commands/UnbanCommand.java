/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class UnbanCommand extends CoreCommand {
    
    public UnbanCommand() {
        super("unban", Rank.MODERATOR);
    }
    
    @CommandAnnotation
    public void unban(CorePlayer sender, OfflinePlayer op, String reason) {
        Core.getInstance().unban(sender.getName(), op, reason);
    }
    @CommandAnnotation
    public void unban(CorePlayer sender, OfflinePlayer op) {
        Core.getInstance().unban(sender.getName(), op, "");
    }
    @CommandAnnotation
    public void unban(CommandSender sender, OfflinePlayer op, String reason) {
        Core.getInstance().unban(sender.getName(), op, reason);
    }
    @CommandAnnotation
    public void unban(CommandSender sender, OfflinePlayer op) {
        Core.getInstance().unban(sender.getName(), op, "");
    }
    
}
