/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.plugin.CorePlugin;

import java.util.Random;

/**
 * @author NickM13
 */
public class SpectateCommand extends CoreCommand {
    
    public SpectateCommand() {
        super("spectate", Rank.DEFAULT);
        setUsage("/spectate <player>");
        setDescription("Spectate a player's match");
        this.setOptions("ingamers", cp -> CorePlugin.getIngamePlayerNames());
    }

    @CommandAnnotation
    public void spectate(CorePlayer sender) {
        if (sender.isInBattle() && sender.getBattleState() == BattleState.SPECTATOR) {
            sender.getBattle().leavePlayer(sender);
            success(sender, "You are no longer spectating");
        } else {
            if (CorePlugin.getIngamePlayerNames().isEmpty()) {
                error(sender, "There are no active games to spectate!");
                return;
            }
            int r = new Random().nextInt(CorePlugin.getIngamePlayerNames().size());
            int i = 0;
            for (String name : CorePlugin.getIngamePlayerNames()) {
                if (i == r) {
                    spectate(sender, name);
                    return;
                }
                i++;
            }
            error(sender, "There are no active games to spectate!");
        }
    }
    
    @CommandAnnotation
    public void spectate(CorePlayer sender,
            @OptionArg(listName="ingamers") String targetName) {
        CorePlayer target = Core.getInstance().getPlayers().get(targetName);
        if (!sender.canJoinBattle()) {
            error(sender, CoreError.INGAME);
        } else {
            if (sender.isInBattle()) {
                sender.getBattle().leavePlayer(sender);
            }
            if (CorePlugin.spectatePlayerGlobal(sender, target)) {
                success(sender, "You are now spectating " + target.getDisplayName() + "'s game");
            } else {
                error(sender, target.getDisplayName() + Chat.ERROR + " is not in a spectatable game");
            }
        }
    }

}
