/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.NumberArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

/**
 * @author NickM13
 */
public class FSpeedCommand extends CommandTemplate {
    
    public FSpeedCommand() {
        super(FSpeedCommand.class, "fspeed", Rank.MODERATOR, Rank.BUILDER);
        setUsage("/fspeed [player] <-10 to 10>");
        setDescription("Set flying speed");
    }
    
    @CommandAnnotation
    public void fspeed(CorePlayer sender, @NumberArg(minValue=-10, maxValue=10) Double f) {
        f /= 10.;
        sender.getPlayer().setFlySpeed(f.floatValue());
        success(sender, "Fly speed set to " + f);
    }
    
    @CommandAnnotation(minRank="SENIOR_MODERATOR")
    public void fspeed(CorePlayer sender, CorePlayer cp, @NumberArg(minValue=-10, maxValue=10) Double f) {
        f /= 10.;
        cp.getPlayer().setFlySpeed(f.floatValue());
        success(cp, "Fly speed set to " + f);
        success(sender, "Fly speed of " + cp.getDisplayName() + " set to " + f);
    }
    
    @CommandAnnotation(minRank="SENIOR_MODERATOR")
    public void fspeed(CorePlayer sender, CorePlayer cp, @LiteralArg(value="reset") String l) {
        fspeed(sender, cp, 2.);
    }

}
