package com.spleefleague.core.player.currency.currencies;

import com.spleefleague.core.player.currency.Currency;
import com.spleefleague.coreapi.chat.ChatColor;
import org.bukkit.Material;

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

    public Material getDisplayIcon() {
        return Material.GOLD_ORE;
    }

}
