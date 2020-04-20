/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.command.CommandSender;
import com.spleefleague.core.command.annotation.OptionArg;

/**
 * @author NickM13
 */
public class SetRankCommand extends CommandTemplate {
    
    public SetRankCommand() {
        super(SetRankCommand.class, "setrank", Rank.DEVELOPER);
        setUsage("/setrank [name] <rank>");
        setOptions("rankList", (cp) -> Rank.getRankNames());
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
    
    private boolean sr(CommandSender sender, CorePlayer cp, Rank rank) {
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
    public void setrank(CorePlayer sender, CorePlayer cp, @OptionArg(listName="rankList") String rank) {
        sr(sender, cp, Rank.getRank(rank));
    }
    
    @CommandAnnotation
    public void setrank(CommandSender sender, CorePlayer cp, @OptionArg(listName="rankList") String rank) {
        sr(sender, cp, Rank.getRank(rank));
    }
    
}
