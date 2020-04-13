/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class TpCommand extends CommandTemplate {
    
    public TpCommand() {
        super(TpCommand.class, "tp", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/tp <player> [player2]");
    }
    
    @CommandAnnotation
    public void tp(CorePlayer sender, CorePlayer cp) {
        sender.teleport(cp.getLocation());
        success(sender, "Teleported to " + cp.getDisplayName());
    }
    @CommandAnnotation
    public void tp(CommandSender sender, CorePlayer cp1, CorePlayer cp2) {
        cp1.teleport(cp2.getLocation());
        success(sender, "Teleported " + cp1.getDisplayName() + " to " + cp2.getDisplayName());
        success(cp1, "Teleported to " + cp2.getDisplayName());
    }
}
