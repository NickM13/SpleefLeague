package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

public class CurrencyLegendaryOre extends Currency {

    public CurrencyLegendaryOre() {

    }

    public static String getIdentifier() {
        return "legendary_ore";
    }

    public static String getName() {
        return ChatColor.YELLOW + "Legendary Ore";
    }

    public static Material getDisplayIcon() {
        return Material.DIAMOND_ORE;
    }

}
