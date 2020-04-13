/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/**
 * Legacy command
 * 
 * @author NickM13
 */
public class StaffChatCommand extends CommandTemplate {
    
    public StaffChatCommand() {
        super(StaffChatCommand.class, "staffchat", Rank.MODERATOR);
        addAlias("sc");
        setUsage("/staffchat <message>");
    }
    
    @CommandAnnotation
    public void staffchat(CorePlayer sender,
            String message) {
        ChatChannel sc = ChatChannel.Channel.STAFF.getChatChannel();
        Chat.sendMessage(sc, sc.formatMessage(sender, message));
    }
    
}
