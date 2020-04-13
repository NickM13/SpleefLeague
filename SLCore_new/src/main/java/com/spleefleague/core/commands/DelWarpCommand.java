/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.Warp;

/**
 * @author NickM13
 */
public class DelWarpCommand extends CommandTemplate {

    public DelWarpCommand() {
        super(DelWarpCommand.class, "delwarp", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/delwarp <warp>");
        setDescription("Delete a warp");
    }
    
    @CommandAnnotation
    public void delwarp(CorePlayer sender, String warp) {
        if (Warp.delWarp(warp)) {
            success(sender, "You have deleted a warp: " + Chat.INFO + warp);
        } else {
            error(sender, "Warp does not exist, try /warps");
        }
    }
    
}
