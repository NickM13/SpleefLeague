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
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.player.rank.Ranks;
import org.bukkit.command.CommandSender;
import com.spleefleague.core.command.annotation.OptionArg;

/**
 * @author NickM13
 */
public class SetRankCommand extends CoreCommand {
    
    public SetRankCommand() {
        super("setrank", Rank.DEVELOPER);
        setUsage("/setrank [name] <rank>");
        setOptions("rankList", (cp) -> Ranks.getRankNames());
    }
    
    private boolean sr(CorePlayer sender, CorePlayer cp, Rank rank) {
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
    
    private boolean sr(CommandSender sender, @CorePlayerArg(allowOffline = true) CorePlayer cp, Rank rank) {
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
        if (cp.getPlayer() == null) {
            Core.getInstance().getPlayers().save(cp);
        }
        return true;
    }
    
    @CommandAnnotation
    public void setrankPlayer(CorePlayer sender, CorePlayer cp, @OptionArg(listName="rankList") String rank) {
        sr(sender, cp, Ranks.getRank(rank));
    }
    
    @CommandAnnotation
    public void setrankConsole(CommandSender sender, CorePlayer cp, @OptionArg(listName="rankList") String rank) {
        sr(sender, cp, Ranks.getRank(rank));
    }
    
}
