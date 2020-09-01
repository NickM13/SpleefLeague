/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.commands;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.command.HoldableCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.rank.Rank;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.spleef.game.Shovel;

/**
 * @author NickM13
 */
public class ShovelCommand extends HoldableCommand {

    public ShovelCommand() {
        super(Shovel.class, "shovel", Rank.DEVELOPER);
        this.setContainer("spleef");
        this.setOptions("shovelTypes", cp -> Shovel.getShovelTypes());
    }

    @CommandAnnotation
    public void shovelSetType(CorePlayer sender,
                              @LiteralArg("set") String l1,
                              @LiteralArg("type") String l2,
                              @OptionArg(listName = "shovelTypes") String type) {
        Shovel shovel = Vendorables.get(Shovel.class, sender.getHeldItem());
        if (shovel != null) {
            shovel.setShovelType(type);
            success(sender, "Changed shovel type to " + type);
        } else {
            error(sender, "That's not a registered collectible!");
        }
    }

}
