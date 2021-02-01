/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.CoreCommand;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.vendor.Vendor;

import java.util.Iterator;
import java.util.Map;

import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.vendor.Vendors;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import com.spleefleague.core.command.annotation.OptionArg;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;

/**
 * @author NickM13
 */
public class VendorCommand extends CoreCommand {
    
    public VendorCommand() {
        super("vendor", CoreRank.DEVELOPER);
        setUsage("/vendor");
        setOptions("vendors", (cp) -> Vendors.getVendors().keySet());
    }
    
    @CommandAnnotation
    public void vendor(CorePlayer sender) {
        
    }
    
    @CommandAnnotation
    public void vendorCreate(CorePlayer sender,
            @LiteralArg(value="create") String l,
            @HelperArg(value="<vendor>") String vendor,
            @HelperArg(value="<displayName>") String displayName) {
        Vendors.createVendor(vendor, displayName);
    }
    
    @CommandAnnotation
    public void vendorRename(CorePlayer sender,
            @LiteralArg(value="rename") String l,
            @OptionArg(listName="vendors") String vendor,
            @HelperArg(value="<displayName>") String displayName) {
        Vendors.getVendor(vendor).setDisplayName(displayName);
    }
    
    @CommandAnnotation
    public void vendorEdit(CorePlayer sender,
            @LiteralArg(value="edit") String l,
            @OptionArg(listName="vendors") String vendor) {
        Vendors.getVendor(vendor).edit(sender);
    }
    
    @CommandAnnotation
    @Deprecated
    public void vendorList(CorePlayer sender,
            @LiteralArg(value="list") String l) {
        StringBuilder vendorlist = new StringBuilder();
        Iterator<String> vit = Vendors.getVendors().keySet().iterator();
        while (vit.hasNext()) {
            vendorlist.append(vit.next());
            if (vit.hasNext()) {
                vendorlist.append(", ");
            }
        }
        success(sender, "List of Vendors: " + vendorlist);
    }
    
    @CommandAnnotation
    public void vendorItems(CorePlayer sender,
            @LiteralArg(value="items") String l) {
        InventoryMenuContainerChest menu = InventoryMenuAPI.createContainer()
                .setTitle("Vendor Items!");
        
        for (String type : Vendorable.getParentTypeNames()) {
            InventoryMenuItem typeItem = InventoryMenuAPI.createItemDynamic()
                    .setName(type)
                    .setDisplayItem(Material.CHEST)
                    .createLinkedContainer(type);
            InventoryMenuContainerChest typeMenu = typeItem
                    .getLinkedChest();
            Map<String, Vendorable> vendorableMap = Vendorables.getAll(type);
            if (vendorableMap != null) {
                for (Vendorable item : vendorableMap.values()) {
                    typeMenu.addMenuItem(InventoryMenuAPI.createItemDynamic()
                            .setDisplayItem(item.getDisplayItem())
                            .setName(item.getDisplayName())
                            .setDescription(item.getDescriptionVendor())
                            .setAction(cp -> cp.getPlayer().getInventory().addItem(item.getDisplayItem()))
                            .setCloseOnAction(false));
                }
            }
            menu.addMenuItem(typeItem);
        }
        
        sender.getMenu().setInventoryMenuChest(menu, true);
    }
    
    @CommandAnnotation
    public void vendorOpen(CommandSender sender,
            @LiteralArg(value="open") String l,
            CorePlayer cp,
            @OptionArg(listName="vendors") String name) {
        Vendor vendor = Vendors.getVendor(name);
        if (vendor != null) {
            vendor.openShop(cp);
            success(sender, "Opened vendor " + name + " for player " + cp.getDisplayName());
        } else {
            error(sender, "Unknown vendor " + name);
        }
    }
    
    @CommandAnnotation
    public void vendorSet(CorePlayer sender,
            @LiteralArg(value="set") String l,
            @OptionArg(listName="vendors") String name) {
        if (Vendors.setPlayerVendor(sender, name)) {
            success(sender, "Punch an entity to set it's vendor to " + name);
        } else {
            error(sender, "Unknown entity " + name);
        }
    }
    
    @CommandAnnotation
    public void vendorUnset(CorePlayer sender,
            @LiteralArg(value="unset") String l) {
        Vendors.unsetPlayerVendor(sender);
        success(sender, "Punch a vendor to clear it");
    }
    
    @CommandAnnotation
    public void vendorDelete(CorePlayer sender,
            @LiteralArg(value="delete") String l,
            @OptionArg(listName="vendors") String name) {
        if (Vendors.deleteVendor(Vendors.getVendor(name))) {
            success(sender, "Deleted vendor " + name);
        } else {
            error(sender, "Unknown vendor " + name);
        }
    }

}
