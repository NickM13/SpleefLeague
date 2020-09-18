package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

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

    public Material getDisplayIcon() {
        return Material.GOLD_NUGGET;
    }

}
