/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.VendorItem;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * @author NickM13
 */
public class Shovel extends VendorItem {
    
    private static final Map<Integer, Shovel> shovels = new HashMap<>();
    private static MongoCollection<Document> shovelCollection;
    
    public static void init() {
        shovelCollection = Spleef.getInstance().getPluginDB().getCollection("Shovels");
        shovelCollection.find().iterator().forEachRemaining(doc -> {
            Shovel shovel = new Shovel();
            shovel.load(doc);
            shovels.put(shovel.getDamage(), shovel);
        });
    }
    
    public static void save(Shovel shovel) {
        if (shovelCollection.find(new Document("damage", shovel.getDamage())).first() != null) {
            shovelCollection.replaceOne(new Document("damage", shovel.getDamage()), shovel.save());
        } else {
            shovelCollection.insertOne(shovel.save());
        }
    }
    
    // Better name? I don't think so
    public static void unsave(int damage) {
        if (shovelCollection.find(new Document("damage", damage)).first() != null) {
            shovelCollection.deleteMany(new Document("damage", damage));
        }
    }
    
    public static boolean isShovel(ItemStack item) {
        if (item != null && item.getType().equals(Material.DIAMOND_SHOVEL)) {
            int id = ((Damageable) item.getItemMeta()).getDamage();
            return (shovels.containsKey(id));
        }
        return false;
    }
    
    public static boolean createShovel(int id) {
        if (shovels.containsKey(id)) return false;
        Shovel shovel = new Shovel(id);
        shovels.put(id, shovel);
        Shovel.save(shovel);
        VendorItem.addVendorItem(shovel);
        return true;
    }
    
    public static boolean destroyShovel(int damage) {
        if (shovels.containsKey(damage)) {
            VendorItem.removeVendorItem(shovels.get(damage));
            shovels.remove(damage);
            Shovel.unsave(damage);
            return true;
        } else {
            return false;
        }
    }
    
    public static Shovel getShovel(int damage) {
        return shovels.get(damage);
    }
    public static String getShovelName(int damage) {
        if (shovels.containsKey(damage)) {
            return shovels.get(damage).getDisplayName() + Chat.DEFAULT;
        }
        return "" + damage;
    }
    
    public static Shovel getDefault() {
        for (Shovel s : shovels.values()) {
            if (s.isDefault()) {
                return s;
            }
        }
        return null;
    }
    
    private static InventoryMenuItem createActiveShovelMenuItem() {
        return InventoryMenuAPI.createItem()
                .setName(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveShovel().getDisplayName();
                }).setDescription(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveShovel().getDescription();
                }).setDisplayItem(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActiveShovel().getItem();
                }).setCloseOnAction(false);
    }
    
    public static InventoryMenuItem createMenuTyped(InventoryMenuItem menuItem, ShovelType shovelType) {
        for (Shovel shovel : shovels.values()) {
            if (shovel.getShovelType().equals(shovelType)) {
                InventoryMenuItem smi = InventoryMenuAPI.createItem()
                        .setName(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            return sp.hasShovel(shovel.getDamage()) ? shovel.getDisplayName() : "Locked";
                        })
                        .setDisplayItem(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            return sp.hasShovel(shovel.getDamage()) ? shovel.getItem() : InventoryMenuAPI.getLockedIcon();
                        })
                        .setDescription(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            return sp.hasShovel(shovel.getDamage()) ? shovel.getDescription() : "";
                        })
                        .setAction(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            sp.setActiveShovel(shovel.damage);
                        })
                        .setCloseOnAction(false);
                menuItem.getLinkedContainer().addMenuItem(smi);
            }
        }
        menuItem.getLinkedContainer().addStaticItem(createActiveShovelMenuItem(), 4, 4);
        return menuItem;
    }
    
    public static InventoryMenuItem createMenu() {
        InventoryMenuItem shovelMenu = InventoryMenuAPI.createItem()
                .setName("Shovels")
                .setDescription("Set your active shovel")
                .setDisplayItem(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.getActiveShovel().getItem();
                        })
                .createLinkedContainer("Active Shovel");
        shovelMenu.getLinkedContainer()
                .addStaticItem(createActiveShovelMenuItem(), 4, 4);
        
        shovelMenu.getLinkedContainer().addMenuItem(createMenuTyped(InventoryMenuAPI.createItem()
                .setName("Default Shovels")
                .setDescription("Shovels you have unlocked by default!")
                .setDisplayItem(Material.LIGHT_BLUE_BANNER)
                .createLinkedContainer("Default Shovels"), ShovelType.DEFAULT), 4, 2);
        
        shovelMenu.getLinkedContainer().addMenuItem(createMenuTyped(InventoryMenuAPI.createItem()
                .setName("Hidden Shovels")
                .setDescription("Shh-ovels!")
                .setDisplayItem(Material.BLACK_BANNER)
                .createLinkedContainer("Hidden Shovels"), ShovelType.HIDDEN), 3, 3);
        
        shovelMenu.getLinkedContainer().addMenuItem(createMenuTyped(InventoryMenuAPI.createItem()
                .setName("Event Shovels")
                .setDescription("Unlock these by attending special events!")
                .setDisplayItem(Material.RED_BANNER)
                .createLinkedContainer("Event Shovels"), ShovelType.EVENT), 2, 2);
        
        shovelMenu.getLinkedContainer().addMenuItem(createMenuTyped(InventoryMenuAPI.createItem()
                .setName("Tournament Shovels")
                .setDescription("Unlock these by winning tournaments!")
                .setDisplayItem(Material.ORANGE_BANNER)
                .createLinkedContainer("Tournament Shovels"), ShovelType.TOURNAMENT), 5, 3);
        
        shovelMenu.getLinkedContainer().addMenuItem(createMenuTyped(InventoryMenuAPI.createItem()
                .setName("Purchased Shovels")
                .setDescription("Shovels you have unlocked by default!")
                .setDisplayItem(Material.GREEN_BANNER)
                .createLinkedContainer("Purchased Shovels"), ShovelType.SHOP), 6, 2);
        
        return shovelMenu;
    }
    
    private enum ShovelType {
        DEFAULT,
        HIDDEN,
        EVENT,
        TOURNAMENT,
        SHOP
    }
    
    public static Set<String> getShovelTypes() {
        return CoreUtils.enumToSet(ShovelType.class);
    }
    
    private ShovelType shovelType;
    
    public Shovel() {
        super("shovel");
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
    }
    public Shovel(int damage) {
        super("shovel");
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
        this.damage = damage;
        this.identifier = String.valueOf(damage);
    }
    
    @Override
    public void load(Document doc) {
        super.load(doc);
        this.identifier = String.valueOf(damage);
        addVendorItem(this);
    }
    
    @DBLoad(fieldname="type")
    public void loadType(String str) {
        shovelType = ShovelType.valueOf(str);
    }
    
    public boolean isDefault() {
        return shovelType.equals(ShovelType.DEFAULT);
    }
    public void setShovelType(String type) {
        shovelType = ShovelType.valueOf(type);
    }
    public ShovelType getShovelType() {
        return shovelType;
    }
    
    @Override
    public boolean isUnlocked(CorePlayer cp) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        return (sp.hasShovel(getDamage()) || isDefault());
    }
    @Override
    public boolean isPurchaseable(CorePlayer cp) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        return cp.getCoins() >= getCoinCost();
    }
    @Override
    public void purchase(CorePlayer cp) {
        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp);
        sp.addShovel(getDamage());
        cp.addCoins(-getCoinCost());
    }
    
}
