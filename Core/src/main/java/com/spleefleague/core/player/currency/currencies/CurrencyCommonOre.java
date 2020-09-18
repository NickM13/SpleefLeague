package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

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

    public Material getDisplayIcon() {
        return Material.COAL_ORE;
    }

}
