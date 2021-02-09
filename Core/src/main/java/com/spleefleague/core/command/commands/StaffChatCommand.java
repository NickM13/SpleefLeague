/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * Legacy command
 * 
 * @author NickM13
 */
public class StaffChatCommand extends CoreCommand {
    
    public StaffChatCommand() {
        super("staffchat", CoreRank.TEMP_MOD);
        addAlias("sc");
        setUsage("/staffchat <message>");
    }
    
    @CommandAnnotation
    public void staffchat(CorePlayer sender,
            String message) {
        Chat.sendMessage(sender, ChatChannel.STAFF, message);
    }
    
}
