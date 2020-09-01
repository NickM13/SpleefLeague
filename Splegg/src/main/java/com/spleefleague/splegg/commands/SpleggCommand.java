/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;

import javax.annotation.Nullable;

/**
 * @author NickM13
 */
public class SpleggCommand extends CoreCommand {

    public SpleggCommand() {
        super("splegg", Rank.DEFAULT);
        this.setOptions("classicArenas", cp -> Arenas.getAll(SpleggMode.CLASSIC.getBattleMode()).keySet());
        this.setContainer("splegg");
    }

    public void splegg(CorePlayer sender) {

    }

    @CommandAnnotation(minRank="DEVELOPER")
    public void spleggDebug(CorePlayer sender,
                            @LiteralArg("debug") String l) {
        Splegg.getInstance().getBattleManager(SpleggMode.CLASSIC.getBattleMode()).startMatch(Lists.newArrayList(sender, sender), "temple");
    }

    @CommandAnnotation
    public void spleggClassic(CorePlayer sender, @LiteralArg("classic") String l, @Nullable @OptionArg(listName="classicArenas") String arena) {
        Splegg.getInstance().queuePlayer(SpleggMode.CLASSIC.getBattleMode(), sender, Arenas.get(arena, SpleggMode.CLASSIC.getBattleMode()));
    }

}