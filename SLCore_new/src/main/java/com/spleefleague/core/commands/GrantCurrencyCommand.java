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
public class GrantCurrencyCommand extends CommandTemplate {
    
    public GrantCurrencyCommand() {
        super(GrantCurrencyCommand.class, "grantcurrency", Rank.DEVELOPER);
        setUsage("/grantcurrency <player> <amt>");
        setDescription("Add currency");
    }
    
    @CommandAnnotation
    public void grantcurrency(CorePlayer sender, CorePlayer cp, Integer amt) {
        cp.addCoins(amt);
    }

}
