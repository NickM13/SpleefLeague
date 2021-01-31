/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.hotbars.HeldItemHotbar;
import com.spleefleague.core.menu.hotbars.main.CollectiblesMenu;
import com.spleefleague.core.menu.hotbars.main.HeldItemMenu;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.CollectibleSkin;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.player.collectible.pet.Pet;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.spleef.Spleef;
import java.util.Set;

import com.spleefleague.spleef.util.SpleefUtils;
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
    
    private static MongoCollection<Document> shovelCol;
    private static NBTTagList canDestroyTags = new NBTTagList();
    
    public static void init() {
        SpleefUtils.breakableBlocks.forEach(mat -> canDestroyTags.add(NBTTagString.a("minecraft:" + mat.name().toLowerCase())));
        
        Vendorable.registerParentType(Shovel.class);

        boolean hasDefault = false;
        shovelCol = Spleef.getInstance().getPluginDB().getCollection("Shovels");
        for (Document doc : shovelCol.find()) {
            Shovel shovel = new Shovel();
            shovel.load(doc);
            if (shovel.getIdentifier().equals("default")) {
                hasDefault = true;
            }
        }
        if (!hasDefault) {
            Shovel.create(Shovel.class, "default", "Default Shovel");
        }
        
        InventoryMenuAPI.createItemHotbar(0, "shovelHotbarItem")
                .setName(cp -> cp.getCollectibles().getActiveName(Shovel.class))
                .setDisplayItem(cp -> {
                    if (cp.getBattle().getGameWorld().isEditable()) {
                        return cp.getCollectibles().getActive(Shovel.class).getGameItem();
                    } else {
                        return cp.getCollectibles().getActiveIcon(Shovel.class);
                    }
                })
                .setDescription(cp -> cp.getCollectibles().getActive(Shovel.class).getDescription())
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle().getPlugin() instanceof Spleef);
    }
    
    public static void save(Shovel shovel) {
        unsave(shovel.getIdentifier());
        shovelCol.insertOne(shovel.toDocument());
    }
    
    // Better name? I don't think so
    public static void unsave(String identifier) {
        if (shovelCol.find(new Document("identifier", identifier)).first() != null) {
            shovelCol.deleteMany(new Document("identifier", identifier));
        }
    }
    
    public static void createMenu() {
        InventoryMenuItem shovelMenu = CorePlayerCollectibles.createCollectibleContainer(Shovel.class,
                InventoryMenuAPI.createItemDynamic()
                        .setName("Shovels")
                        .setDescription("Set your active shovel")
                        .setDisplayItem(Material.DIAMOND_SHOVEL, 10));

        CollectiblesMenu.getItem().getLinkedChest().addStaticItem(shovelMenu, 6, 2);

        HeldItemMenu.getItem().getLinkedChest().addStaticItem(shovelMenu, 6, 2);
        HeldItemMenu.getItem().getLinkedChest().addStaticItem(CorePlayerCollectibles.createActiveMenuItem(Shovel.class), 6, 3);
        HeldItemMenu.getItem().getLinkedChest().addStaticItem(CorePlayerCollectibles.createToggleMenuItem(Shovel.class), 6, 4);
    }
    
    protected enum ShovelType {
        DEFAULT,
        HIDDEN,
        EVENT,
        TOURNAMENT,
        SHOP
    }
    
    public static Set<String> getShovelTypes() {
        return CoreUtils.enumToStrSet(ShovelType.class, false);
    }

    @DBField private ShovelType shovelType;
    
    public Shovel() {
        super();
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
    }
    
    public Shovel(String identifier, String displayName) {
        super();
        this.identifier = identifier;
        this.name = displayName;
        this.material = Material.DIAMOND_SHOVEL;
        this.shovelType = ShovelType.DEFAULT;
    }

    @Override
    public boolean isDefault(CorePlayer cp) {
        return shovelType.equals(ShovelType.DEFAULT) || super.isDefault(cp);
    }

    public void setShovelType(String type) {
        shovelType = ShovelType.valueOf(type);
    }
    public ShovelType getShovelType() {
        return shovelType;
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
        tagCompound.set("CanDestroy", canDestroyTags);
        nmsItemStack.setTag(tagCompound);

        ItemStack itemStack = CraftItemStack.asBukkitCopy(nmsItemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) itemMeta.addEnchant(Enchantment.DIG_SPEED, 9, true);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public void saveChanges() {
        unsave();
        shovelCol.insertOne(toDocument());
    }

    @Override
    public void unsave() {
        Document query = (new Document("type", this.type)).append("identifier", this.identifier);
        if (shovelCol.find(query).first() != null) {
            shovelCol.deleteMany(query);
        }
    }
    
}
