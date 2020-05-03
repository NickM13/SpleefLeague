package com.spleefleague.core.vendor;

import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
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
import org.bukkit.inventory.ItemStack;

/**
 * Vendor is a type that can be applied to all living entities
 *
 * @author NickM13
 */
public class Vendor extends DBEntity {
    
    @DBField
    private String name;
    @DBField
    private String displayName;
    private static class SimpleVendorItem {
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
    
    /**
     * Opens the shop of this vendor for a player
     *
     * @param cp Core Player
     */
    public void openShop(CorePlayer cp) {
        cp.setInventoryMenuChest(createVendorMenu(cp), true);
    }
    
    /**
     * Sets the VendorItem of a slot in the vendor's shop
     *
     * @param slot Slot Number
     * @param vendorable Vendor Item
     */
    public void setItem(int slot, Vendorable vendorable) {
        items.put(slot, new SimpleVendorItem(vendorable.getType(), vendorable.getIdentifier()));
    }
    
    /**
     * Saves the current Vendor to the database
     */
    public void quicksave() {
        Vendors.save(this);
    }
    
    /**
     * Removes an entity from the controlled entity set
     *
     * @param entity Entity
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity.getUniqueId());
    }
    
    /**
     * Adds an entity to the controlled entity set
     * @param entity Entity
     */
    public void addEntity(Entity entity) {
        entities.add(entity.getUniqueId());
    }
    
    /**
     * Refreshes all entities to have the current display name of the vendor
     */
    public void refreshEntities() {
        Set<Entity> entitySet = new HashSet<>();
        
        Iterator<UUID> uit = entities.iterator();
        while (uit.hasNext()) {
            Entity entity = Bukkit.getEntity(uit.next());
            if (entity == null) {
                uit.remove();
            } else {
                entitySet.add(entity);
            }
        }
        for (Entity entity : entitySet) {
            addEntity(entity);
            Vendors.setupEntityVendor(this, entity);
        }
    }
    
    /**
     * Get the set of controlled entities by their Entity::getUniqueId
     *
     * @return Set of Entity UUIDs
     */
    public Set<UUID> getEntities() {
        return entities;
    }
    
    /**
     * Open the Vendor's shop for editing by moderators
     *
     * @param cp CorePlayer
     */
    public void edit(CorePlayer cp) {
        InventoryMenuEditor editor = (InventoryMenuEditor) InventoryMenuAPI.createEditor()
                .setSaveFun((invItems) -> {
                    items.clear();
                    for (Map.Entry<Integer, InventoryMenuItem> item : invItems.entrySet()) {
                        ItemStack itemStack = item.getValue().createItem(cp);
                        Vendorable vendorable = Vendorables.get(itemStack);
                        if (vendorable != null) {
                            SimpleVendorItem svi = new SimpleVendorItem(vendorable.getType(), vendorable.getIdentifier());
                            items.put(item.getKey(), svi);
                        } else {
                            cp.sendMessage("Item is not a valid vendor item: " + itemStack);
                        }
                    }
                    quicksave();
                })
                .setOpenAction((container, cp2) -> {
                    container.clearSorted();
                    for (Map.Entry<Integer, SimpleVendorItem> item : items.entrySet()) {
                        Vendorable vendorable = Vendorables.get(item.getValue().type, item.getValue().id);
                        if (vendorable != null) {
                            container.addMenuItem(vendorable.getVendorMenuItem(), item.getKey());
                        }
                    }
                })
                .setTitle(this.getName() + "(" + this.getDisplayName() + ")");
        
        cp.setInventoryMenuChest(editor, true);
    }
    
    /**
     * Create a player-specific vendor menu
     *
     * @param cp Core Player
     * @return Menu Container
     */
    protected InventoryMenuContainerChest createVendorMenu(CorePlayer cp) {
        InventoryMenuContainerChest menu = InventoryMenuAPI.createContainer()
                .setTitle(getDisplayName());
        
        for (Map.Entry<Integer, SimpleVendorItem> item : items.entrySet()) {
            Vendorable vendorable = Vendorables.get(item.getValue().type, item.getValue().id);
            if (vendorable != null) {
                menu.addMenuItem(vendorable.getVendorMenuItem(), item.getKey());
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
    
    /**
     * Get the identifying name of Vendor
     *
     * @return Name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the display name for Vendor
     *
     * @param displayName Display Name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        refreshEntities();
    }
    
    /**
     * Get the display name of Vendor
     * @return Display Name
     */
    public String getDisplayName() {
        return displayName;
    }
    
}
