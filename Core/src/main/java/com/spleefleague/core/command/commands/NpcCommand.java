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
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author NickM13
 */
public class NpcCommand extends CoreCommand {

    public NpcCommand() {
        super("npc", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void npc(CommandSender sender,
                    CorePlayer target,
                    String profile,
                    String name,
                    String message) {
        Chat.sendNpcMessage(target, profile, name, message);
    }

    @CommandAnnotation
    public void npc(CommandSender sender,
                    List<CorePlayer> targets,
                    String profile,
                    String name,
                    String message) {
        targets.forEach(target -> Chat.sendNpcMessage(target, profile, name, message));
    }

}
