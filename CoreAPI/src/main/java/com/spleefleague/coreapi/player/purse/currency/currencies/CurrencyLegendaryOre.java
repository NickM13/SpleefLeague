package com.spleefleague.coreapi.player.purse.currency.currencies;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.purse.currency.Currency;

public class CurrencyLegendaryOre extends Currency {

    public CurrencyLegendaryOre() {

    }

    public String getIdentifier() {
        return "legendary_ore";
    }

    public String getChatColor() {
        return ChatColor.YELLOW + "";
    }

    public String getName() {
        return "Legendary Ore";
    }

}
