package com.spleefleague.core.player;

import com.spleefleague.core.player.currency.currencies.CurrencyCoin;
import com.spleefleague.core.player.currency.currencies.CurrencyCommonOre;
import com.spleefleague.core.player.currency.currencies.CurrencyEpicOre;
import com.spleefleague.core.player.currency.currencies.CurrencyLegendaryOre;
import com.spleefleague.core.player.currency.currencies.CurrencyRareOre;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

public class CorePlayerPurse extends DBEntity {

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
