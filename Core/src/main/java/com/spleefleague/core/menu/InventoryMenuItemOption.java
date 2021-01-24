/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.menu;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.player.CorePlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public class InventoryMenuItemOption extends InventoryMenuItemDynamic {
    
    public class Option {
        private String name;
        private ItemStack item;
        
        public Option(String name, ItemStack item) {
            this.name = name;
            this.item = item;
        }
        
        public String getName() {
            return name;
        }
        
        public ItemStack getDisplayItem() {
            return item;
        }
    }
    
    protected List<Option> options;
    protected Function<CorePlayer, Integer> selectedFun;
    
    public InventoryMenuItemOption() {
        options = new ArrayList<>();
        setCloseOnAction(false);
    }
    
    public InventoryMenuItemOption addOption(String name, ItemStack item) {
        options.add(new Option(name, item));
        return this;
    }
    
    public InventoryMenuItemOption setSelected(Function<CorePlayer, Integer> selectedFun) {
        this.selectedFun = selectedFun;
        return this;
    }
    
    @Override
    public ItemStack createItem(CorePlayer cp) {
        int selected = selectedFun.apply(cp);
        ItemStack item = options.get(selected).getDisplayItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Chat.MENU_NAME + nameFun.apply(cp));
        
        List<String> lore = getWrappedDescription(cp);
        for (int i = 0; i < options.size(); i++) {
            Option o = options.get(i);
            if (i == selected) {
                lore.add(Chat.SUCCESS + o.getName());
            } else {
                lore.add(Chat.ERROR + o.getName());
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }
    
}
