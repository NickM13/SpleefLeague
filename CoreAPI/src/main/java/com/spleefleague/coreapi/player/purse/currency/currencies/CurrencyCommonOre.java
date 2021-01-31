package com.spleefleague.coreapi.player.purse.currency.currencies;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.purse.currency.Currency;

public class CurrencyCommonOre extends Currency {

    public CurrencyCommonOre() {

    }

    public String getIdentifier() {
        return "common_ore";
    }

    public String getChatColor() {
        return ChatColor.GREEN + "";
    }

    public String getName() {
        return "Common Ore";
    }

}
