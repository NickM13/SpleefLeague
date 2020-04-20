/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.annotation.CommandAnnotation;
import com.spleefleague.core.command.annotation.HelperArg;
import com.spleefleague.core.command.annotation.LiteralArg;
import com.spleefleague.core.command.CommandTemplate;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;
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
import com.spleefleague.core.menu.InventoryMenuContainer;

/**
 * @author NickM13
 */
public class VendorCommand extends CommandTemplate {
    
    public VendorCommand() {
        super(VendorCommand.class, "vendor", Rank.DEVELOPER);
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
    public void vendorList(CorePlayer sender,
            @LiteralArg(value="list") String l) {
        String vendorlist = "";
        Iterator<String> vit = Vendors.getVendors().keySet().iterator();
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
        
        System.out.println(Vendorable.getTypeNames().size());
        for (String type : Vendorable.getTypeNames()) {
            InventoryMenuItem typeItem = InventoryMenuAPI.createItem()
                    .setName(type)
                    .setDisplayItem(Material.CHEST);
            InventoryMenuContainer typeMenu = typeItem
                    .getLinkedContainer()
                    .setTitle(type);
            Map<String, Vendorable> vendorableMap = Vendorables.getAll(type);
            if (vendorableMap != null) {
                for (Vendorable item : vendorableMap.values()) {
                    typeMenu.addMenuItem(item.getVendorMenuItem());
                }
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
        Vendor vendor = Vendors.getVendor(name);
        if (vendor != null) {
            vendor.openShop(cp);
        }
    }
    
    @CommandAnnotation
    public void vendorSet(CorePlayer sender,
            @LiteralArg(value="set") String l,
            @OptionArg(listName="vendors") String name) {
        Vendors.setPlayerVendor(sender, name);
    }
    
    @CommandAnnotation
    public void vendorUnset(CorePlayer sender,
            @LiteralArg(value="unset") String l) {
        Vendors.unsetPlayerVendor(sender);
    }
    
    @CommandAnnotation
    public void vendorDelete(CorePlayer sender,
            @LiteralArg(value="delete") String l,
            @OptionArg(listName="vendors") String name) {
        Vendors.deleteVendor(Vendors.getVendor(name));
    }

}
