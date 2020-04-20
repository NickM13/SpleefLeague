/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.game.SpleggMode;

import javax.annotation.Nullable;

/**
 * @author NickM13
 */
public class SpleggCommand extends CommandTemplate {

    public SpleggCommand() {
        super(SpleggCommand.class, "splegg", Rank.DEFAULT);
        this.setOptions("classicArenas", (cp) -> Arena.getArenaNames(SpleggMode.CLASSIC.getArenaMode()));
        this.setOptions("multiArenas", (cp) -> Arena.getArenaNames(SpleggMode.MULTI.getArenaMode()));
    }

    public void splegg(CorePlayer sender) {

    }

    @CommandAnnotation(minRank="DEVELOPER")
    public void spleefDebug(CorePlayer sender,
                            @LiteralArg("debug") String l) {
        Splegg.getInstance().getBattleManager(SpleggMode.CLASSIC.getArenaMode()).startMatch(Lists.newArrayList(sender, sender), "temple");
    }

    @CommandAnnotation
    public void spleggClassic(CorePlayer sender, @LiteralArg("classic") String l, @Nullable @OptionArg(listName="classicArenas") String arena) {
        Splegg.getInstance().queuePlayer(SpleggMode.CLASSIC.getArenaMode(), sender, Arena.getByName(arena, SpleggMode.CLASSIC.getArenaMode()));
    }

    @CommandAnnotation
    public void spleggMulti(CorePlayer sender, @LiteralArg("multi") String l, @Nullable @OptionArg(listName="multiArenas") String arena) {
        Splegg.getInstance().queuePlayer(SpleggMode.MULTI.getArenaMode(), sender, Arena.getByName(arena, SpleggMode.MULTI.getArenaMode()));
    }

}
