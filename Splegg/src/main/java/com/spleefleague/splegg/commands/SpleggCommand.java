/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.commands;

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
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author NickM13
 */
public class SpleggCommand extends CoreCommand {

    public SpleggCommand() {
        super("splegg", CoreRank.DEFAULT);
        this.addAlias("sg");
        this.setOptions("gamemodes", pi -> CoreUtils.enumToStrSet(SpleggMode.class, true));
        this.setOptions("arenas", this::getArenas);
        this.setContainer("splegg");
    }


    protected SortedSet<String> getArenas(PriorInfo pi) {
        BattleMode mode = SpleggMode.valueOf(pi.getArgs().get(pi.getArgs().size() - 1).toUpperCase()).getBattleMode();
        if (mode.getTeamStyle() == BattleMode.TeamStyle.TEAM ||
                mode.getTeamStyle() == BattleMode.TeamStyle.VERSUS ||
                mode.getTeamStyle() == BattleMode.TeamStyle.SOLO) {
            return Sets.newTreeSet(Arenas.getUnpaused(mode).keySet());
        }
        return new TreeSet<>();
    }

    @CommandAnnotation
    public void splegg(CorePlayer sender) {
        sender.getMenu().setInventoryMenuItem(Splegg.getInstance().getSpleggMenu());
    }

    @CommandAnnotation
    public void spleggChallenge(CorePlayer sender,
                                @LiteralArg("challenge") String l,
                                @LiteralArg("versus") String mode,
                                @OptionArg(listName = "arenas") String arenaName,
                                @CorePlayerArg(allowCrossServer = true, allowSelf = false) CorePlayer target) {
        Splegg.getInstance().challengePlayer(sender, target, SpleggMode.VERSUS.getBattleMode(), arenaName);
    }

    @CommandAnnotation(minRank="DEVELOPER")
    public void spleggMatch(CorePlayer sender,
                            @LiteralArg("match") String l,
                            @OptionArg(listName = "gamemodes") String mode,
                            @OptionArg(listName = "arenas") String arenaName,
                            @HelperArg("players") String players) {
        List<CorePlayer> corePlayers = new ArrayList<>();
        for (String player : players.split(" ")) {
            CorePlayer cp = Core.getInstance().getPlayers().get(player);
            if (cp == null) {
                error(sender, "Player " + player + " not found");
                return;
            }
            corePlayers.add(cp);
        }
        BattleMode battleMode = SpleggMode.valueOf(mode.toUpperCase()).getBattleMode();
        Arena arena = Arenas.get(arenaName, battleMode);
        if (arena == null) {
            error(sender, "Arena not found!");
            return;
        }
        if (corePlayers.size() < battleMode.getRequiredTeams()) {
            error(sender, "Not enough players! (" + corePlayers.size() + "/" + battleMode.getRequiredTeams() + ")");
            Chat.sendRequest(sender, "spleggforce", (s, r) -> Splegg.getInstance().forceStart(battleMode, corePlayers, arena), "Force start anyway?");
        } else {
            Splegg.getInstance().forceStart(battleMode, corePlayers, arena);
        }
    }

    @CommandAnnotation
    public void spleggQueue(CorePlayer sender,
                            @OptionArg(listName = "gamemodes") String mode,
                            @Nullable @OptionArg(listName = "arenas") String arena) {
        BattleMode battleMode = SpleggMode.valueOf(mode.toUpperCase()).getBattleMode();
        Splegg.getInstance().queuePlayer(battleMode, sender, Arenas.get(arena, battleMode));
    }

}
