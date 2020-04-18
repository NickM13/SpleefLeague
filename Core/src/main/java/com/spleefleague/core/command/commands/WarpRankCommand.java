/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.variable.Warp;

/**
 * @author NickM13
 */
public class WarpRankCommand extends CommandTemplate {
    
    public WarpRankCommand() {
        super(WarpRankCommand.class, "warprank", Rank.MODERATOR);
        setOptions("warpList", (cp) -> Warp.getWarpNames(cp));
        setOptions("rankList", (cp) -> Rank.getRankNames());
    }
    
    private void printWarps(CorePlayer sender, Rank rank) {
        sender.sendMessage(Chat.fillTitle("[ List of Warps: " + rank.getDisplayName() + " ]"));
        sender.sendMessage(Warp.getWarpsFormatted(rank));
    }
    
    @CommandAnnotation
    public void warpRankSet(CorePlayer sender,
            @LiteralArg(value="set") String l,
            @OptionArg(listName="rankList") String rankName,
            @OptionArg(listName="warpList") String warpName) {
        Warp warp = Warp.getWarp(warpName);
        Rank rank = Rank.getRank(rankName);
        warp.setMinRank(rank);
        success(sender, warp.getName() + "'s rank is now " + rank.getDisplayName());
    }
    
    @CommandAnnotation
    public void warpRankGet(CorePlayer sender,
            @LiteralArg(value="get") String l,
            @OptionArg(listName="warpList") String warpName) {
        Warp warp = Warp.getWarp(warpName);
        success(sender, warp.getName() + ": " + warp.getMinRank().getDisplayName());
    }
    
    @CommandAnnotation
    public void warpRankAvailable(CorePlayer sender,
            @LiteralArg(value="available") String l,
            @OptionArg(listName="rankList") String rankName) {
        Rank rank = Rank.getRank(rankName);
        printWarps(sender, rank);
    }
    
}
