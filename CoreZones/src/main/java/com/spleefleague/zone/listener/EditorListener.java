package com.spleefleague.zone.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.zone.CoreZones;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class EditorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent event) {
        if (!Core.getInstance().getPlayers().get(event.getPlayer()).getRank().hasPermission(CoreRank.DEVELOPER)) return;
        ItemStack item = event.getItem();
        if (item != null) {
            if (item.getType().equals(Material.IRON_AXE)) {
                Bukkit.getScheduler().runTaskAsynchronously(CoreZones.getInstance(), () -> {
                    if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        CoreZones.getInstance().getZoneManager().onPlayerRightClick(
                                Core.getInstance().getPlayers().get(event.getPlayer()),
                                new Point(event.getPlayer().getEyeLocation()),
                                event.getPlayer().getLocation().getDirection());
                        event.setCancelled(true);
                    } else if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        CoreZones.getInstance().getZoneManager().onPlayerLeftClick(
                                Core.getInstance().getPlayers().get(event.getPlayer()),
                                new Point(event.getPlayer().getEyeLocation()),
                                event.getPlayer().getLocation().getDirection());
                        event.setCancelled(true);
                    }
                });
            } else if (item.getType().equals(Material.GOLDEN_AXE)) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    CoreZones.getInstance().getFragmentManager().onPlayerRightClick(
                            Core.getInstance().getPlayers().get(event.getPlayer()),
                            new Point(event.getPlayer().getEyeLocation()),
                            event.getPlayer().getLocation().getDirection());
                    event.setCancelled(true);
                } else if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                    CoreZones.getInstance().getFragmentManager().onPlayerLeftClick(
                            Core.getInstance().getPlayers().get(event.getPlayer()),
                            new Point(event.getPlayer().getEyeLocation()),
                            event.getPlayer().getLocation().getDirection());
                    event.setCancelled(true);
                }
            }
        }
    }

}
