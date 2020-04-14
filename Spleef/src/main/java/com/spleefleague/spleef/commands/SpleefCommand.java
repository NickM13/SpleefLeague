/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.google.common.collect.Lists;
import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.LiteralArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.error.CoreError;
import com.spleefleague.core.game.Arena;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.SpleefMode;
import com.spleefleague.spleef.player.SpleefPlayer;
import javax.annotation.Nullable;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.party.Party;

/**
 * @author NickM13
 */
public class SpleefCommand extends CommandTemplate {
    
    public SpleefCommand() {
        super(SpleefCommand.class, "spleef", Rank.DEFAULT);
        this.addAlias("s");
        this.setOptions("classicArenas", (cp) -> Arena.getArenaNames(SpleefMode.CLASSIC.getArenaMode()));
        this.setOptions("multiArenas", (cp) -> Arena.getArenaNames(SpleefMode.MULTI.getArenaMode()));
        this.setOptions("powerArenas", (cp) -> Arena.getArenaNames(SpleefMode.POWER.getArenaMode()));
        this.setOptions("teamArenas", (cp) -> Arena.getArenaNames(SpleefMode.TEAM.getArenaMode()));
        this.setOptions("wcArenas", (cp) -> Arena.getArenaNames(SpleefMode.WC.getArenaMode()));
    }
    
    @CommandAnnotation(minRank="DEVELOPER")
    public void spleefDebug(CorePlayer sender,
            @LiteralArg(value="debug") String l) {
        Party.createParty(sender);
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().getBattleManager(SpleefMode.CLASSIC.getArenaMode()).startMatch(Lists.newArrayList(sp, sp), "temple");
    }
    
    @CommandAnnotation
    public void spleef(CorePlayer sender) {
        sender.setInventoryMenuItem(Spleef.getInstance().getSpleefMenu());
    }
    
    @CommandAnnotation
    public void spleefClassic(CorePlayer sender, @LiteralArg(value="classic") String l, @Nullable @OptionArg(listName="classicArenas") String arena) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().queuePlayer(SpleefMode.CLASSIC.getArenaMode(), sp, Arena.getByName(arena, SpleefMode.CLASSIC.getArenaMode()));
    }
    
    @CommandAnnotation
    public void spleefMulti(CorePlayer sender, @LiteralArg(value="multi") String l) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().queuePlayer(SpleefMode.MULTI.getArenaMode(), sp);
    }
    
    @CommandAnnotation
    public void spleefPower(CorePlayer sender, @LiteralArg(value="power") String l, @Nullable @OptionArg(listName="powerArenas") String arena) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().queuePlayer(SpleefMode.POWER.getArenaMode(), sp, Arena.getByName(arena, SpleefMode.POWER.getArenaMode()));
    }
    
    @CommandAnnotation
    public void spleefTeam(CorePlayer sender, @LiteralArg(value="team") String l, @Nullable @OptionArg(listName="teamArenas") String arena) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        Spleef.getInstance().queuePlayer(SpleefMode.TEAM.getArenaMode(), sp, Arena.getByName(arena, SpleefMode.TEAM.getArenaMode()));
    }
    
    @CommandAnnotation(hidden=true)
    public void spleefWc(CorePlayer sender, @LiteralArg(value="wc") String l, @Nullable @OptionArg(listName="wcArenas") String arena) {
        error(sender, CoreError.SETUP);
        //SpleefPlayer sp = Spleef.getInstance().getPlayers().get(sender);
        //Spleef.getInstance().queuePlayer(SpleefMode.WC.getArenaMode(), sp, Arena.getByName(arena, SpleefMode.WC.getArenaMode()));
    }
    
}
