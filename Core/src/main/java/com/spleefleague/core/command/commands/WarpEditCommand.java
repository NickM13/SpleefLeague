/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.menu.overlays.SLMainOverlay;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class WarpEditCommand extends CoreCommand {

    public WarpEditCommand() {
        super("warpedit", CoreRank.TEMP_MOD);
        setOptions("warpList", Warp::getWarpNames);
        setContainer("warp");
    }

    @CommandAnnotation
    public void warpSetItem(CorePlayer sender,
                            @OptionArg(listName = "warpList") String warpName,
                            @LiteralArg("set") String l1,
                            @LiteralArg("item") String l2,
                            @EnumArg Material material,
                            @HelperArg("customModelData") @NumberArg(minValue = 0) Integer customModelData) {
        Warp.getWarp(warpName).setDisplayItem(material, customModelData);
    }

}
