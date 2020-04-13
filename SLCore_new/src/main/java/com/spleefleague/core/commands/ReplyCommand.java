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
import org.bukkit.entity.Player;

/**
 * @author NickM13
 */
public class ReplyCommand extends CommandTemplate {
    
    public ReplyCommand() {
        super(ReplyCommand.class, "reply", Rank.DEFAULT);
        addAlias("r");
        setUsage("/reply <message>");
    }
    
    @CommandAnnotation
    public void reply(CorePlayer sender, String message) {
        Player player = sender.getReply();
        if (player == null) {
            error(sender, "No player to reply to!");
        }
        CorePlayer receiver = Core.getInstance().getPlayers().get(player);
        if (receiver == null) {
            error(sender, "Issue replying to player!");
        } else if (!receiver.isOnline()) {
            error(sender, receiver.getDisplayName() + " is offline!");
        } else {
            Core.getInstance().sendTell(sender, receiver, message);
        }
    }

}
