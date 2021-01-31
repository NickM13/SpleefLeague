package com.spleefleague.core.menu;

import com.google.common.collect.Lists;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.chat.ChatUtils;
import com.spleefleague.core.player.CorePlayer;
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
public class InventoryMenuItemSearch extends InventoryMenuItemDynamic {

    String searchTag = null;
    Function<String, Boolean> successFun = null;
    BiConsumer<CorePlayer, String> successAction = null;
    String failText = "Invalid input";

    public InventoryMenuItemSearch() {
        super();

        setDisplayItem(Material.NAME_TAG, 1);
        setCloseOnAction(false);
    }

    @Override
    public InventoryMenuItemSearch setName(String name) {
        super.setName(name);
        return this;
    }

    @Override
    public InventoryMenuItemSearch setName(Function<CorePlayer, String> nameFun) {
        super.setName(nameFun);
        return this;
    }

    @Override
    public InventoryMenuItemSearch setDescription(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public InventoryMenuItemSearch setDescription(Function<CorePlayer, String> descriptionFun) {
        super.setDescription(descriptionFun);
        return this;
    }

    @Override
    public InventoryMenuItemSearch setDescription(List<String> lore) {
        super.setDescription(lore);
        return this;
    }

    @Override
    public InventoryMenuItemSearch setDescriptionBuffer(int buffer) {
        this.descriptionBuffer = buffer;
        return this;
    }

    @Override
    public InventoryMenuItemSearch setVisibility(Function<CorePlayer, Boolean> visibilityFun) {
        super.setVisibility(visibilityFun);
        return this;
    }

    @Override
    public InventoryMenuItemSearch setAvailability(Function<CorePlayer, Boolean> availableFun) {
        super.setAvailability(availableFun);
        return this;
    }

    public InventoryMenuItemSearch setSearchTag(String tag) {
        this.searchTag = tag;
        return this;
    }

    public InventoryMenuItemSearch setSuccess(Function<String, Boolean> successFun) {
        this.successFun = successFun;
        return this;
    }

    public InventoryMenuItemSearch setSuccessAction(BiConsumer<CorePlayer, String> action) {
        successAction = action;
        return this;
    }

    public InventoryMenuItemSearch setFailText(String failText) {
        this.failText = failText;
        return this;
    }

    @Override
    public InventoryMenuItemSearch build() {
        setAction(cp -> cp.getMenu().setInventoryMenuAnvil(InventoryMenuAPI.createAnvil()
                .setTitle(nameFun.apply(cp))
                .setSuccessFunc(str -> successFun != null ? successFun.apply(str) : true)
                .setAction((cp2, str) -> {
                    if (searchTag != null) {
                        cp.getMenu().setMenuTag(searchTag, str);
                    }
                    if (successAction != null) {
                        successAction.accept(cp2, str);
                    }
                })
                .setFailText(failText)));
        return this;
    }

}
