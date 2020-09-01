package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

public class CurrencyCommonOre extends Currency {

    public CurrencyCommonOre() {

    }

    public static String getIdentifier() {
        return "common_ore";
    }

    public static String getName() {
        return ChatColor.GREEN + "Common Ore";
    }

    public static Material getDisplayIcon() {
        return Material.COAL_ORE;
    }

}
