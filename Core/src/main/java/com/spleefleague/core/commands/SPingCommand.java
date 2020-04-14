/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import java.util.List;
import net.md_5.bungee.api.ChatColor;

/**
 * @author NickM13
 */
public class SPingCommand extends CommandTemplate {
    
    public SPingCommand() {
        super(SPingCommand.class, "sping", Rank.DEFAULT);
        setDescription("Get all player pings");
    }
    
    @CommandAnnotation
    public void sping(CorePlayer sender) {
        List<CorePlayer> players = Lists.newArrayList(Core.getInstance().getPlayers().getOnline());
        players.sort((cp1, cp2) -> {
            return cp1.getPing() - cp2.getPing();
        });
        
        sender.sendMessage(Chat.fillTitle(ChatColor.AQUA + "[" + ChatColor.GOLD + " Everyone's Ping " + ChatColor.AQUA + "]"));
        for (CorePlayer cp : players) {
            sender.sendMessage(cp.getPingFormatted() + Chat.DEFAULT + " >> " + cp.getDisplayName());
        }
    }

}
