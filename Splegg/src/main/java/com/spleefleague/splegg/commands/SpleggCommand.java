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
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

/**
 * @author NickM13
 */
public class SpleggCommand extends CoreCommand {

    public SpleggCommand() {
        super("splegg", Rank.DEFAULT);
        this.addAlias("sg");
        this.setOptions("gamemodes", pi -> CoreUtils.enumToStrSet(SpleggMode.class, true));
        this.setOptions("arenas", this::getArenas);
        this.setOptions("players", this::getPlayers);
        this.setContainer("splegg");
    }

    protected SortedSet<String> getArenas(PriorInfo pi) {
        String mode = pi.getArgs().get(pi.getArgs().size() - 1);
        return Sets.newTreeSet(Arenas.getUnpaused(SpleggMode.valueOf(mode.toUpperCase()).getBattleMode()).keySet());
    }

    protected SortedSet<String> getPlayers(PriorInfo pi) {
        SortedSet<String> players = Splegg.getInstance().getPlayers().getAllNames();
        for (int i = 2; i < pi.getArgs().size(); i++) {
            players.remove(pi.getArgs().get(i));
        }
        String mode = pi.getArgs().get(pi.getArgs().size() - 1);
        return Sets.newTreeSet(Arenas.getUnpaused(SpleggMode.valueOf(mode.toUpperCase()).getBattleMode()).keySet());
    }

    public void splegg(CorePlayer sender) {

    }

    @CommandAnnotation(minRank="DEVELOPER")
    public void spleggMatch(CorePlayer sender,
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
        BattleMode battleMode = SpleggMode.valueOf(mode.toUpperCase()).getBattleMode();
        Arena arena = Arenas.get(arenaName, battleMode);
        if (arena == null) {
            error(sender, "Arena not found!");
            return;
        }
        if (corePlayers.size() < battleMode.getRequiredTeams()) {
            error(sender, "Not enough players! (" + corePlayers.size() + "/" + battleMode.getRequiredTeams() + ")");
            Chat.sendRequest(sender, "spleggforce", (s, r) -> {
                Splegg.getInstance().forceStart(battleMode, corePlayers, arena);
            }, "Force start anyway?");
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
