package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author NickM13
 * @since 5/1/2020
 */
public class InventoryMenuDialog {

    private class Dialog {
        String tagName;

        public Dialog(String tagName) {
            this.tagName = tagName;
        }
    }

    private class DialogText extends Dialog {

        Function<String, Boolean> successFun;

        public DialogText(String tagName, Function<String, Boolean> successFun) {
            super(tagName);
            this.successFun = successFun;
        }

    }

    private InventoryMenuContainerChest parentContainer = null;
    private List<Dialog> dialogs = new ArrayList<>();

    public InventoryMenuDialog setParentContainer(InventoryMenuContainerChest inventoryMenuContainer) {
        parentContainer = inventoryMenuContainer;
        return this;
    }

    public InventoryMenuDialog addTextDialog(String tagName, Function<String, Boolean> successFun) {
        dialogs.add(new DialogText(tagName, successFun));
        return this;
    }

    public boolean openNextContainer(CorePlayer cp, int id) {
        if (id < dialogs.size()) {
            Dialog dialog = dialogs.get(id);
            if (dialog instanceof DialogText) {
                cp.getMenu().addInvSwap();
                new AnvilGUI.Builder()
                        .onClose(player -> {
                            CorePlayer cp2 = Core.getInstance().getPlayers().get(player);
                            Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                                if (cp2.getMenu().hasMenuTag(dialog.tagName)) {
                                    cp2.getMenu().openNextDialog();
                                } else {
                                    cp2.getMenu().setInventoryMenuChest(parentContainer, false);
                                }
                            }, 1L);
                        })
                        .onComplete((player, str) -> {
                            CorePlayer cp2 = Core.getInstance().getPlayers().get(player);
                            if (((DialogText) dialog).successFun.apply(str)) {
                                cp2.getMenu().setMenuTag(dialog.tagName, str);
                                return AnvilGUI.Response.close();
                            } else {
                                return AnvilGUI.Response.text("Invalid");
                            }
                        })
                        .text("Enter name here")
                        .itemLeft(new ItemStack(Material.PAPER))
                        .title(dialog.tagName)
                        .plugin(Core.getInstance())
                        .open(cp.getPlayer());
            }
            return true;
        } else {

            return false;
        }
    }

}
