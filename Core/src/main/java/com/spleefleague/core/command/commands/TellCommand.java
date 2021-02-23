/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.infraction.Infraction;
import com.spleefleague.coreapi.infraction.InfractionType;

/**
 * @author NickM13
 */
public class TellCommand extends CoreCommand {

    public TellCommand() {
        super("tell", CoreRank.DEFAULT);
        addAlias("msg");
        addAlias("whisper");
    }

    @CommandAnnotation
    public void tell(CorePlayer sender,
                     @CorePlayerArg(allowCrossServer = true) CorePlayer target,
                     String msg) {
        if (sender.isMuted()) {
            Core.getInstance().sendMessage(sender, "You're muted!");
            return;
        }
        Chat.sendTell(sender, target, msg);
    }

}
