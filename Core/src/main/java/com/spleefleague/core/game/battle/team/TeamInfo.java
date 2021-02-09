package com.spleefleague.core.game.battle.team;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.BattleState;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author NickM13
 * @since 2/8/2021
 */
public enum TeamInfo {

    BLUE(ChatColor.BLUE + "Blue", org.bukkit.Color.fromRGB(0, 0, 255)),
    RED(ChatColor.RED + "Red", org.bukkit.Color.fromRGB(255, 0, 0)),
    YELLOW(ChatColor.YELLOW + "Yellow", org.bukkit.Color.fromRGB(255, 255, 0)),
    GREEN(ChatColor.GREEN + "Green", org.bukkit.Color.fromRGB(0, 255, 0));

    String name;
    org.bukkit.Color color;

    public final ItemStack boots;
    public final ItemStack leggings;
    public final ItemStack chestplate;

    TeamInfo(String name, org.bukkit.Color color) {
        this.name = name;
        this.color = color;

        LeatherArmorMeta meta;

        boots = new ItemStack(Material.LEATHER_BOOTS);
        meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(color);
        boots.setItemMeta(meta);

        leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        meta = (LeatherArmorMeta) leggings.getItemMeta();
        meta.setColor(color);
        leggings.setItemMeta(meta);

        chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = (LeatherArmorMeta) chestplate.getItemMeta();
        meta.setColor(color);
        chestplate.setItemMeta(meta);
    }

    public String getName() {
        return name;
    }

    public org.bukkit.Color getColor() {
        return color;
    }

    public static void init() {
        InventoryMenuAPI.createItemHotbar(38, "teamChestplate")
                .setName(cp -> ((TeamBattle<?>) cp.getBattle()).getTeam(cp).getTeamInfo().getName() + " Chestplate")
                .setDisplayItem(cp -> ((TeamBattle<?>) cp.getBattle()).getTeam(cp).getTeamInfo().chestplate)
                .setAvailability(cp -> cp.isInBattle() &&
                        cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle() instanceof TeamBattle);

        InventoryMenuAPI.createItemHotbar(37, "teamLeggings")
                .setName(cp -> ((TeamBattle<?>) cp.getBattle()).getTeam(cp).getTeamInfo().getName() + " Leggings")
                .setDisplayItem(cp -> ((TeamBattle<?>) cp.getBattle()).getTeam(cp).getTeamInfo().leggings)
                .setAvailability(cp -> cp.isInBattle() &&
                        cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle() instanceof TeamBattle);

        InventoryMenuAPI.createItemHotbar(36, "teamBoots")
                .setName(cp -> ((TeamBattle<?>) cp.getBattle()).getTeam(cp).getTeamInfo().getName() + " Boots")
                .setDisplayItem(cp -> ((TeamBattle<?>) cp.getBattle()).getTeam(cp).getTeamInfo().boots)
                .setAvailability(cp -> cp.isInBattle() &&
                        cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle() instanceof TeamBattle);
    }

}
