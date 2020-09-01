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
import com.spleefleague.core.util.TimeUtils;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class TempBanCommand extends CoreCommand {
    
    public TempBanCommand() {
        super("tempban", Rank.MODERATOR);
        setUsage("/tempban <player> <seconds> [reason]");
        setDescription("Temporarily ban a player from the server");
    }
    
    @CommandAnnotation
    public void tempban(CorePlayer sender,
            OfflinePlayer op,
            String time,
            @Nullable String reason) {
        Core.getInstance().tempban(sender.getName(), op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
    @CommandAnnotation
    public void tempban(CommandSender sender,
            OfflinePlayer op,
            String time,
            @Nullable String reason) {
        Core.getInstance().tempban(sender.getName(), op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
    
}