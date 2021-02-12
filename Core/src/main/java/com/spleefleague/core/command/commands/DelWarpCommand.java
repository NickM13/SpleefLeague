/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Warp;

/**
 * @author NickM13
 */
public class DelWarpCommand extends CoreCommand {

    public DelWarpCommand() {
        super("delwarp", CoreRank.TEMP_MOD, CoreRank.BUILDER);
        setUsage("/delwarp <warp>");
        setDescription("Delete a warp");
        setOptions("warpList", Warp::getWarpNames);
        setContainer("warp");
    }

    @CommandAnnotation
    public void delwarp(CorePlayer sender,
                        @OptionArg(listName = "warpList") String warpName) {
        if (Warp.delWarp(warpName)) {
            success(sender, "You have deleted a warp: " + Chat.INFO + warpName);
        } else {
            error(sender, "Warp does not exist, try /warps");
        }
    }

}
