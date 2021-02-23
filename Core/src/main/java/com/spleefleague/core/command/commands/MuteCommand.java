/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.infraction.Infractions;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.TimeUtils;

import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class MuteCommand extends CoreCommand {

    public MuteCommand() {
        super("mute", CoreRank.TEMP_MOD);
    }

    @CommandAnnotation
    public void mutePublic(CorePlayer sender,
                           @LiteralArg(value = "public") String l,
                           OfflinePlayer op,
                           @HelperArg("time") String time,
                           @Nullable String reason) {
        System.out.println("Core Player");
        Infractions.mutePublic(sender, op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }

    /*
    @CommandAnnotation
    public void mutePublic(CommandSender sender,
                           @LiteralArg(value = "public") String l,
                           OfflinePlayer op,
                           @HelperArg("time") String time,
                           @Nullable String reason) {
        System.out.println("Command sender");
        Infractions.mutePublic(null, op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
     */

    /**
     * Secret mute doesn't let the player know that
     * they're muted (their messages appear to still
     * send for them)
     *
     * @param sender Sender
     * @param l      "secret"
     * @param op     Offline Player
     * @param time   Time String
     * @param reason Reason
     * @see TimeUtils
     */
    @CommandAnnotation
    public void muteSecret(CorePlayer sender,
                           @LiteralArg(value = "secret") String l,
                           OfflinePlayer op,
                           @HelperArg("time") String time,
                           @Nullable String reason) {
        Infractions.muteSecret(sender, op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }

    /*
    @CommandAnnotation
    public void muteSecret(CommandSender sender,
                           @LiteralArg(value = "secret") String l,
                           OfflinePlayer op,
                           @HelperArg("time") String time,
                           @Nullable String reason) {
        Infractions.muteSecret(null, op, TimeUtils.toMillis(time), reason == null ? "" : reason);
    }
    */

}
