package com.spleefleague.core.menu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author NickM13
 */
public class InventoryMenuItemStatic extends InventoryMenuItem {

    private final ItemStack DEFAULT_DISPLAY = new ItemStack(Material.SNOW_BLOCK);

    protected boolean visible;
    protected boolean available;

    protected String name;
    protected String description;
    protected ItemStack displayItem;
    protected ItemStack selectedItem;

    public InventoryMenuItemStatic() {
        visible = true;
        available = true;

        name = "";
        description = "";
        displayItem = DEFAULT_DISPLAY;
        selectedItem = null;
    }

    @Override
    public String toString(CorePlayer cp) {
        return name;
    }

    @Override
    public InventoryMenuItemStatic setName(String name) {
        this.name = name;
        itemChanges = true;
        return this;
    }

    @Override
    public InventoryMenuItemStatic setDescription(String description) {
        this.description = description;
        itemChanges = true;
        return this;
    }

    @Override
    public InventoryMenuItemStatic setDescription(List<String> lore) {
        StringBuilder description = new StringBuilder();
        if (lore != null) {
            for (String line : lore) {
                description.append(line);
            }
        }
        this.description = description.toString();
        itemChanges = true;
        return this;
    }

    @Override
    public InventoryMenuItemStatic setDescriptionBuffer(int buffer) {
        this.descriptionBuffer = buffer;
        return this;
    }

    @Override
    public InventoryMenuItemStatic setDisplayItem(Material material) {
        this.displayItem = InventoryMenuUtils.createCustomItem(material);
        itemChanges = true;
        return this;
    }

    @Override
    public InventoryMenuItemStatic setDisplayItem(Material material, int customModelData) {
        this.displayItem = InventoryMenuUtils.createCustomItem(material, customModelData);
        itemChanges = true;
        return this;
    }

    @Override
    public InventoryMenuItemStatic setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
        itemChanges = true;
        return this;
    }

    @Override
    public InventoryMenuItemStatic setSelectedItem(Material material, int customModelData) {
        this.selectedItem = InventoryMenuUtils.createCustomItem(material, customModelData);
        itemChanges = true;
        return this;
    }

    @Override
    public boolean isVisible(CorePlayer cp) {
        return visible;
    }

    public InventoryMenuItemStatic setVisibility(boolean visible) {
        this.visible = visible;
        return this;
    }

    @Override
    public boolean isAvailable(CorePlayer cp) {
        return available;
    }

    @Override
    public InventoryMenuItem setCloseOnAction(boolean closeOnAction) {
        this.closeOnAction = closeOnAction;
        return this;
    }

    @Override
    public InventoryMenuItem setAction(Consumer<CorePlayer> action) {
        this.action = action;
        return this;
    }

    private boolean itemChanges = true;
    private ItemStack bakedItem;

    private void updateDisplayItem() {
        List<String> wrappedDescription;
        if (!description.isEmpty()) {
            wrappedDescription = ChatUtils.wrapDescription(Strings.repeat("\n", descriptionBuffer) + Chat.colorize(description));
        } else {
            wrappedDescription = Lists.newArrayList();
        }
        bakedItem = displayItem.clone();
        ItemMeta meta = bakedItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Chat.MENU_NAME + Chat.colorize(name));
            meta.setLore(wrappedDescription);
            meta.addItemFlags(ItemFlag.values());
            bakedItem.setItemMeta(meta);
        }
    }

    public ItemStack createItem(CorePlayer cp, boolean selected) {
        if (itemChanges) {
            updateDisplayItem();
            itemChanges = false;
        }
        return bakedItem;
    }

}
