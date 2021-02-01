/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.NumberArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class WalkSpeedCommand extends CoreCommand {

    public WalkSpeedCommand() {
        super("wspeed", CoreRank.MODERATOR, CoreRank.BUILDER);
        addAlias("walkspeed");
        setUsage("/wspeed [player] <-10 to 10>");
        setDescription("Set walking speed");
    }

    @CommandAnnotation
    public void wspeed(CorePlayer sender, @NumberArg(minValue = -10, maxValue = 10) Double f) {
        f /= 10.;
        sender.getPlayer().setWalkSpeed(f.floatValue());
        success(sender, "Walk speed set to " + f);
    }

    @CommandAnnotation(minRank = "SENIOR_MODERATOR")
    public void wspeed(CorePlayer sender, CorePlayer cp, @NumberArg(minValue = -10, maxValue = 10) Double f) {
        f /= 10.;
        cp.getPlayer().setWalkSpeed(f.floatValue());
        success(cp, "Walk speed set to " + f);
        success(sender, "Walk speed of " + cp.getDisplayName() + " set to " + f);
    }

    @CommandAnnotation(minRank = "SENIOR_MODERATOR")
    public void wspeed(CorePlayer sender, CorePlayer cp, @LiteralArg(value = "reset") String l) {
        wspeed(sender, cp, 2.);
    }

}
