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
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class UnmuteCommand extends CommandTemplate {
    
    public UnmuteCommand() {
        super(UnmuteCommand.class, "unmute", Rank.MODERATOR);
    }
    
    @CommandAnnotation
    public void unmute(CorePlayer sender,
            OfflinePlayer op,
            @Nullable String reason) {
        Core.getInstance().unmute(sender.getName(), op, reason == null ? "" : reason);
    }
    @CommandAnnotation
    public void unmute(CommandSender sender,
            OfflinePlayer op,
            @Nullable String reason) {
        Core.getInstance().unmute(sender.getName(), op, reason == null ? "" : reason);
    }
    
}
