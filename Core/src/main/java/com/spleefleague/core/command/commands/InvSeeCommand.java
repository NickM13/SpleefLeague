/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class InvSeeCommand extends CoreCommand {
    
    public InvSeeCommand() {
        super("invsee", CoreRank.MODERATOR);
        setUsage("/invsee <player>");
        setDescription("See the inventory of a player");
    }
    
    @CommandAnnotation
    public void invsee(CorePlayer sender, CorePlayer cp) {
        sender.invsee(cp);
    }

}
