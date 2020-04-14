/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.HelperArg;
import com.spleefleague.core.command.LiteralArg;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.Warp;
import com.spleefleague.core.command.OptionArg;

/**
 * @author NickM13
 */
public class WarpFolderCommand extends CommandTemplate {

    public WarpFolderCommand() {
        super(WarpFolderCommand.class, "warpfolder", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/warpfolder");
        setOptions("warpList", (cp) -> Warp.getWarpNames(cp));
        setOptions("folderList", (cp) -> Warp.getWarpFolders());
    }
    
    @CommandAnnotation
    public void warpfolderCreate(CorePlayer sender, @LiteralArg(value="create") String l, @HelperArg(value="<folder>") String folder) {
        Warp.createFolder(folder);
    }
    
    @CommandAnnotation
    public void warpfolderDelete(CorePlayer sender, @LiteralArg(value="delete") String l, @OptionArg(listName="folderList") String folder) {
        Warp.deleteFolder(folder);
    }
    
    @CommandAnnotation
    public void warpfolderMove(CorePlayer sender,
            @LiteralArg(value="move") String l,
            @OptionArg(listName="warpList") String warp,
            @OptionArg(listName="folderList", force=false) String folder) {
        Warp.moveWarp(warp, folder);
    }
    
    @CommandAnnotation
    public void warpfolderList(CorePlayer sender,
            @LiteralArg(value="list") String l,
            @OptionArg(listName="folderList") String folder) {
        sender.sendMessage(Chat.fillTitle("[ Warps: " + folder + " ]"));
        sender.getPlayer().spigot().sendMessage(Warp.getWarpsFormatted(folder));
    }
    
}
