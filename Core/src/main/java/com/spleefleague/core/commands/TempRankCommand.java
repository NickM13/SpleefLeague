/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.HelperArg;
import com.spleefleague.core.command.LiteralArg;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class TempRankCommand extends CommandTemplate {
    
    public TempRankCommand() {
        super(TempRankCommand.class, "temprank", Rank.DEVELOPER);
        setUsage("/temprank <set/clear> <player> [rank] [hours]");
        setDescription("Give a temporary rank to a player");
        setOptions("rankList", (cp) -> Rank.getRankNames());
    }
    
    @CommandAnnotation
    public void temprankAdd(CommandSender sender,
            @LiteralArg(value="add") String l,
            CorePlayer cp,
            @OptionArg(listName="rankList") String rank,
            @HelperArg(value="<hours>") Integer hours) {
        if (cp.addTempRank(rank, hours)) {
            success(sender, "Temp rank added");
        } else {
            error(sender, "Rank not found");
        }
    }
    
    @CommandAnnotation
    public void temprankClear(CommandSender sender,
            @LiteralArg(value="clear") String l,
            CorePlayer cp) {
        cp.clearTempRank();
    }
}
