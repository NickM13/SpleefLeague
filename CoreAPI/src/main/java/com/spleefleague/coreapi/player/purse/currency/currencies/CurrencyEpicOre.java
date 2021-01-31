package com.spleefleague.coreapi.player.purse.currency.currencies;

import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.player.purse.currency.Currency;

public class CurrencyEpicOre extends Currency {

    public CurrencyEpicOre() {

    }

    public String getIdentifier() {
        return "epic_ore";
    }

    public String getChatColor() {
        return ChatColor.DARK_PURPLE + "";
    }

    public String getName() {
        return "Epic Ore";
    }

}
