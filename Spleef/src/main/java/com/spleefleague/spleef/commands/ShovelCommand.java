/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.spleef.game.Shovel;

/**
 * @author NickM13
 */
public class ShovelCommand extends HoldableCommand {

    public ShovelCommand() {
        super(Shovel.class, "shovel", CoreRank.DEVELOPER);
        this.setContainer("spleef");
    }

}
