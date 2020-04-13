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
public class SetWarpCommand extends CommandTemplate {

    public SetWarpCommand() {
        super(SetWarpCommand.class, "setwarp", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/setwarp <name>");
        setDescription("Set a warp");
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
