package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public class InventoryMenuContainerAnvil extends InventoryMenuContainer {

    protected static final ItemStack SEARCH_ICON = InventoryMenuUtils.createCustomItem(Material.NAME_TAG, 1);

    static {
        ItemMeta itemMeta = SEARCH_ICON.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Reset");
        SEARCH_ICON.setItemMeta(itemMeta);
    }

    protected Function<CorePlayer, String> titleFunc;
    protected Function<String, Boolean> successFunc;
    protected BiConsumer<CorePlayer, String> action;
    protected InventoryMenuContainerChest parentContainer;
    protected String failText = "Invalid";
    
    public InventoryMenuContainerAnvil() {
        this.parentContainer = null;
    }

    public InventoryMenuContainerAnvil(InventoryMenuContainerAnvil container) {
        super(container);

        this.titleFunc = container.getTitle();
        this.successFunc = container.getSuccessFunc();
        this.action = container.getAction();
        this.failText = container.getFailText();
    }

    public Function<CorePlayer, String> getTitle() {
        return titleFunc;
    }

    public Function<String, Boolean> getSuccessFunc() {
        return successFunc;
    }

    public BiConsumer<CorePlayer, String> getAction() {
        return action;
    }

    public String getFailText() {
        return failText;
    }
    
    public InventoryMenuContainerAnvil setTitle(String title) {
        this.titleFunc = cp -> title;
        return this;
    }

    public InventoryMenuContainerAnvil setTitle(Function<CorePlayer, String> titleFunc) {
        this.titleFunc = titleFunc;
        return this;
    }
    
    public InventoryMenuContainerAnvil setSuccessFunc(Function<String, Boolean> successFunc) {
        this.successFunc = successFunc;
        return this;
    }
    
    public InventoryMenuContainerAnvil setAction(BiConsumer<CorePlayer, String> action) {
        this.action = action;
        return this;
    }
    
    public InventoryMenuContainerAnvil setFailText(String text) {
        this.failText = text;
        return this;
    }

    @Override
    public Inventory open(CorePlayer cp) {
        parentContainer = (InventoryMenuContainerChest) cp.getMenu().getInventoryMenuContainer();
        cp.getMenu().addInvSwap();
        new AnvilGUI.Builder()
                .onClose(player -> {
                    CorePlayer cp2 = Core.getInstance().getPlayers().get(player);
                    Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                        cp2.getMenu().removeInvSwap();
                        cp2.getMenu().setInventoryMenuChest(parentContainer, true);
                    }, 2L);
                })
                .onComplete((player, str) -> {
                    CorePlayer cp2 = Core.getInstance().getPlayers().get(player);
                    if (successFunc.apply(str)) {
                        action.accept(cp2, str);
                        return AnvilGUI.Response.close();
                    } else {
                        return AnvilGUI.Response.text(failText);
                    }
                })
                .text("Enter name here")
                .itemLeft(SEARCH_ICON)
                .title(titleFunc.apply(cp))
                .plugin(Core.getInstance())
                .open(cp.getPlayer());
        return null;
    }

    public InventoryMenuContainerAnvil clone() {
        return new InventoryMenuContainerAnvil(this);
    }

}
