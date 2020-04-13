/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.Warp;
import org.bukkit.Bukkit;
import com.spleefleague.core.command.OptionArg;

/**
 * @author NickM13
 */
public class WarpCommand extends CommandTemplate {

    public WarpCommand() {
        super(WarpCommand.class, "warp", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/warp [name]");
        setOptions("warpList", (cp) -> Warp.getWarpNames(cp));
    }
    
    private void printWarps(CorePlayer sender) {
        sender.sendMessage(Chat.fillTitle("[ List of Warps ]"));
        sender.sendMessage(Warp.getWarpsFormatted(sender.getRank()));
    }
    
    @CommandAnnotation
    public void warp(CorePlayer cp, @OptionArg(listName="warpList") String warpName) {
        Warp warp;
        if ((warp = Warp.getWarp(warpName)) != null) {
            if (!Bukkit.getServer().getWorlds().contains(warp.getLocation().getWorld())) {
                error(cp, CoreError.WORLD);
            } else {
                cp.teleport(warp.getLocation());
                success(cp, "You have been warped to " + warp.getName());
            }
        } else {
            error(cp, "Warp does not exist!");
        }
    }
    
    @CommandAnnotation
    public void warp(CorePlayer sender) {
        printWarps(sender);
    }

}
