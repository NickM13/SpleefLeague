package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author NickM13
 * @since 4/29/2020
 */
public class InventoryMenuContainerAnvil extends InventoryMenuContainer {
    
    protected String title;
    protected Function<String, Boolean> successFunc;
    protected BiConsumer<CorePlayer, String> action;
    protected InventoryMenuContainerChest parentContainer;
    protected String failText = "Invalid";
    
    public InventoryMenuContainerAnvil() {
        this.parentContainer = null;
    }
    
    public InventoryMenuContainerAnvil setTitle(String title) {
        this.title = title;
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
    
    public InventoryMenuContainerAnvil setParentContainer(InventoryMenuContainerChest parentContainer) {
        this.parentContainer = parentContainer;
        return this;
    }
    
    @Override
    public void open(CorePlayer cp) {
        cp.getMenu().addInvSwap();
        new AnvilGUI.Builder()
                .onClose(player -> {
                    CorePlayer cp2 = Core.getInstance().getPlayers().get(player);
                    Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                        cp2.getMenu().setInventoryMenuChest(parentContainer, false);
                    }, 1L);
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
                .itemLeft(new ItemStack(Material.PAPER))
                .title(title)
                .plugin(Core.getInstance())
                .open(cp.getPlayer());
    }
    
}
