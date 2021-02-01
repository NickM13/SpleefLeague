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
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.settings.Settings;
import com.spleefleague.coreapi.chat.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author NickM13
 */
public class DiscordCommand extends CoreCommand {

    public DiscordCommand() {
        super("discord", CoreRank.DEFAULT);
        setUsage("/discord");
    }

    @CommandAnnotation
    public void discord(CorePlayer sender) {
        TextComponent text = new TextComponent("Join us on discord: ");
        text.addExtra(new ComponentBuilder(ChatColor.BLUE + Settings.getDiscord().getUrl())
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, Settings.getDiscord().getUrl()))
                .create()[0]);
        success(sender, text);
    }

    @CommandAnnotation(minRank = "DEVELOPER")
    public void discord(CorePlayer sender,
                        @LiteralArg("set") String l,
                        @HelperArg("<url>") String url) {
        Settings.setDiscord(url);
        success(sender, "Set discord link to " + url);
    }

}
