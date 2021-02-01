/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.Core;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class BalanceCommand extends CoreCommand {
    
    public BalanceCommand() {
        super("balance", CoreRank.MODERATOR);
        addAlias("bal");
        setUsage("/balance");
        setDescription("Shows how many coins you have");
    }
    
    @CommandAnnotation
    public void balance(CorePlayer sender) {
        Core.getInstance().sendMessage(sender, "You have " + sender.getPurse().getCoins().getAmount() + " coins");
    }

}
