/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class BanCommand extends CommandTemplate {
    
    public BanCommand() {
        super(BanCommand.class, "ban", Rank.MODERATOR);
        setUsage("/ban <player> [reason]");
        setDescription("Ban a player from the server");
    }
    
    @CommandAnnotation
    public void ban(CorePlayer sender,
            OfflinePlayer op,
            @Nullable String reason) {
        Core.getInstance().ban(sender.getName(), op, reason == null ? "" : reason);
    }
    @CommandAnnotation
    public void ban(CommandSender sender,
            OfflinePlayer op,
            @Nullable String reason) {
        Core.getInstance().ban(sender.getName(), op, reason == null ? "" : reason);
    }
    
}
