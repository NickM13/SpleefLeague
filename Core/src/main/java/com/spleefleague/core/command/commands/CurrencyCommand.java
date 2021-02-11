/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.command.annotation.*;
import com.spleefleague.core.command.error.CoreError;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.player.rank.CoreRank;

/**
 * @author NickM13
 */
public class CurrencyCommand extends CoreCommand {

    public CurrencyCommand() {
        super("currency", CoreRank.DEVELOPER);
    }

    @CommandAnnotation
    public void currencyGive(CorePlayer sender,
                             @LiteralArg("give") String l,
                             @CorePlayerArg(allowOffline = true, allowCrossServer = true) CorePlayer target,
                             @EnumArg CoreCurrency currency,
                             @NumberArg Integer amount) {
        target.getPurse().addCurrency(currency, amount);
    }

}
