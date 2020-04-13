/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.commands;

import com.spleefleague.core.command.CommandAnnotation;
import com.spleefleague.core.command.HelperArg;
import com.spleefleague.core.command.LiteralArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.vendor.Vendor;
import com.spleefleague.core.vendor.VendorItem;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import com.spleefleague.core.command.OptionArg;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.menu.InventoryMenuItem;

/**
 * @author NickM13
 */
public class VendorCommand extends CommandTemplate {
    
    public VendorCommand() {
        super(VendorCommand.class, "vendor", Rank.DEVELOPER);
        setUsage("/vendor");
        setOptions("vendors", (cp) -> Vendor.getVendors().keySet());
        setOptions("itemTypes", (cp) -> VendorItem.getItemTypes());
    }
    
    @CommandAnnotation
    public void vendor(CorePlayer sender) {
        
    }
    
    @CommandAnnotation
    public void vendorCreate(CorePlayer sender,
            @LiteralArg(value="create") String l,
            @HelperArg(value="<vendor>") String vendor,
            @HelperArg(value="<displayName>") String displayName) {
        Vendor.createVendor(vendor, displayName);
    }
    
    @CommandAnnotation
    public void vendorRename(CorePlayer sender,
            @LiteralArg(value="rename") String l,
            @OptionArg(listName="vendors") String vendor,
            @HelperArg(value="<displayName>") String displayName) {
        Vendor.getVendor(vendor).setDisplayName(displayName);
    }
    
    @CommandAnnotation
    public void vendorEdit(CorePlayer sender,
            @LiteralArg(value="edit") String l,
            @OptionArg(listName="vendors") String vendor) {
        Vendor.getVendor(vendor).edit(sender);
    }
    
    @CommandAnnotation
    public void vendorList(CorePlayer sender,
            @LiteralArg(value="list") String l) {
        String vendorlist = "";
        Iterator<String> vit = Vendor.getVendors().keySet().iterator();
        while (vit.hasNext()) {
            vendorlist += vit.next();
            if (vit.hasNext()) {
                vendorlist += ", ";
            }
        }
        success(sender, "List of Vendors: " + vendorlist);
    }
    
    @CommandAnnotation
    public void vendorItems(CorePlayer sender,
            @LiteralArg(value="items") String l) {
        InventoryMenuContainer menu = InventoryMenuAPI.createContainer()
                .setTitle("Vendor Items!");
        for (String type : VendorItem.getItemTypes()) {
            InventoryMenuItem typeItem = InventoryMenuAPI.createItem()
                    .setName(type)
                    .setDisplayItem(Material.CHEST);
            InventoryMenuContainer typeMenu = typeItem
                    .getLinkedContainer()
                    .setTitle(type);
            for (VendorItem item : VendorItem.getItems(type).values()) {
                typeMenu.addMenuItem(InventoryMenuAPI.createItem()
                        .setName(item.getDisplayName())
                        .setDescription(item.getVendorDescription())
                        .setDisplayItem(item.getItem())
                        .setAction(cp -> {
                            cp.getPlayer().getInventory().addItem(item.getItem());
                        })
                        .setCloseOnAction(false));
            }
            menu.addMenuItem(typeItem);
        }
        sender.setInventoryMenuContainer(menu);
    }
    
    @CommandAnnotation
    public void vendorOpen(CommandSender sender,
            @LiteralArg(value="open") String l,
            CorePlayer cp,
            @OptionArg(listName="vendors") String name) {
        Vendor vendor = Vendor.getVendor(name);
        if (vendor != null) {
            vendor.openShop(cp);
        }
    }
    
    @CommandAnnotation
    public void vendorSet(CorePlayer sender,
            @LiteralArg(value="set") String l,
            @OptionArg(listName="vendors") String name) {
        Vendor.setPlayerVendor(sender, name);
    }
    
    @CommandAnnotation
    public void vendorUnset(CorePlayer sender,
            @LiteralArg(value="unset") String l) {
        Vendor.unsetPlayerVendor(sender);
    }
    
    @CommandAnnotation
    public void vendorDelete(CorePlayer sender,
            @LiteralArg(value="delete") String l,
            @OptionArg(listName="vendors") String name) {
        Vendor.deleteVendor(name);
    }

}
