package com.spleefleague.core.menu;

import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author NickM13
 */
public class InventoryMenuItemToggle extends InventoryMenuItem {

    private static ItemStack ENABLED = InventoryMenuUtils.MenuIcon.ENABLED.getIconItem();
    private static ItemStack DISABLED = InventoryMenuUtils.MenuIcon.DISABLED.getIconItem();

    protected Function<CorePlayer, Boolean> visibilityFun;
    protected Function<CorePlayer, Boolean> enabledFun;

    protected String name;
    protected String description;

    public InventoryMenuItemToggle() {
        super();
        this.closeOnAction = false;

        visibilityFun = null;
        enabledFun = null;

        name = "";
        description = "";
    }

    public InventoryMenuItemToggle setEnabledFun(Function<CorePlayer, Boolean> enabledFun) {
        this.enabledFun = enabledFun;
        return this;
    }

    @Override
    public InventoryMenuItemToggle setAction(Consumer<CorePlayer> action) {
        this.action = action;
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public InventoryMenuItemToggle setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public InventoryMenuItemToggle setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public InventoryMenuItemToggle setDescription(List<String> lore) {
        StringBuilder description = new StringBuilder();
        if (lore != null) {
            for (String line : lore) {
                description.append(line);
            }
        }
        this.description = description.toString();
        return this;
    }

    @Override
    public InventoryMenuItemToggle setDescriptionBuffer(int buffer) {
        this.descriptionBuffer = buffer;
        return this;
    }

    @Override
    public InventoryMenuItemToggle setDisplayItem(Material material) {
        return this;
    }

    @Override
    public InventoryMenuItemToggle setDisplayItem(Material material, int customModelData) {
        return this;
    }

    @Override
    public InventoryMenuItemToggle setDisplayItem(ItemStack displayItem) {
        return this;
    }

    @Override
    public InventoryMenuItemToggle setSelectedItem(Material material, int customModelData) {
        return this;
    }

    @Override
    public InventoryMenuItemToggle setCloseOnAction(boolean closeOnAction) {
        this.closeOnAction = closeOnAction;
        return this;
    }

    @Override
    public boolean isVisible(CorePlayer cp) {
        return visibilityFun == null || visibilityFun.apply(cp);
    }

    public InventoryMenuItemToggle setVisibility(Function<CorePlayer, Boolean> visible) {
        this.visibilityFun = visible;
        return this;
    }

    @Override
    public boolean isAvailable(CorePlayer cp) {
        return true;
    }

    public ItemStack createItem(CorePlayer cp, boolean selected) {
        List<String> wrappedDescription;
        if (!description.isEmpty()) wrappedDescription = Lists.newArrayList();
        wrappedDescription = ChatUtils.wrapDescription("\n" + Chat.colorize(description));
        ItemStack bakedItem;
        boolean enabled = enabledFun.apply(cp);
        if (enabled) {
            bakedItem = ENABLED.clone();
        } else {
            bakedItem = DISABLED.clone();
        }
        ItemMeta meta = bakedItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Chat.MENU_NAME + (enabled ? "Enabled" : "Disabled"));
            meta.setLore(wrappedDescription);
            meta.addItemFlags(ItemFlag.values());
            bakedItem.setItemMeta(meta);
        }
        return bakedItem;
    }

    @Override
    public String toString(CorePlayer cp) {
        return "TOGGLE:" + enabledFun.apply(cp);
    }

}
