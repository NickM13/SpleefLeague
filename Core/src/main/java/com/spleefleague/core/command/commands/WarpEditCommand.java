package com.spleefleague.core.command.commands;

import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.util.variable.Warp;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/20/2020
 */
public class WarpEditCommand extends CommandTemplate {
    
    public WarpEditCommand() {
        super(WarpEditCommand.class, "warpedit", Rank.MODERATOR, Rank.BUILDER);
        setOptions("warpList", Warp::getWarpNames);
        setOptions("folderList", (cp) -> Warp.getWarpFolders());
        setOptions("rankList", (cp) -> Rank.getRankNames());
        setOptions("materials", (cp) -> getMaterialNames());
        setContainer("warp");
    }
    
    private static Set<String> materialNameSet;
    private static Set<String> getMaterialNames() {
        if (materialNameSet == null) {
            materialNameSet = new HashSet<>();
            for (Material mat : Material.values()) {
                materialNameSet.add(mat.toString().toLowerCase());
            }
        }
        return materialNameSet;
    }
    
    /**
     * Appearance Commands
     */
    
    @CommandAnnotation
    public void warpeditDisplaySkull(CorePlayer sender,
            @LiteralArg("display") String m,
            @OptionArg(listName="warpList") String warpName,
            @HelperArg("<username>") String username) {
        Warp warp = Warp.getWarp(warpName);
        warp.setDisplayItem(username);
    }
    
    /**
     * Folder Commands
     */
    
    @CommandAnnotation
    public void warpeditFolderCreate(CorePlayer sender,
            @LiteralArg("folder") String f,
            @LiteralArg("create") String l,
            @HelperArg("<folder>") String folder) {
        Warp.createFolder(folder);
    }
    
    @CommandAnnotation
    public void warpeditFolderDelete(CorePlayer sender,
            @LiteralArg("folder") String f,
            @LiteralArg("delete") String l,
            @OptionArg(listName="folderList") String folder) {
        if (Warp.deleteFolder(folder)) {
            success(sender, "Deleted warpfolder " + folder + ", warps have been transferred");
        } else {
            error(sender, "Please don't do that");
        }
    }
    
    @CommandAnnotation
    public void warpeditFolderMove(CorePlayer sender,
            @LiteralArg("folder") String f,
            @LiteralArg("move") String l,
            @OptionArg(listName="warpList") String warp,
            @OptionArg(listName="folderList", force=false) String folder) {
        Warp.moveWarp(warp, folder);
    }
    
    @CommandAnnotation
    public void warpeditFolderList(CorePlayer sender,
            @LiteralArg("folder") String f,
            @LiteralArg("list") String l,
            @OptionArg(listName="folderList") String folder) {
        sender.sendMessage(ChatUtils.centerTitle("[ Warps: " + folder + " ]"));
        sender.getPlayer().spigot().sendMessage(Warp.getWarpsFormatted(folder));
    }
    
}
