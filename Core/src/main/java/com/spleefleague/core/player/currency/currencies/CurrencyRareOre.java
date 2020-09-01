package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

public class CurrencyRareOre extends Currency {

    public CurrencyRareOre() {

    }

    public static String getIdentifier() {
        return "rare_ore";
    }

    public static String getName() {
        return ChatColor.AQUA + "Rare Ore";
    }

    public static Material getDisplayIcon() {
        return Material.IRON_ORE;
    }

}
