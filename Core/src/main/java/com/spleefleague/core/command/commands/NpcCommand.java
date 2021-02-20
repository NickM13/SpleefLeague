/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class NpcCommand extends CoreCommand {

    public NpcCommand() {
        super("npc", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void npc(CorePlayer sender,
                    String profile,
                    String name,
                    String message) {
        Chat.sendNpcMessage(profile, name, message);
    }

}
