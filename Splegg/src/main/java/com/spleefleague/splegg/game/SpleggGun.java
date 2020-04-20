/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.FakeProjectile;
import com.spleefleague.splegg.Splegg;
import com.spleefleague.splegg.player.SpleggPlayer;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Egg;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author NickM13
 */
public class SpleggGun extends Holdable {
    
    private static final Map<Integer, SpleggGun> spleggGuns = new TreeMap<>();
    
    public static void init() {
        Vendorable.registerVendorableType(SpleggGun.class);
        
        MongoCollection<Document> collection = Splegg.getInstance().getPluginDB().getCollection("SpleggGuns");
        collection.find().iterator().forEachRemaining(doc -> {
            SpleggGun spleggGun = new SpleggGun();
            spleggGun.load(doc);
            spleggGuns.put(spleggGun.getDamage(), spleggGun);
        });
        
        InventoryMenuAPI.createItemHotbar(0, "spleggGunHotbarItem")
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getDisplayItem())
                .setAction(cp -> cp.sendMessage("hehe"))
                .setAvailability(cp -> {
                    return cp.isInBattle()
                            && cp.getBattleState() == BattleState.BATTLER
                            && cp.getBattle() instanceof SpleggBattle;
                });
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
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getDisplayItem())
                .setCloseOnAction(false);
    }
    
    public static InventoryMenuItem createMenu() {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName("Splegg Guns")
                .setDescription("Set your active splegg gun")
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getDisplayItem())
                .createLinkedContainer("Active Splegg Gun");
        menuItem.getLinkedContainer().addStaticItem(createActiveSpleggGunMenuItem(), 4, 4);
        
        for (SpleggGun spleggGun : spleggGuns.values()) {
            InventoryMenuItem smi = InventoryMenuAPI.createItem()
                    .setName(cp -> spleggGun.isAvailable(cp) ? spleggGun.getName() : "Locked")
                    .setDisplayItem(cp -> spleggGun.isAvailable(cp) ? spleggGun.getDisplayItem() : InventoryMenuUtils.getLockedIcon())
                    .setDescription(cp -> spleggGun.isAvailable(cp) ? spleggGun.getDescription() : "")
                    .setAction(cp -> {
                        if (spleggGun.isAvailable(cp))
                            cp.getCollectibles().activate(spleggGun);
                    })
                    .setCloseOnAction(false);
                menuItem.getLinkedContainer().addMenuItem(smi);
        }
        
        return menuItem;
    }
    
    @DBField private Integer damage;
    @DBField private FakeProjectile projectile;
    
    public SpleggGun() {
        super(false);
        this.material = Material.DIAMOND_SHOVEL;
    }
    
    @Override
    public void afterLoad() {
        this.identifier = String.valueOf(damage);
        this.setDamageNbt(damage);
        super.afterLoad();
    }
    
    @Override
    public boolean isAvailableToPurchase(CorePlayer corePlayer) {
        return false;
    }
    
    @Override
    public void onRightClick(CorePlayer corePlayer) {
        corePlayer.getPlayer().launchProjectile(Egg.class);
    }
    
    @DBLoad(fieldName="projectile")
    private void loadProjectile(Document doc) {
        projectile = new FakeProjectile();
        projectile.load(doc);
    }
    
    public boolean isDefault() {
        return true;
    }
    
    public FakeProjectile getProjectile() {
        return projectile;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public boolean isAvailable(CorePlayer cp) {
        return isDefault() || cp.getCollectibles().contains(this);
    }
    
}
