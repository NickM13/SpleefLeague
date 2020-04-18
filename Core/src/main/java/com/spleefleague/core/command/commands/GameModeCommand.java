/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.NumberArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.util.CoreUtils;
import org.bukkit.GameMode;

/**
 * @author NickM13
 */
public class GameModeCommand extends CommandTemplate {

    public GameModeCommand() {
        super(GameModeCommand.class, "gamemode", Rank.DEVELOPER, Rank.BUILDER);
        setOptions("gamemodes", cp -> CoreUtils.enumToSet(GameMode.class));
        addAlias("gm");
        setUsage("/gm [player] <0-3>");
        setDescription("Set player's gamemode");
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
    
    @CommandAnnotation
    public void gameMode(CorePlayer sender, @NumberArg(minValue=0, maxValue=3) Integer i) {
        GameMode gm = getGameMode(i);
        if (gm != null) {
            sender.setGameMode(gm);
            success(sender, "Your gamemode has been updated to " + gm.toString().toLowerCase());
        }
    }
    
    @CommandAnnotation
    public void gameMode(CorePlayer sender, CorePlayer cp, @NumberArg(minValue=0, maxValue=3) Integer i) {
        GameMode gm = getGameMode(i);
        if (gm != null) {
            cp.setGameMode(gm);
            if (!cp.equals(sender))
                success(sender, Chat.PLAYER_NAME + cp.getDisplayName() +
                        Chat.DEFAULT + "'s gamemode has been updated to " + gm.toString().toLowerCase());
            success(cp, "Your gamemode has been updated to " + gm.toString().toLowerCase());
        }
    }
    
    @CommandAnnotation
    public void gameMode(CorePlayer sender, @OptionArg(listName="gamemodes") String mode) {
        GameMode gm = GameMode.valueOf(mode);
        sender.setGameMode(gm);
        success(sender, "Your gamemode has been updated to " + gm.toString().toLowerCase());
    }
    
    @CommandAnnotation
    public void gameMode(CorePlayer sender, CorePlayer cp, @OptionArg(listName="gamemodes") String mode) {
        GameMode gm = GameMode.valueOf(mode);
        cp.setGameMode(gm);
        if (!cp.equals(sender))
            success(sender, Chat.PLAYER_NAME + cp.getDisplayName() +
                    Chat.DEFAULT + "'s gamemode has been updated to " + gm.toString().toLowerCase());
        success(cp, "Your gamemode has been updated to " + gm.toString().toLowerCase());
    }

}
