package com.spleefleague.coreapi.player.purse.currency.currencies;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.purse.currency.Currency;

public class CurrencyCoin extends Currency {

    public CurrencyCoin() {

    }

    public String getIdentifier() {
        return "coin";
    }

    public String getChatColor() {
        return ChatColor.GOLD + "";
    }

    public String getName() {
        return "Coin";
    }

}
