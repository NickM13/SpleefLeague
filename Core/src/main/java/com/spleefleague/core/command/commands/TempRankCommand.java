/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.mysql.jdbc.TimeUtil;
import com.spleefleague.core.Core;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.player.rank.CoreRankManager;
import com.spleefleague.core.util.TimeUtils;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class TempRankCommand extends CoreCommand {
    
    public TempRankCommand() {
        super("temprank", CoreRank.DEVELOPER);
        setDescription("Give a temporary rank to a player");
        setOptions("rankList", (cp) -> Core.getInstance().getRankManager().getRankNames());
    }
    
    @CommandAnnotation
    public void temprankAdd(CommandSender sender,
            @LiteralArg(value="add") String l,
            CorePlayer cp,
            @OptionArg(listName="rankList") String rank,
            @HelperArg(value="time") String time) {
        Long millis = TimeUtils.toMillis(time);
        if (millis == null) {
            error(sender, "Time not valid");
            return;
        }
        if (cp.addTempRank(rank, millis)) {
            success(sender, "Added temp rank " + rank + " to " + cp.getDisplayName() + " for " + TimeUtils.timeToString(millis));
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
