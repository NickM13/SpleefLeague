package com.spleefleague.core.menu.hotbars.main.profile;

import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.purse.CoreCurrency;
import org.bukkit.Material;

import java.util.function.Function;
import java.util.function.Supplier;

public class OresMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Gold and Ores")
                .setDisplayItem(Material.QUARTZ, 1)
                .setDescription("View your currencies")
                .createLinkedContainer("Gold and Ores");

        InventoryMenuContainerChest container = menuItem.getLinkedChest();

        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> "Gold Coins [" + cp.getPurse().getCurrency(CoreCurrency.COIN) + "]")
                .setDisplayItem(CoreCurrency.COIN.displayItem)
                .setDescription(CoreCurrency.COIN.description)
                .setCloseOnAction(false), 4, 0);

        createDigitMenu(container, 0, 0, 4, cp -> cp.getPurse().getCurrency(CoreCurrency.COIN));

        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> "Common Ore [" + cp.getPurse().getCurrency(CoreCurrency.ORE_COMMON) + "]")
                .setDisplayItem(CoreCurrency.ORE_COMMON.displayItem)
                .setDescription(CoreCurrency.ORE_COMMON.description)
                .setCloseOnAction(false), 0, 1);

        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> "Rare Ore [" + cp.getPurse().getCurrency(CoreCurrency.ORE_RARE) + "]")
                .setDisplayItem(CoreCurrency.ORE_RARE.displayItem)
                .setDescription(CoreCurrency.ORE_RARE.description)
                .setCloseOnAction(false), 1, 1);

        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> "Epic Ore [" + cp.getPurse().getCurrency(CoreCurrency.ORE_EPIC) + "]")
                .setDisplayItem(CoreCurrency.ORE_EPIC.displayItem)
                .setDescription(CoreCurrency.ORE_EPIC.description)
                .setCloseOnAction(false), 2, 1);

        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                .setName(cp -> "Legendary Ore [" + cp.getPurse().getCurrency(CoreCurrency.ORE_LEGENDARY) + "]")
                .setDisplayItem(CoreCurrency.ORE_LEGENDARY.displayItem)
                .setDescription(CoreCurrency.ORE_LEGENDARY.description)
                .setCloseOnAction(false), 3, 1);
    }

    private static void createDigitMenu(InventoryMenuContainerChest container, int startX, int startY, int count, Function<CorePlayer, Integer> function) {
        for (int i = 0; i < count; i++) {
            int finalI = (int) Math.pow(10, count - i - 1);
            container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                    .setName(cp -> {
                        int num = (function.apply(cp) / finalI) % 10;
                        return "" + num;
                    })
                    .setDisplayItem(cp -> {
                        int num = (function.apply(cp) / finalI) % 10;
                        if (num == 0) num = 10;
                        return InventoryMenuUtils.createCustomItem(Material.DIAMOND, num);
                    })
                    .setCloseOnAction(false), startX + i, startY);
        }
    }

    /**
     * Gets the menu item for this menu, if it doesn't exist
     * already then initialize it
     *
     * @return Inventory Menu Item
     */
    public static InventoryMenuItem getItem() {
        if (menuItem == null) init();
        return menuItem;
    }

}
