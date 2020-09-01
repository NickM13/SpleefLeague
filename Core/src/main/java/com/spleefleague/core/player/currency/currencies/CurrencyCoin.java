package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

public class CurrencyCoin extends Currency {

    public CurrencyCoin() {

    }

    public static String getIdentifier() {
        return "coin";
    }

    public static String getName() {
        return ChatColor.GOLD + "Coin";
    }

    public static Material getDisplayIcon() {
        return Material.GOLD_NUGGET;
    }

}
