/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.menu.overlays.SLMainOverlay;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Warp;
import org.bukkit.Bukkit;
import com.spleefleague.core.command.annotation.OptionArg;

/**
 * @author NickM13
 */
public class WarpCommand extends CoreCommand {

    public WarpCommand() {
        super("warp", CoreRank.TEMP_MOD);
        setUsage("/warp [name]");
        setOptions("warpList", Warp::getWarpNames);
        setContainer("warp");
    }

    @CommandAnnotation
    public void warp(CorePlayer cp,
                     @OptionArg(listName = "warpList") String warpName) {
        Warp warp;
        if ((warp = Warp.getWarp(warpName)) != null) {
            if (!Bukkit.getServer().getWorlds().contains(warp.getLocation().getWorld())) {
                error(cp, CoreError.WORLD);
            } else {
                cp.teleport(warp.getLocation());
                success(cp, "You have been warped to " + warp.getIdentifier());
            }
        } else {
            error(cp, "Warp does not exist!");
        }
    }

    @CommandAnnotation
    public void warp(CorePlayer sender) {
        sender.getMenu().setInventoryMenuChest(SLMainOverlay.getOverlay(), Warp.createMenuContainer(null), true);
    }

}
