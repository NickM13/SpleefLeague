/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import org.bukkit.entity.Player;

/**
 * @author NickM13
 */
public class ReplyCommand extends CoreCommand {
    
    public ReplyCommand() {
        super("reply", CoreRank.DEFAULT);
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
        } else if (receiver.getOnlineState() == DBPlayer.OnlineState.OFFLINE) {
            error(sender, receiver.getDisplayName() + " is offline!");
        } else {
            Chat.sendTell(sender, receiver, message);
        }
    }

}
