package com.spleefleague.core.vendor;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.menu.InventoryMenuEditor;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.database.variable.DBEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author NickM13
 */
public class Vendor extends DBEntity {
    
    private static Map<String, Vendor> vendors = new HashMap<>();
    private static MongoCollection<Document> vendorCol;
    private static Map<UUID, Vendor> entityVendors = new HashMap<>();
    private static Map<CorePlayer, Vendor> playerVendors = new HashMap<>();
    
    public static void init() {
        vendorCol = Core.getInstance().getPluginDB().getCollection("Vendors");
        vendorCol.find().iterator().forEachRemaining(doc -> {
            Vendor vendor = new Vendor();
            vendor.load(doc);
            vendor.refreshEntities();
            vendors.put(vendor.getName().toLowerCase(), vendor);
        });
    }
    
    public static void close() {
        vendorCol.deleteMany(new Document());
        vendors.values().forEach(vendor -> {
            vendor.saveVendor();
        });
    }
    
    public static Vendor getVendor(String name) {
        return vendors.get(name.toLowerCase());
    }
    
    public static Map<String, Vendor> getVendors() {
        return vendors;
    }
    
    public static void createVendor(String name, String displayName) {
        vendors.put(name.toLowerCase(), new Vendor(name, displayName));
    }
    
    public static void deleteVendor(String name) {
        vendors.get(name.toLowerCase()).clearEntities();
        vendors.remove(name.toLowerCase());
    }
    
    public static boolean setPlayerVendor(CorePlayer player, String name) {
        Vendor vendor = vendors.get(name.toLowerCase());
        if (vendor != null) {
            playerVendors.put(player, vendors.get(name.toLowerCase()));
            return true;
        }
        return false;
    }
    
    public static boolean unsetPlayerVendor(CorePlayer player) {
        playerVendors.put(player, null);
        return true;
    }
    
    public static void interactEvent(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        CorePlayer cp = Core.getInstance().getPlayers().get(p);
        Entity entity = e.getRightClicked();
        if (entityVendors.containsKey(entity.getUniqueId())) {
            entityVendors.get(entity.getUniqueId()).openShop(cp);
        }
    }
    public static void punchEvent(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            return;
        }
        Player p = (Player) e.getDamager();
        CorePlayer cp = Core.getInstance().getPlayers().get(p);
        if (playerVendors.containsKey(cp)) {
            if (playerVendors.get(cp) == null) {
                Vendor vendor = entityVendors.get(e.getEntity().getUniqueId());
                if (vendor != null) {
                    vendor.removeEntity(e.getEntity().getUniqueId());
                }
            } else {
                playerVendors.get(cp).addEntity(e.getEntity());
            }
            playerVendors.remove(cp);
            e.setCancelled(true);
        } else if (entityVendors.containsKey(e.getEntity().getUniqueId())) {
            e.setCancelled(true);
        }
    }
    
    @DBField
    private String name;
    @DBField
    private String displayName;
    private class SimpleVendorItem {
        String type;
        String id;
        
        SimpleVendorItem(String type, String id) {
            this.type = type;
            this.id = id;
        }
    }
    private final Map<Integer, SimpleVendorItem> items = new HashMap<>();
    @DBField
    private static Set<UUID> entities = new HashSet<>();
    
    public Vendor() {
        super();
    }
    
    public Vendor(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }
    
    public void openShop(CorePlayer cp) {
        cp.setInventoryMenuContainer(createVendorMenu(cp));
    }
    
    public void setItem(int slot, VendorItem item) {
        items.put(slot, new SimpleVendorItem(item.getType(), item.getIdentifier()));
    }
    public void saveVendor() {
        if (vendorCol.find(new Document("name", getName())).first() != null) {
            vendorCol.deleteMany(new Document("name", getName()));
        }
        vendorCol.insertOne(save());
    }
    
    public void removeEntity(UUID entityId) {
        entities.remove(entityId);
        entityVendors.remove(entityId);
        Entity e = Bukkit.getEntity(entityId);
        e.setCustomName("");
        e.setCustomNameVisible(false);
        if (e instanceof LivingEntity) {
            ((LivingEntity) e).setAI(true);
        }
        e.setInvulnerable(false);
    }
    public void addEntity(Entity entity) {
        UUID entityUuid = entity.getUniqueId();
        if (entityVendors.containsKey(entityUuid)
                && entityVendors.get(entityUuid) != this)
            entityVendors.get(entityUuid).removeEntity(entityUuid);
        entityVendors.put(entityUuid, this);
        entities.add(entityUuid);
        entity.setCustomName(displayName);
        entity.setCustomNameVisible(true);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setAI(false);
        }
        entity.setInvulnerable(true);
    }
    public void refreshEntities() {
        Set<Entity> entitySet = new HashSet<>();
        
        Iterator<UUID> uit = entities.iterator();
        while (uit.hasNext()) {
            Entity e = Bukkit.getEntity(uit.next());
            if (e == null) {
                uit.remove();
            } else {
                entitySet.add(e);
            }
        }
        for (Entity e : entitySet) {
            addEntity(e);
        }
    }
    public void clearEntities() {
        Set<Entity> entitySet = new HashSet<>();
        
        Iterator<UUID> uit = entities.iterator();
        while (uit.hasNext()) {
            Entity e = Bukkit.getEntity(uit.next());
            if (e == null) {
                uit.remove();
            } else {
                entitySet.add(e);
            }
        }
        for (Entity e : entitySet) {
            removeEntity(e.getUniqueId());
        }
    }
    
    public void edit(CorePlayer cp1) {
        InventoryMenuEditor editor = (InventoryMenuEditor) InventoryMenuAPI.createEditor()
                .setSaveFun((invItems) -> {
                    items.clear();
                    for (Map.Entry<Integer, InventoryMenuItem> item : invItems.entrySet()) {
                        ItemStack itemStack = item.getValue().createItem(cp1);
                        VendorItem vi = VendorItem.getVendorItem(itemStack);
                        if (vi != null) {
                            SimpleVendorItem svi = new SimpleVendorItem(vi.getType(), vi.getIdentifier());
                            items.put(item.getKey(), svi);
                        } else {
                            cp1.sendMessage("Item is not a valid vendor item: " + itemStack.getItemMeta().getDisplayName());
                        }
                    }
                    saveVendor();
                })
                .setOpenAction((container, cp2) -> {
                    container.clearSorted();
                    for (Map.Entry<Integer, SimpleVendorItem> item : items.entrySet()) {
                        VendorItem vendorItem = VendorItem.getVendorItem(item.getValue().type, item.getValue().id);
                        if (vendorItem != null) {
                            container.addMenuItem(InventoryMenuAPI.createItem()
                                    .setName(vendorItem.getDisplayName())
                                    .setDescription(vendorItem.getVendorDescription())
                                    .setDisplayItem(vendorItem.getItem()), item.getKey());
                        }
                    }
                })
                .setTitle(this.getName() + "(" + this.getDisplayName() + ")");
        
        cp1.setInventoryMenuContainer(editor);
    }
    
    protected InventoryMenuContainer createVendorMenu(CorePlayer cp) {
        InventoryMenuContainer menu = InventoryMenuAPI.createContainer()
                .setTitle(getDisplayName());
        
        for (Map.Entry<Integer, SimpleVendorItem> item : items.entrySet()) {
            VendorItem vendorItem = VendorItem.getVendorItem(item.getValue().type, item.getValue().id);
            if (vendorItem != null) {
                menu.addMenuItem(InventoryMenuAPI.createItem()
                        .setName(vendorItem.getDisplayName())
                        .setDescription(vendorItem.getVendorDescription())
                        .setDisplayItem(vendorItem.getItem())
                        .setAction(cp2 -> {
                            if (vendorItem.isUnlocked(cp)) {
                                Core.getInstance().sendMessage(cp, "You already own that!");
                            } else if (vendorItem.isPurchaseable(cp)) {
                                vendorItem.purchase(cp);
                            } else {
                                Core.getInstance().sendMessage(cp, "You can't buy that!");
                            }
                        }), item.getKey());
            } else {
                menu.addMenuItem(InventoryMenuAPI.createItem()
                        .setName("Unknown Item")
                        .setDescription("")
                        .setDisplayItem(Material.BARRIER)
                        .setAction(p -> {}), item.getKey());
            }
        }
        
        return menu;
    }
    
    @DBLoad(fieldName ="items")
    protected void loadItems(List<Document> items) {
        for (Document doc : items) {
            int slot = doc.get("slot", Integer.class);
            String type = doc.get("type", String.class);
            String id = doc.get("id", String.class);
            this.items.put(slot, new SimpleVendorItem(type, id));
        }
    }
    @DBSave(fieldName ="items")
    protected List<Document> saveItems() {
        List<Document> docs = new ArrayList<>();
        for (Map.Entry<Integer, SimpleVendorItem> item : items.entrySet()) {
            docs.add(new Document("type", item.getValue().type).append("id", item.getValue().id).append("slot", item.getKey()));
        }
        return docs;
    }
    
    public String getName() {
        return name;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        refreshEntities();
    }
    public String getDisplayName() {
        return displayName;
    }
    
}
