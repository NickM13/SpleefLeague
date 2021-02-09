/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.infraction.Infractions;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class WarnCommand extends CoreCommand {
    
    public WarnCommand() {
        super("warn", CoreRank.TEMP_MOD);
    }
    
    @CommandAnnotation
    public void warn(CorePlayer sender, OfflinePlayer op, String reason) {
        Infractions.warn(sender, op, reason);
    }
    @CommandAnnotation
    public void warn(CorePlayer sender, OfflinePlayer op) {
        Infractions.warn(sender, op, "");
    }
    @CommandAnnotation
    public void warn(CommandSender sender, OfflinePlayer op, String reason) {
        Infractions.warn(null, op, reason);
    }
    @CommandAnnotation
    public void warn(CommandSender sender, OfflinePlayer op) {
        Infractions.warn(null, op, "");
    }
    
}
