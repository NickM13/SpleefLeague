/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.splegg.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.game.projectile.ProjectileStats;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.splegg.Splegg;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Snowball;

import java.util.Collection;

/**
 * @author NickM13
 */
public class SpleggGun extends Holdable {

    private static MongoCollection<Document> spleggGunCol;
    
    public static void init() {
        Vendorable.registerVendorableType(SpleggGun.class);
        
        spleggGunCol = Splegg.getInstance().getPluginDB().getCollection("SpleggGuns");
        Integer count = 0;
        for (Document doc : spleggGunCol.find()) {
            SpleggGun spleggGun = new SpleggGun();
            spleggGun.load(doc);
            Vendorables.register(spleggGun);
            count++;
        }
        if (count == 0) {
            create(SpleggGun.class, "default", "Splegg Gun");
        }

        InventoryMenuAPI.createItemHotbar(0, "spleggGunHotbarItem")
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, SpleggGun.getDefault()).getDisplayItem())
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle().getPlugin() instanceof Splegg);
    }

    public static SpleggGun getDefault() {
        Collection<SpleggGun> collection = Vendorables.getAll(SpleggGun.class).values();
        return collection == null ? null : collection.iterator().next();
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

        menuItem.getLinkedChest().addStaticItem(createActiveSpleggGunMenuItem(), 4, 4);

        for (SpleggGun spleggGun : Vendorables.getAll(SpleggGun.class).values()) {
            InventoryMenuItem smi = InventoryMenuAPI.createItem()
                    .setName(cp -> spleggGun.isAvailable(cp) ? spleggGun.getName() : "Locked")
                    .setDisplayItem(cp -> spleggGun.isAvailable(cp) ? spleggGun.getDisplayItem() : InventoryMenuUtils.getLockedIcon())
                    .setDescription(cp -> spleggGun.isAvailable(cp) ? spleggGun.getDescription() : "")
                    .setAction(cp -> {
                        if (spleggGun.isAvailable(cp))
                            cp.getCollectibles().setActiveItem(spleggGun);
                    })
                    .setCloseOnAction(false);
                menuItem.getLinkedChest().addMenuItem(smi);
        }
        
        return menuItem;
    }

    @DBField
    private ProjectileStats projectileStats;
    
    public SpleggGun() {
        super(false);
    }

    public SpleggGun(String identifier, String displayName) {
        super(false);
        this.material = Material.DIAMOND_SHOVEL;
        this.identifier = identifier;
        this.name = displayName;
        this.projectileStats = new ProjectileStats();
    }
    
    @Override
    public boolean isAvailableToPurchase(CorePlayer corePlayer) {
        return false;
    }
    
    @Override
    public void onRightClick(CorePlayer corePlayer) {
        corePlayer.getPlayer().getWorld().spawn(corePlayer.getPlayer().getEyeLocation(), Snowball.class, entity -> {
            entity.setVelocity(corePlayer.getPlayer().getLocation().getDirection().normalize().multiply(projectileStats.fireRange * 0.25f));
        });
    }

    @Override
    public String getDescription() {
        /*
        StringBuilder debugDesc = new StringBuilder("");
        for (Field field : ProjectileStats.class.getFields()) {
            try {
                if (field.isAnnotationPresent(Deprecated.class)) {
                    debugDesc.append("(disabled) " + field.getName() + ": " + field.get(getProjectileStats()) + "\n");
                } else {
                    debugDesc.append(field.getName() + ": " + field.get(getProjectileStats()) + "\n");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return debugDesc.toString();
        */
        return description;
    }
    
    @DBLoad(fieldName="projectile")
    private void loadProjectile(Document doc) {
        projectileStats = new ProjectileStats();
        projectileStats.load(doc);
    }
    
    public boolean isDefault() {
        return true;
    }
    
    public ProjectileStats getProjectileStats() {
        return projectileStats;
    }
    
    public boolean isAvailable(CorePlayer cp) {
        return isDefault() || cp.getCollectibles().contains(this);
    }

    @Override
    public void updateDisplayItem() {
        super.updateDisplayItem();
        projectileStats.updateProjectileItem();
    }

    @Override
    public void saveChanges() {
        save(spleggGunCol);
    }

    @Override
    public void unsave() {
        Document query = (new Document("identifier", this.identifier));
        if (spleggGunCol.find(query).first() != null) {
            spleggGunCol.deleteMany(query);
        }
    }
    
}
