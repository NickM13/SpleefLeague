/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Warp;

/**
 * @author NickM13
 */
public class SetWarpCommand extends CoreCommand {

    public SetWarpCommand() {
        super("setwarp", CoreRank.TEMP_MOD, CoreRank.BUILDER);
        setUsage("/setwarp <name>");
        setDescription("Set a warp");
        setContainer("warp");
    }
    
    @CommandAnnotation
    public void setwarp(CorePlayer sender, String warp) {
        if (Warp.getWarp(warp) == null) {
            Warp.setWarp(warp, sender.getPlayer().getLocation());
            success(sender, "You have created a warp: " + Chat.INFO + warp);
        } else {
            error(sender, "Warp already exists");
        }
    }

}
