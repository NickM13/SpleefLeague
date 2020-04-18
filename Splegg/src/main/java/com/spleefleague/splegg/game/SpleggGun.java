/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.vendor.VendorItem;
import com.spleefleague.core.world.FakeProjectile;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.player.SpleggPlayer;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author NickM13
 */
public class SpleggGun extends VendorItem {
    
    private static final Map<Integer, SpleggGun> spleggGuns = new TreeMap<>();
    private static ItemStack LOCKED_ICON;
    
    public static void init() {
        MongoCollection<Document> collection = Splegg.getInstance().getPluginDB().getCollection("SpleggGuns");
        collection.find().iterator().forEachRemaining(doc -> {
            SpleggGun spleggGun = new SpleggGun();
            spleggGun.load(doc);
            spleggGuns.put(spleggGun.getDamage(), spleggGun);
        });
        
        LOCKED_ICON = InventoryMenuAPI.createCustomItem(Material.DIAMOND_AXE, 12);
    }
    
    public static SpleggGun getSpleggGun(int id) {
        return spleggGuns.get(id);
    }
    
    public static SpleggGun getDefault() {
        for (SpleggGun sg : spleggGuns.values()) {
            return sg;
        }
        return null;
    }
    
    private static InventoryMenuItem createActiveSpleggGunMenuItem() {
        return InventoryMenuAPI.createItem()
                .setName(cp -> {
                    SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveSpleggGun().getDisplayName();
                }).setDescription(cp -> {
                    SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveSpleggGun().getDescription();
                }).setDisplayItem(cp -> {
                    SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveSpleggGun().getItem();
                }).setCloseOnAction(false);
    }
    
    public static InventoryMenuItem createMenu() {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("Splegg Guns")
                .setDescription("Set your active splegg gun")
                .setDisplayItem(cp -> {
                    SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveSpleggGun().getItem();
                }).createLinkedContainer("Active Splegg Gun");
        menuItem.getLinkedContainer().addStaticItem(createActiveSpleggGunMenuItem(), 4, 4);
        
        for (SpleggGun spleggGun : spleggGuns.values()) {
            InventoryMenuItem smi = InventoryMenuAPI.createItem()
                    .setName(cp -> {
                        SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.hasSpleggGun(spleggGun.getDamage()) ? spleggGun.getDisplayName() : "Locked";
                    })
                    .setDisplayItem(cp -> {
                        SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.hasSpleggGun(spleggGun.getDamage()) ? spleggGun.getItem() : LOCKED_ICON;
                    })
                    .setDescription(cp -> {
                        SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.hasSpleggGun(spleggGun.getDamage()) ? spleggGun.getDescription() : "";
                    })
                    .setAction(cp -> {
                        SpleggPlayer sp = Splegg.getInstance().getPlayers().get(cp.getPlayer());
                        sp.setActiveSpleggGun(spleggGun.damage);
                    })
                    .setCloseOnAction(false);
                menuItem.getLinkedContainer().addMenuItem(smi);
        }
        
        return menuItem;
    }
    
    private FakeProjectile projectile;
    
    public SpleggGun() {
        super("splegg");
        this.material = Material.DIAMOND_SHOVEL;
    }
    
    @DBLoad(fieldName="projectile")
    private void loadProjectile(Document doc) {
        projectile = new FakeProjectile();
        projectile.load(doc);
    }
    
    public boolean isDefault() {
        return true;
    }
    
    @Override
    public void load(Document doc) {
        super.load(doc);
        this.identifier = String.valueOf(damage);
        addVendorItem(this);
    }
    
    public FakeProjectile getProjectile() {
        return projectile;
    }
    
    @Override
    public void activate(CorePlayer cp) {
        cp.getPlayer().launchProjectile(Snowball.class);
    }
    
}
