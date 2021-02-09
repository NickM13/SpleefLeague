package com.spleefleague.core.menu.hotbars.main;

import com.spleefleague.core.Core;
import com.spleefleague.core.crate.Crate;
import com.spleefleague.core.crate.CrateLoot;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.collectible.Collectible;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.player.purse.CoreCurrency;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CrateMenu {

    private static InventoryMenuItem menuItem = null;

    public static void init() {
        menuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Crates")
                .setDisplayItem(Material.YELLOW_SHULKER_BOX, 3)
                .setSelectedItem(Material.YELLOW_SHULKER_BOX, 4)
                .setDescription("")
                .createLinkedContainer("Crates");

        InventoryMenuContainer lootContainer = InventoryMenuAPI.createContainer()
                .setTitle("Loot!")
                .setParent(menuItem.getLinkedChest())
                .setOpenAction((container, cp) -> {
                    String crateName = cp.getMenu().getMenuTag("openedCrate", String.class);
                    CrateLoot loot = cp.getCrates().openCrate(crateName);
                    container.clear();
                    for (Collectible collectible : loot.collectibles) {
                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setDisplayItem(collectible.getDisplayItem())
                                .setName(collectible.getDisplayName()));
                    }
                    for (CollectibleSkin skin : loot.collectibleSkins) {
                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setDisplayItem(skin.getDisplayItem())
                                .setName(skin.getDisplayName()));
                    }
                    for (Map.Entry<CoreCurrency, Integer> entry : loot.currencies.entrySet()) {
                        ItemStack item = entry.getKey().displayItem.clone();
                        item.setAmount(entry.getValue());
                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setDisplayItem(item)
                                .setName(entry.getKey().displayName));
                    }
                });

        menuItem.getLinkedChest()
                .addDeadSpace(0, 0)
                .addDeadSpace(0, 1)
                .addDeadSpace(0, 2)
                .addDeadSpace(0, 3)
                .addDeadSpace(0, 4)
                .addDeadSpace(4, 0)
                .addDeadSpace(4, 1)
                .addDeadSpace(4, 2)
                .addDeadSpace(4, 3)
                .addDeadSpace(4, 4);

        menuItem.getLinkedChest()
                .setRefreshAction((container, cp) -> {
                    container.clear();
                    for (Crate crate : Core.getInstance().getCrateManager().getSortedCrates()) {
                        int crateCount = cp.getCrates().getCrateCount(crate.getIdentifier());
                        if (crateCount <= 0 && crate.isHidden()) {
                            continue;
                        }

                        if (crateCount > 0) {
                            container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                    .setName("Open Crate")
                                    .setDisplayItem(InventoryMenuUtils.MenuIcon.ENABLED.getIconItem(crateCount))
                                    .setAction(cp2 -> cp2.getMenu().setMenuTag("openedCrate", crate.getIdentifier()))
                                    .setLinkedContainer(lootContainer));
                        } else {
                            container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                    .setName("0 Crates, buy more at the store!")
                                    .setDisplayItem(InventoryMenuUtils.MenuIcon.DISABLED.getIconItem())
                                    .setCloseOnAction(false));
                        }

                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName(crate.getDisplayName())
                                .setDisplayItem(crate.getClosed())
                                .setDescription(crate.getDescription())
                                .setCloseOnAction(false));

                        container.addMenuItem(InventoryMenuAPI.createItemStatic()
                                .setName("Crates Online Store")
                                .setDisplayItem(InventoryMenuUtils.MenuIcon.STORE.getIconItem())
                                .setCloseOnAction(false));
                    }
                });
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
