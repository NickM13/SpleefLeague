/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;

/**
 * @author NickM13
 */
public class InvSeeCommand extends CommandTemplate {
    
    public InvSeeCommand() {
        super(InvSeeCommand.class, "invsee", Rank.MODERATOR);
        setUsage("/invsee <player>");
        setDescription("See the inventory of a player");
    }
    
    @CommandAnnotation
    public void invsee(CorePlayer sender, CorePlayer cp) {
        sender.invsee(cp);
        /*
        This is more of an invcopy
        if (args.length > 0) {
            CorePlayer target = Core.getInstance().getPlayer(args[0]);
            cp.invsee(target);
        } else {
            // Reverts the inventory (TODO: Find better name for function)
            cp.loadPregameState();
        }
        */
    }

}
