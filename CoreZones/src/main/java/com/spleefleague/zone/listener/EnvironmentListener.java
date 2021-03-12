package com.spleefleague.zone.listener;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.gear.Gear;
import com.spleefleague.zone.gear.hookshot.GearHookshot;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.zones.Zone;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class EnvironmentListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null || !CoreZones.getInstance().getPlayers().isLocal(event.getPlayer().getUniqueId())) return;
        CoreZones.getInstance().getZoneManager().onPlayerMove(event.getPlayer(), event.getTo());
        CoreZones.getInstance().getFragmentManager().onPlayerMove(event.getPlayer(), event.getTo());
        CoreZones.getInstance().getMonumentManager().onPlayerMove(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (CoreZones.getInstance().getPlayers().isLocal(event.getPlayer().getUniqueId())) return;
        if (event.isSneaking()) {
            GearHookshot.onPlayerSneak(Core.getInstance().getPlayers().get(event.getPlayer()));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        CoreZones.getInstance().getFragmentManager().onPlayerJoin(event.getPlayer().getUniqueId());
        GearHookshot.onPlayerJoin(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED ||
                    event.getRegainReason() == EntityRegainHealthEvent.RegainReason.EATING) {
                ZonePlayer zonePlayer = CoreZones.getInstance().getPlayers().get(event.getEntity().getUniqueId());
                Zone zone = zonePlayer.getZone();
                if (zone != null && !zone.allowNaturalRegen()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerSlotChange(PlayerItemHeldEvent event) {
        Gear gear = Vendorables.get(Gear.class, event.getPlayer().getInventory().getItemInMainHand());
        if (gear != null) {

        }
    }

}
