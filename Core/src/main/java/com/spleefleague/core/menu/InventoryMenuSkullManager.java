package com.spleefleague.core.menu;

import com.spleefleague.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InventoryMenuSkullManager {

    private static final Map<UUID, OfflinePlayer> uuidSkullMap = new HashMap<>();

    private static final Set<UUID> loadingSkulls = new HashSet<>();

    private static void loadSkull(UUID uuid) {
        if (!loadingSkulls.contains(uuid)) {
            loadingSkulls.add(uuid);
            Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {
                uuidSkullMap.put(uuid, Bukkit.getOfflinePlayer(uuid));
                Bukkit.getScheduler().runTask(Core.getInstance(), () -> {
                    loadingSkulls.remove(uuid);
                });
            });
        }
    }

    public static ItemStack getPlayerSkull(UUID uuid) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        if (uuidSkullMap.containsKey(uuid)) {
            skullMeta.setOwningPlayer(uuidSkullMap.get(uuid));
        } else {
            loadSkull(uuid);
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("Blaezon"));
        }
        skullItem.setItemMeta(skullMeta);
        return skullItem;
    }

}
