package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

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

    public Material getDisplayIcon() {
        return Material.IRON_ORE;
    }

}
