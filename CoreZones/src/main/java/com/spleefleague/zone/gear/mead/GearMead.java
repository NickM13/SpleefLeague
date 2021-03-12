package com.spleefleague.zone.gear.mead;

import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.gear.Gear;
import com.spleefleague.zone.gear.hookshot.GearHookshot;
import com.spleefleague.zone.gear.hookshot.HookshotPlayer;
import com.spleefleague.zone.player.ZonePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/25/2021
 */
public class GearMead extends Gear {

    private static final Map<UUID, Long> MEAD_COOLDOWN = new HashMap<>();

    private static final int duration = 15;

    private ItemStack empty, full;

    public GearMead() {
        super(GearType.MEAD);
    }

    public GearMead(String identifier, String name) {
        super(GearType.MEAD, identifier, name);
        this.empty = applyPersistents(InventoryMenuUtils.createCustomItem(material, 1));
        this.full = applyPersistents(InventoryMenuUtils.createCustomItem(material, 2));
    }

    @Override
    public void afterLoad() {
        super.afterLoad();
        this.empty = applyPersistents(InventoryMenuUtils.createCustomItem(material, 1));
        this.full = applyPersistents(InventoryMenuUtils.createCustomItem(material, 2));
    }

    @Override
    protected void createGearItems() {

    }

    @Override
    protected boolean onActivate(CorePlayer corePlayer) {
        ZonePlayer zonePlayer = CoreZones.getInstance().getPlayers().get(corePlayer);
        if (zonePlayer.getOptions().getInteger("Gear:TreeStumpMead") > 0 && MEAD_COOLDOWN.getOrDefault(corePlayer.getUniqueId(), 0L) < System.currentTimeMillis()) {
            zonePlayer.getOptions().addInteger("Gear:TreeStumpMead", -1);
            // TODO: Glug glug glug
            corePlayer.getGlobalWorld().playSound(zonePlayer.getPlayer().getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 1, 0.75f);
            corePlayer.getPlayer().addPotionEffect(PotionEffectType.CONFUSION.createEffect(20 * duration, 0));
            MEAD_COOLDOWN.put(corePlayer.getUniqueId(), System.currentTimeMillis() + 1000L * duration);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getGearItem(CorePlayer corePlayer) {
        if (CoreZones.getInstance().getPlayers().get(corePlayer).getOptions().getInteger("Gear:TreeStumpMead") > 0) {
            return full;
        }
        return empty;
    }

}
