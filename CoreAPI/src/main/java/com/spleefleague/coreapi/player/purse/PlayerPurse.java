package com.spleefleague.coreapi.player.purse;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.player.purse.currency.currencies.*;

/**
 * @author NickM13
 */
public class PlayerPurse extends DBEntity {

    @DBField private final CurrencyCoin coins = new CurrencyCoin();
    @DBField private final CurrencyCommonOre commonOre = new CurrencyCommonOre();
    @DBField private final CurrencyRareOre rareOre = new CurrencyRareOre();
    @DBField private final CurrencyEpicOre epicOre = new CurrencyEpicOre();
    @DBField private final CurrencyLegendaryOre legendaryOre = new CurrencyLegendaryOre();

    public CurrencyCoin getCoins() {
        return coins;
    }

    public CurrencyCommonOre getCommonOre() {
        return commonOre;
    }

    public CurrencyRareOre getRareOre() {
        return rareOre;
    }

    public CurrencyEpicOre getEpicOre() {
        return epicOre;
    }

    public CurrencyLegendaryOre getLegendaryOre() {
        return legendaryOre;
    }

}
