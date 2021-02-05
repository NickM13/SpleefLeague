/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.CorePlayerArg;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.player.rank.CoreRankManager;
import org.bukkit.command.CommandSender;
import com.spleefleague.core.command.annotation.OptionArg;

/**
 * @author NickM13
 */
public class SetRankCommand extends CoreCommand {
    
    public SetRankCommand() {
        super("setrank", CoreRank.DEVELOPER);
        setUsage("/setrank [name] <rank>");
        setOptions("rankList", (cp) -> Core.getInstance().getRankManager().getRankNames());
    }
    
    private boolean sr(CorePlayer sender, CorePlayer cp, CoreRank rank) {
        if (cp == null) {
            error(sender, CoreError.PLAYER);
            return true;
        }
        if (rank == null) {
            error(sender, CoreError.RANK);
            return true;
        }
        if (!sender.equals(cp)) {
            success(sender, Chat.DEFAULT + cp.getDisplayName() + Chat.DEFAULT + "'s rank has been set to " + rank.getDisplayName());
        }
        success(cp, Chat.DEFAULT + "Your rank has been set to " + rank.getDisplayName());
        cp.setRank(rank);
        return true;
    }
    
    private boolean sr(CommandSender sender, @CorePlayerArg(allowOffline = true) CorePlayer cp, CoreRank rank) {
        if (cp == null) {
            error(sender, "Player does not exist");
            return true;
        }
        if (rank == null) {
            error(sender, "Rank does not exist");
            return true;
        }
        success(sender, cp.getDisplayName() + Chat.DEFAULT + "'s rank has been set to " + rank.getDisplayName());
        success(cp, Chat.DEFAULT + "Your rank has been set to " + rank.getDisplayName());
        cp.setRank(rank);
        return true;
    }
    
    @CommandAnnotation
    public void setrankPlayer(CorePlayer sender,
                              @CorePlayerArg(allowOffline = true) CorePlayer cp,
                              @OptionArg(listName="rankList") String rank) {
        sr(sender, cp, Core.getInstance().getRankManager().getRank(rank));
    }
    
    @CommandAnnotation
    public void setrankConsole(CommandSender sender,
                               @CorePlayerArg(allowOffline = true) CorePlayer cp,
                               @OptionArg(listName="rankList") String rank) {
        sr(sender, cp, Core.getInstance().getRankManager().getRank(rank));
    }
    
}
