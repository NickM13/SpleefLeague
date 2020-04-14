/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.Warp;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class WarpOtherCommand extends CommandTemplate {
    
    public WarpOtherCommand() {
        super(WarpOtherCommand.class, "warpother", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/warpother <player> <warp>");
        setOptions("warpList", (cp) -> Warp.getWarpNames(cp));
    }
    
    @CommandAnnotation
    public void warpOther(CommandSender cs,
            CorePlayer cp,
            @OptionArg(listName="warpList") String warpName) {
        Warp warp = Warp.getWarp(warpName);
        if (warp != null) {
            if (cp.warp(warp)) {
                success(cp, "You were warped to " + warp.getName());
                success(cs, "Warped " + cp.getDisplayName() + " to " + warp.getName());
            }
        }
    }
    
    @CommandAnnotation
    public void warpOther(CommandSender cs,
            List<CorePlayer> cps,
            @OptionArg(listName="warpList") String warpName) {
        Warp warp = Warp.getWarp(warpName);
        if (warp != null) {
            for (CorePlayer cp : cps) {
                if (cp.warp(warp)) {
                    success(cp, "You were warped to " + warp.getName());
                }
            }
            success(cs, "Warped others to " + warp.getName());
        }
    }
    
}
