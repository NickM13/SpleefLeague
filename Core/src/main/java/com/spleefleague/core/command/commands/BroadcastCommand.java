/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class BroadcastCommand extends CoreCommand {
    
    public BroadcastCommand() {
        super("broadcast", Rank.DEVELOPER);
        setUsage("/broadcast <message>");
        setDescription("Send a message through the broadcast channel");
    }
    
    @CommandAnnotation
    public void broadcast(CorePlayer sender, @HelperArg(value="<title/subtitle>") String message) {
        Chat.broadcast(message);
    }
    
    @CommandAnnotation
    public void broadcast(CorePlayer sender,
            @HelperArg(value="<fadeIn>") Integer fadeIn,
            @HelperArg(value="<stay>") Integer stay,
            @HelperArg(value="<fadeOut>") Integer fadeOut,
            @HelperArg(value="<title/subtitle>") String message) {
        Chat.broadcast(message);
    }

}
