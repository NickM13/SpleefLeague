package com.spleefleague.coreapi.player.purse.currency.currencies;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.purse.currency.Currency;

public class CurrencyRareOre extends Currency {

    public CurrencyRareOre() {

    }

    public String getIdentifier() {
        return "rare_ore";
    }

    public String getChatColor() {
        return ChatColor.AQUA + "";
    }

    public String getName() {
        return "Rare Ore";
    }

}
