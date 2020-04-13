/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.command.LiteralArg;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.party.Party;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.game.SpleggMode;
import com.spleefleague.spleef.player.SpleefPlayer;
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
            @LiteralArg(value="debug") String l) {
        Party.createParty(sender);
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().getBattleManager(SpleggMode.CLASSIC.getArenaMode()).startMatch(Lists.newArrayList(sp, sp), "temple");
    }
    
    @CommandAnnotation
    public void spleggClassic(CorePlayer sender, @LiteralArg(value="classic") String l, @Nullable @OptionArg(listName="classicArenas") String arena) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().queuePlayer(SpleggMode.CLASSIC.getArenaMode(), sp, Arena.getByName(arena, SpleggMode.CLASSIC.getArenaMode()));
    }
    
    @CommandAnnotation
    public void spleggMulti(CorePlayer sender, @LiteralArg(value="multi") String l, @Nullable @OptionArg(listName="multiArenas") String arena) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().queuePlayer(SpleggMode.MULTI.getArenaMode(), sp, Arena.getByName(arena, SpleggMode.MULTI.getArenaMode()));
    }
    
}
