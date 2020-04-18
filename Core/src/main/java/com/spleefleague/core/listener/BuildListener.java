package com.spleefleague.core.listener;

import com.spleefleague.core.world.build.BuildWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Listener for players who are in a build world
 *
 * @author NickM13
 * @since 4/16/2020
 */
public class BuildListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        if (BuildWorld.isBuilder(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (BuildWorld.isBuilder(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
}
