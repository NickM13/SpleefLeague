package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

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

    public Material getDisplayIcon() {
        return Material.DIAMOND_ORE;
    }

}
