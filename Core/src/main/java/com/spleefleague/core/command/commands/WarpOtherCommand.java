/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Warp;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 * @author NickM13
 */
public class WarpOtherCommand extends CoreCommand {
    
    public WarpOtherCommand() {
        super("warpother", CoreRank.TEMP_MOD, CoreRank.BUILDER);
        setUsage("/warpother <player> <warp>");
        setOptions("warpList", Warp::getWarpNames);
        setContainer("warp");
    }

    @CommandAnnotation
    public void warpOther(CorePlayer cs,
                          CorePlayer cp,
                          @OptionArg(listName="warpList") String warpName) {
        Warp warp = Warp.getWarp(warpName);
        if (warp != null) {
            if (cp.warp(warp)) {
                success(cp, "You were warped to " + warp.getIdentifier());
                success(cs, "Warped " + cp.getDisplayName() + " to " + warp.getIdentifier());
            } else {

            }
        }
    }

    @CommandAnnotation
    public void warpOther(CommandSender cs,
                          CorePlayer cp,
                          @OptionArg(listName="warpList") String warpName) {
        Warp warp = Warp.getWarp(warpName);
        if (warp != null) {
            if (cp.warp(warp)) {
                success(cp, "You were warped to " + warp.getIdentifier());
                success(cs, "Warped " + cp.getDisplayName() + " to " + warp.getIdentifier());
            } else {

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
                    success(cp, "You were warped to " + warp.getIdentifier());
                }
            }
            success(cs, "Warped others to " + warp.getIdentifier());
        }
    }
    
}
