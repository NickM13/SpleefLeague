/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.settings.Settings;
import com.spleefleague.coreapi.chat.ChatColor;

/**
 * @author NickM13
 */
public class DiscordCommand extends CoreCommand {

    public DiscordCommand() {
        super("discord", Rank.DEFAULT);
        setUsage("/discord");
    }

    @CommandAnnotation
    public void discord(CorePlayer sender) {
        success(sender, "Join us on discord: " + ChatColor.BLUE + Settings.getDiscord().getUrl());
    }

    @CommandAnnotation(minRank = "DEVELOPER")
    public void discord(CorePlayer sender,
                        @LiteralArg("set") String l,
                        @HelperArg("<url>") String url) {
        Settings.setDiscord(url);
        success(sender, "Set discord link to " + url);
    }

}
