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
import com.spleefleague.splegg.game.classic.ClassicSpleggBattle;
import com.spleefleague.splegg.game.multi.MultiSpleggBattle;
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
        int count = 0;
        for (Document doc : spleggGunCol.find()) {
            SpleggGun spleggGun = new SpleggGun();
            spleggGun.load(doc);
            Vendorables.register(spleggGun);
            count++;
        }
        if (count == 0) {
            create(SpleggGun.class, "henholster", "Hen Holster");
        }

        InventoryMenuAPI.createItemHotbar(0, "spleggGunHotbarItemS1")
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s1", SpleggGun.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s1", SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s1", SpleggGun.getDefault()).getDisplayItem())
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle().getPlugin() instanceof Splegg
                        && cp.getBattle() instanceof ClassicSpleggBattle);

        InventoryMenuAPI.createItemHotbar(1, "spleggGunHotbarItemS2")
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s2", SpleggGun.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s2", SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "s2", SpleggGun.getDefault()).getDisplayItem())
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle().getPlugin() instanceof Splegg
                        && cp.getBattle() instanceof ClassicSpleggBattle);

        InventoryMenuAPI.createItemHotbar(0, "spleggGunHotbarItemM1")
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "m1", SpleggGun.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "m1", SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "m1", SpleggGun.getDefault()).getDisplayItem())
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle().getPlugin() instanceof Splegg
                        && cp.getBattle() instanceof MultiSpleggBattle);

        InventoryMenuAPI.createItemHotbar(1, "spleggGunHotbarItemM2")
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "m2", SpleggGun.getDefault()).getName())
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "m2", SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, "m2", SpleggGun.getDefault()).getDisplayItem())
                .setAvailability(cp -> cp.isInBattle()
                        && cp.getBattleState() == BattleState.BATTLER
                        && cp.getBattle().getPlugin() instanceof Splegg
                        && cp.getBattle() instanceof MultiSpleggBattle);
    }

    public static SpleggGun getDefault() {
        Collection<SpleggGun> collection = Vendorables.getAll(SpleggGun.class).values();
        return collection.iterator().next();
    }

    private static InventoryMenuItem createActiveSpleggGunMenuItem(String affix) {
        return InventoryMenuAPI.createItem()
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, affix, SpleggGun.getDefault()).getName() + "")
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, affix, SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, affix, SpleggGun.getDefault()).getDisplayItem())
                .setCloseOnAction(false);
    }

    public static InventoryMenuItem createMenu(String affix, String secondary) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, affix, SpleggGun.getDefault()).getName() + " &6&iClick to Change")
                .setDescription(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, affix, SpleggGun.getDefault()).getDescription())
                .setDisplayItem(cp -> cp.getCollectibles().getActiveOrDefault(SpleggGun.class, affix, SpleggGun.getDefault()).getDisplayItem())
                .createLinkedContainer("Active Splegg Gun");

        menuItem.getLinkedChest().setPageBoundaries(1, 3, 1, 7);

        menuItem.getLinkedChest().addStaticItem(createActiveSpleggGunMenuItem(affix), 4, 4);

        for (SpleggGun spleggGun : Vendorables.getAll(SpleggGun.class).values()) {
            InventoryMenuItem smi = InventoryMenuAPI.createItem()
                    .setName(cp -> spleggGun.isAvailable(cp) ? spleggGun.getName() : "Locked")
                    .setDisplayItem(cp -> spleggGun.isAvailable(cp) ? spleggGun.getDisplayItem() : InventoryMenuUtils.getLockedIcon())
                    .setDescription(cp -> spleggGun.isAvailable(cp) ? spleggGun.getDescription() : "")
                    .setAction(cp -> {
                        if (spleggGun.isAvailable(cp)) {
                            if (cp.getCollectibles().getActiveOrDefault(SpleggGun.class, secondary, SpleggGun.getDefault()).equals(spleggGun)) {
                                cp.getCollectibles().setActiveItem(cp.getCollectibles().getActiveOrDefault(SpleggGun.class, affix, SpleggGun.getDefault()), secondary);
                            }
                            cp.getCollectibles().setActiveItem(spleggGun, affix);
                        }
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

    public void resetStats() {
        projectileStats = new ProjectileStats();
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
