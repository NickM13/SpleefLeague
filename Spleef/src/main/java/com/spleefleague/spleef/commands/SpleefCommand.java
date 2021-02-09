/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author NickM13
 */
public class SpleefCommand extends CoreCommand {
    
    public SpleefCommand() {
        super("spleef", CoreRank.DEFAULT);
        this.addAlias("s");
        this.setOptions("gamemodes",    pi -> CoreUtils.enumToStrSet(SpleefMode.class, true));
        this.setOptions("chalModes",    this::getChallengeModes);
        this.setOptions("arenas",       this::getArenas);
        setContainer("spleef");
    }

    protected SortedSet<String> getChallengeModes(PriorInfo pi) {
        SortedSet<String> available = new TreeSet<>();
        for (SpleefMode spleefMode : SpleefMode.values()) {
            if (spleefMode.getBattleMode().getTeamStyle() == BattleMode.TeamStyle.VERSUS) {
                available.add(spleefMode.name().toLowerCase());
            }
        }
        return available;
    }

    protected SortedSet<String> getArenas(PriorInfo pi) {
        BattleMode mode = SpleefMode.valueOf(pi.getArgs().get(pi.getArgs().size() - 1).toUpperCase()).getBattleMode();
        if (mode.getTeamStyle() == BattleMode.TeamStyle.TEAM ||
                mode.getTeamStyle() == BattleMode.TeamStyle.VERSUS ||
                mode.getTeamStyle() == BattleMode.TeamStyle.SOLO) {
            return Sets.newTreeSet(Arenas.getUnpaused(mode).keySet());
        }
        return new TreeSet<>();
    }

    @CommandAnnotation
    public void spleef(CorePlayer sender) {
        sender.getMenu().setInventoryMenuItem(Spleef.getInstance().getSpleefMenu());
    }

    @CommandAnnotation(minRank="DEVELOPER")
    public void spleefMatch(CorePlayer sender,
                            @LiteralArg("match") String l,
                            @OptionArg(listName = "gamemodes") String mode,
                            @OptionArg(listName = "arenas") String arenaName,
                            @HelperArg("<players>") String players) {
        List<CorePlayer> corePlayers = new ArrayList<>();
        for (String player : players.split(" ")) {
            CorePlayer cp = Core.getInstance().getPlayers().get(player);
            if (cp == null) {
                error(sender, "Player " + player + " not found");
                return;
            }
            corePlayers.add(cp);
        }
        BattleMode battleMode = SpleefMode.valueOf(mode.toUpperCase()).getBattleMode();
        Arena arena = Arenas.get(arenaName, battleMode);
        if (arena == null) {
            error(sender, "Arena not found!");
            return;
        }
        if (corePlayers.size() < battleMode.getRequiredTeams()) {
            error(sender, "Not enough players! (" + corePlayers.size() + "/" + battleMode.getRequiredTeams() + ")");
            Chat.sendRequest(sender, "spleefforce", (s, r) -> Spleef.getInstance().forceStart(battleMode, corePlayers, arena), "Force start anyway?");
        } else {
            Spleef.getInstance().forceStart(battleMode, corePlayers, arena);
        }
    }

    @CommandAnnotation
    public void spleefChallenge(CorePlayer sender,
                                @LiteralArg("challenge") String l,
                                @OptionArg(listName = "chalModes") String mode,
                                @OptionArg(listName = "arenas") String arenaName,
                                @CorePlayerArg(allowCrossServer = true, allowSelf = false) CorePlayer target) {
        Spleef.getInstance().challengePlayer(sender, target, SpleefMode.valueOf(mode.toUpperCase()).getBattleMode(), arenaName);
    }

    @CommandAnnotation
    public void spleefQueue(CorePlayer sender,
                              @OptionArg(listName = "gamemodes") String mode,
                              @Nullable @OptionArg(listName="arenas") String arena) {
        BattleMode battleMode = SpleefMode.valueOf(mode.toUpperCase()).getBattleMode();
        Spleef.getInstance().queuePlayer(battleMode, sender, Arenas.get(arena, battleMode));
    }
    
}
