/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class UnmuteCommand extends CoreCommand {
    
    public UnmuteCommand() {
        super("unmute", Rank.MODERATOR);
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
