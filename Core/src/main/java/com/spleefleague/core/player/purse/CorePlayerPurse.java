package com.spleefleague.core.player.purse;

import com.spleefleague.coreapi.player.purse.PlayerPurse;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CorePlayerPurse extends PlayerPurse {

    public void addCurrency(CoreCurrency currency, int amount) {
        super.addCurrency(currency.name(), amount);
    }

    public int getCurrency(CoreCurrency currency) {
        return super.getCurrency(currency.name());
    }

}
