/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.NumberArg;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.CoreUtils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author NickM13
 */
public class GameModeCommand extends CoreCommand {

    public GameModeCommand() {
        super("gamemode", CoreRank.TEMP_MOD);
        setOptions("gamemodes", cp -> CoreUtils.enumToStrSet(GameMode.class, true));
        addAlias("gm");
        setUsage("/gm <0-3> [player]");
        setDescription("Set a player's GameMode");
    }
    
    private GameMode getGameMode(int gm) {
        switch (gm) {
            case 0: return GameMode.SURVIVAL;
            case 1: return GameMode.CREATIVE;
            case 2: return GameMode.ADVENTURE;
            case 3: return GameMode.SPECTATOR;
            default: return null;
        }
    }

    private void setGameMode(CorePlayer target, GameMode mode) {
        target.setGameMode(mode);
        success(target, "Your GameMode has been set to " + mode.toString().toLowerCase());
    }

    @CommandAnnotation
    public void gamemodeSingle(CorePlayer sender,
                               @NumberArg(minValue = 0, maxValue = 3) Integer i) {
        setGameMode(sender, getGameMode(i));
    }

    @CommandAnnotation
    public void gamemodeSingle(CorePlayer sender,
                               @OptionArg(listName = "gamemodes") String modeName) {
        setGameMode(sender, GameMode.valueOf(modeName.toUpperCase()));
    }

    @CommandAnnotation
    public void gamemodeSingle(CommandSender sender,
                               @NumberArg(minValue = 0, maxValue = 3) Integer i,
                               CorePlayer target) {
        GameMode mode = getGameMode(i);
        if (mode != null) {
            setGameMode(target, mode);
            success(sender, Chat.PLAYER_NAME + target.getDisplayNamePossessive() +
                    Chat.DEFAULT + " GameMode has been set to " + mode.toString().toLowerCase());
        }
    }

    @CommandAnnotation
    public void gamemodeSingle(CommandSender sender,
                               @OptionArg(listName = "gamemodes") String modeName,
                               CorePlayer target) {
        GameMode mode = GameMode.valueOf(modeName.toUpperCase());
        setGameMode(target, mode);
        success(sender, Chat.PLAYER_NAME + target.getDisplayNamePossessive() +
                Chat.DEFAULT + " GameMode has been set to " + mode.toString().toLowerCase());
    }

    @CommandAnnotation
    public void gamemodeMany(CommandSender sender,
                             @NumberArg(minValue = 0, maxValue = 3) Integer i,
                             List<CorePlayer> targets) {
        GameMode mode = getGameMode(i);
        for (CorePlayer cp : targets) {
            setGameMode(cp, mode);
        }
        success(sender, "Set GameMode to " + mode.toString().toLowerCase() + " for " + CoreUtils.mergePlayerNames(targets));
    }

    @CommandAnnotation
    public void gamemodeMany(CommandSender sender,
                             @OptionArg(listName = "gamemodes") String modeName,
                             List<CorePlayer> targets) {
        GameMode mode = GameMode.valueOf(modeName.toUpperCase());
        for (CorePlayer cp : targets) {
            setGameMode(cp, mode);
        }
        success(sender, "Set GameMode to " + mode.toString().toLowerCase() + " for " + CoreUtils.mergePlayerNames(targets));
    }

}
