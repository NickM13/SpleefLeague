package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

public class CurrencyEpicOre extends Currency {

    public CurrencyEpicOre() {

    }

    public static String getIdentifier() {
        return "epic_ore";
    }

    public static String getName() {
        return ChatColor.DARK_PURPLE + "Epic Ore";
    }

    public static Material getDisplayIcon() {
        return Material.GOLD_ORE;
    }

}
