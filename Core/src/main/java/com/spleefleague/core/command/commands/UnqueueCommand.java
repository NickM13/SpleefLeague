/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class UnqueueCommand extends CoreCommand {

    public UnqueueCommand() {
        super("unqueue", CoreRank.DEFAULT);
    }

    @CommandAnnotation
    public void unqueue(CorePlayer sender) {
        if (Core.getInstance().unqueuePlayerGlobally(sender)) {
            success(sender, "You have left all queues");
        } else {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF(sender.getIdentifier());
            sender.getPlayer().sendPluginMessage(Core.getInstance(), "queue:leaveall", output.toByteArray());
        }
    }

}
