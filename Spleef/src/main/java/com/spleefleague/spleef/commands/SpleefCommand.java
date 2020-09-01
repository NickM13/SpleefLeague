/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author NickM13
 */
public class SpleefCommand extends CoreCommand {
    
    public SpleefCommand() {
        super("spleef", Rank.DEFAULT);
        this.addAlias("s");
        this.setOptions("classicArenas",    cp -> Arenas.getUnpaused(SpleefMode.CLASSIC.getBattleMode()).keySet());
        this.setOptions("multiArenas",      cp -> Arenas.getUnpaused(SpleefMode.MULTI.getBattleMode()).keySet());
        this.setOptions("powerArenas",      cp -> Arenas.getUnpaused(SpleefMode.POWER.getBattleMode()).keySet());
        this.setOptions("teamArenas",       cp -> Arenas.getUnpaused(SpleefMode.TEAM.getBattleMode()).keySet());
        this.setOptions("wcArenas",         cp -> Arenas.getUnpaused(SpleefMode.WC.getBattleMode()).keySet());
        this.setOptions("modeNames",        cp -> modeNames());
        setContainer("spleef");
    }

    private static Set<String> modeNames() {
        Set<String> names = new HashSet<>();
        for (SpleefMode mode : SpleefMode.values()) {
            names.add(mode.getName());
        }
        return names;
    }
    
    @CommandAnnotation(minRank = "DEVELOPER")
    public void spleefMatch(CorePlayer sender,
            @LiteralArg("m") String l,
            @OptionArg(listName="modeNames") String spleefMode,
            @HelperArg("<arena>") String arenaName,
            @HelperArg("<players>") String playerNames) {
        BattleMode mode;
        try {
            mode = SpleefMode.valueOf(spleefMode.toUpperCase()).getBattleMode();
        } catch(IllegalArgumentException exception) {
            error(sender, "Not a valid spleef mode!");
            return;
        }
        if (mode.getTeamStyle() == BattleMode.TeamStyle.TEAM) {
        
        }
        List<CorePlayer> players = new ArrayList<>();
        for (String playerName : playerNames.split(" ")) {
            CorePlayer cp = Core.getInstance().getPlayers().get(playerName);
            if (cp != null) {
                players.add(cp);
            } else {
                error(sender, playerName + " is not online!");
                return;
            }
        }
        if (Arenas.get(arenaName, mode) != null) {
            Spleef.getInstance().getBattleManager(mode).startMatch(players, arenaName);
        } else {
            error(sender, arenaName + " is not a valid arena for " + mode.getDisplayName() + "!");
        }
    }
    
    @CommandAnnotation
    public void spleef(CorePlayer sender) {
        sender.setInventoryMenuItem(Spleef.getInstance().getSpleefMenu());
    }
    
    @CommandAnnotation
    public void spleefClassic(CorePlayer sender,
            @LiteralArg("classic") String l,
            @Nullable @OptionArg(listName="classicArenas") String arena) {
        Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getBattleMode(), sender, Arenas.get(arena, SpleefMode.CLASSIC.getBattleMode()));
    }
    
    @CommandAnnotation
    public void spleefMulti(CorePlayer sender,
            @LiteralArg("multi") String l) {
        Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getBattleMode(), sender);
    }
    
    @CommandAnnotation
    public void spleefPower(CorePlayer sender,
            @LiteralArg("power") String l,
            @Nullable @OptionArg(listName="powerArenas") String arenaName) {
        Spleef.getInstance().queuePlayer(SpleefMode.POWER.getBattleMode(), sender, Arenas.get(arenaName, SpleefMode.POWER.getBattleMode()));
    }
    
    @CommandAnnotation
    public void spleefTeam(CorePlayer sender,
            @LiteralArg("team") String l,
            @Nullable @OptionArg(listName="teamArenas") String arenaName) {
        Spleef.getInstance().queuePlayer(SpleefMode.TEAM.getBattleMode(), sender, Arenas.get(arenaName, SpleefMode.TEAM.getBattleMode()));
    }
    
    @CommandAnnotation(hidden=true)
    public void spleefWc(CorePlayer sender,
            @LiteralArg("wc") String l,
            @Nullable @OptionArg(listName="wcArenas") String arenaName) {
        error(sender, CoreError.SETUP);
    }
    
}
