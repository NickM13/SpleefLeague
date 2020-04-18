/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.TimeUtils;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class MuteCommand extends CommandTemplate {
    
    public MuteCommand() {
        super(MuteCommand.class, "mute", Rank.MODERATOR);
    }
    
    @CommandAnnotation
    public void mutePublic(CorePlayer sender,
            @LiteralArg(value="public") String l,
            OfflinePlayer op,
            String time,
            @Nullable String reason) {
        Core.getInstance().mutePublic(sender.getName(), op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
    @CommandAnnotation
    public void mutePublic(CommandSender sender,
            @LiteralArg(value="public") String l,
            OfflinePlayer op,
            String time,
            @Nullable String reason) {
        Core.getInstance().mutePublic(sender.getName(), op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
    
    /**
     * Secret mute doesn't let the player know that
     * they're muted (their messages appear to still
     * send for them)
     * 
     * @param sender
     * @param l
     * @param op
     * @param time
     * @param reason
     */
    
    @CommandAnnotation
    public void muteSecret(CorePlayer sender,
            @LiteralArg(value="secret") String l,
            OfflinePlayer op,
            String time,
            @Nullable String reason) {
        Core.getInstance().muteSecret(sender.getName(), op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
    @CommandAnnotation
    public void muteSecret(CommandSender sender,
            @LiteralArg(value="secret") String l,
            OfflinePlayer op,
            String time,
            @Nullable String reason) {
        Core.getInstance().muteSecret(sender.getName(), op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
    
}
