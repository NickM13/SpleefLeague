package com.spleefleague.core.menu;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author NickM13
 */
public class InventoryMenuItemDynamic extends InventoryMenuItem {

    protected Function<CorePlayer, Boolean> visibilityFun;
    protected Function<CorePlayer, Boolean> availableFun;

    protected Function<CorePlayer, String> nameFun;
    protected Function<CorePlayer, String> descriptionFun;
    protected Function<CorePlayer, ItemStack> displayItemFun;
    protected Function<CorePlayer, ItemStack> selectedItemFun;

    public InventoryMenuItemDynamic() {
        super();
        minRank = Rank.DEFAULT;
        visibilityFun = null;
        availableFun = null;

        nameFun = null;
        descriptionFun = null;
        displayItemFun = null;
        selectedItemFun = null;
    }

    public String toString(CorePlayer cp) {
        return nameFun.apply(cp);
    }

    @Override
    public InventoryMenuItemDynamic setName(String name) {
        this.nameFun = (cp) -> name;
        return this;
    }

    public InventoryMenuItemDynamic setName(Function<CorePlayer, String> nameFun) {
        this.nameFun = nameFun;
        return this;
    }

    @Override
    public InventoryMenuItemDynamic setDescription(String description) {
        this.descriptionFun = (cp) -> Chat.colorize(description);
        return this;
    }

    public InventoryMenuItemDynamic setDescription(Function<CorePlayer, String> descriptionFun) {
        this.descriptionFun = descriptionFun;
        return this;
    }
    @Override
    public InventoryMenuItemDynamic setDescription(List<String> lore) {
        this.descriptionFun = cp -> {
            if (lore == null) return "";
            StringBuilder description = new StringBuilder();
            for (String line : lore) {
                description.append(line);
            }
            return description.toString();
        };
        return this;
    }

    @Override
    public InventoryMenuItemDynamic setDescriptionBuffer(int buffer) {
        this.descriptionBuffer = buffer;
        return this;
    }

    @Override
    public InventoryMenuItemDynamic setDisplayItem(Material material) {
        this.displayItemFun = (cp) -> new ItemStack(material);
        return this;
    }
    @Override
    public InventoryMenuItemDynamic setDisplayItem(Material material, int customModelData) {
        this.displayItemFun = (cp) -> InventoryMenuUtils.createCustomItem(material, customModelData);
        return this;
    }
    @Override
    public InventoryMenuItemDynamic setDisplayItem(ItemStack displayItem) {
        this.displayItemFun = (cp) -> displayItem;
        return this;
    }

    public InventoryMenuItemDynamic setDisplayItem(Function<CorePlayer, ItemStack> displayItemFun) {
        this.displayItemFun = displayItemFun;
        return this;
    }

    public InventoryMenuItemDynamic setSelectedItem(Material material, int customModelData) {
        this.selectedItemFun = cp -> InventoryMenuUtils.createCustomItem(material, customModelData);
        return this;
    }

    @Override
    public InventoryMenuItemDynamic setCloseOnAction(boolean closeOnAction) {
        this.closeOnAction = closeOnAction;
        return this;
    }

    @Override
    public InventoryMenuItemDynamic setAction(Consumer<CorePlayer> action) {
        this.action = action;
        return this;
    }

    public InventoryMenuItemDynamic setVisibility(Function<CorePlayer, Boolean> visibilityFun) {
        this.visibilityFun =  visibilityFun;
        return this;
    }

    public boolean isVisible(CorePlayer cp) {
        if (!cp.getRank().hasPermission(getMinRank())) {
            return false;
        }
        return visibilityFun == null || visibilityFun.apply(cp);
    }
    public InventoryMenuItemDynamic setAvailability(Function<CorePlayer, Boolean> availableFun) {
        this.availableFun = availableFun;
        return this;
    }
    public boolean isAvailable(CorePlayer cp) {
        return availableFun == null || availableFun.apply(cp);
    }

    protected List<String> getWrappedDescription(CorePlayer cp) {
        if (descriptionFun != null) {
            return ChatUtils.wrapDescription(Strings.repeat("\n", descriptionBuffer) + Chat.colorize(descriptionFun.apply(cp)));
        } else {
            return Lists.newArrayList();
        }
    }

    public ItemStack createItem(CorePlayer cp, boolean selected) {
        ItemStack item;
        if (selected && selectedItemFun != null) {
            item = selectedItemFun.apply(cp).clone();
        } else if (displayItemFun != null) {
            item = displayItemFun.apply(cp).clone();
        } else {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (nameFun != null) {
                meta.setDisplayName(Chat.MENU_NAME + Chat.colorize(nameFun.apply(cp)));
            } else {
                meta.setDisplayName("");
            }
            meta.setLore(getWrappedDescription(cp));
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
        }
        return item;
    }

}
