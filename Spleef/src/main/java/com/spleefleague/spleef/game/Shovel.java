/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.spleef.Spleef;
import java.util.Set;

import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.NBTTagList;
import net.minecraft.server.v1_15_R1.NBTTagString;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public class Shovel extends Holdable {
    
    private static MongoCollection<Document> shovelCollection;
    
    public static void init() {
        Vendorable.registerVendorableType(Shovel.class);
        
        shovelCollection = Spleef.getInstance().getPluginDB().getCollection("Shovels");
        shovelCollection.find().iterator().forEachRemaining(doc -> {
            Shovel shovel = new Shovel();
            shovel.load(doc);
        });
        
        InventoryMenuAPI.createItemHotbar(0, "shovelHotbarItem")
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(Shovel.class, Shovel.getDefault()).getName())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(Shovel.class, Shovel.getDefault()).getGameItem())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(Shovel.class, Shovel.getDefault()).getDescription())
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle().getPlugin() instanceof Spleef);
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
    
    public static boolean createShovel(int damage) {
        String id = "" + damage;
        if (Vendorables.getAll(Shovel.class).containsKey(id)) return false;
        Shovel shovel = new Shovel(damage);
        Shovel.save(shovel);
        Vendorables.register(shovel);
        return true;
    }
    
    public static void destroyShovel(int damage) {
        if (Vendorables.getAll(Shovel.class).containsKey("" + damage)) {
            Vendorables.unregister(Shovel.class.getSimpleName(), "" + damage);
            Shovel.unsave(damage);
        }
    }
    
    public static Shovel getDefault() {
        for (Vendorable vendorable : Vendorables.getAll(Shovel.class).values()) {
            Shovel shovel = (Shovel) vendorable;
            if (shovel.isDefault()) {
                return shovel;
            }
        }
        return null;
    }
    
    private static InventoryMenuItem createActiveShovelMenuItem() {
        return InventoryMenuAPI.createItem()
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(Shovel.class, Shovel.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(Shovel.class, Shovel.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(Shovel.class, Shovel.getDefault()).getDisplayItem())
                .setCloseOnAction(false);
    }
    
    public static InventoryMenuItem createMenuTyped(InventoryMenuItem menuItem, ShovelType shovelType) {
        for (Vendorable vendorable : Vendorables.getAll(Shovel.class).values()) {
            Shovel shovel = (Shovel) vendorable;
            if (shovel.getShovelType().equals(shovelType)) {
                InventoryMenuItem smi = InventoryMenuAPI.createItem()
                        .setName(cp -> shovel.isUnlocked(cp) ? shovel.getName() : "Locked")
                        .setDisplayItem(cp -> shovel.isUnlocked(cp) ? shovel.getDisplayItem() : InventoryMenuUtils.getLockedIcon())
                        .setDescription(cp -> shovel.isUnlocked(cp) ? shovel.getDescription() : "")
                        .setAction(cp -> {
                            if (shovel.isUnlocked(cp))
                                cp.getCollectibles().setActiveItem(shovel);
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
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(Shovel.class, Shovel.getDefault()).getDisplayItem())
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
    
    protected enum ShovelType {
        DEFAULT,
        HIDDEN,
        EVENT,
        TOURNAMENT,
        SHOP
    }
    
    public static Set<String> getShovelTypes() {
        return CoreUtils.enumToSet(ShovelType.class);
    }

    @DBField private Integer damage;
    @DBField(fieldName="type") private ShovelType shovelType;
    
    public Shovel() {
        super(false);
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
    }
    
    public Shovel(int damage) {
        super(false);
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
        this.damage = damage;
        this.setDamageNbt(damage);
        this.identifier = String.valueOf(damage);
    }
    
    @Override
    public void afterLoad() {
        this.identifier = String.valueOf(damage);
        this.setDamageNbt(damage);
        super.afterLoad();
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
    
    public int getDamage() {
        return damage;
    }
    
    public boolean isUnlocked(CorePlayer corePlayer) {
        return isDefault() || corePlayer.getCollectibles().contains(this);
    }
    
    @Override
    public boolean isAvailableToPurchase(CorePlayer corePlayer) {
        return true;
    }

    /**
     * This is for the Held Item Collectible right click action, NOT ingame!
     *
     * @param corePlayer Core Player
     */
    @Override
    public void onRightClick(CorePlayer corePlayer) {

    }

    /**
     * Creates a shovel item that can only break snow while in adventure mode
     *
     * @return Shovel ItemStack
     */
    public ItemStack getGameItem() {
        net.minecraft.server.v1_15_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(getDisplayItem());
        NBTTagCompound tagCompound = nmsItemStack.hasTag() ? nmsItemStack.getTag() : new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        tagList.add(NBTTagString.a("minecraft:snow"));
        tagList.add(NBTTagString.a("minecraft:snow_block"));
        tagCompound.set("CanDestroy", tagList);
        nmsItemStack.setTag(tagCompound);

        ItemStack itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) itemMeta.addEnchant(Enchantment.DIG_SPEED, 4, true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    
}
