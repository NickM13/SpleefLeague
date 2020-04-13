/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;

/**
 * @author NickM13
 */
public class SpectateCommand extends CommandTemplate {
    
    public SpectateCommand() {
        super(SpectateCommand.class, "spectate", Rank.DEFAULT);
        setUsage("/spectate <player>");
        setDescription("Spectate a player's match");
        this.setOptions("ingamers", cp -> CorePlugin.getIngamePlayersNames());
    }
    
    @CommandAnnotation
    public void spectate(CorePlayer sender,
            @OptionArg(listName="ingamers") String targetName) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(sender.getPlayer());
        CorePlayer target = Core.getInstance().getPlayers().get(targetName);
        if (dbp != null) {
            error(sender, CoreError.INGAME);
        } else {
            if (CorePlugin.spectatePlayerGlobal(sender.getPlayer(), target.getPlayer())) {
                success(sender, "You are now spectating " + target.getDisplayName() + "'s game");
            } else {
                error(sender, target.getDisplayName() + Chat.ERROR + " is not in a spectatable game");
            }
        }
    }

}
