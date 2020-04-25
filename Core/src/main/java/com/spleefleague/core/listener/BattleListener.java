/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.spleefleague.core.Core;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * @author NickM13
 */
public class BattleListener implements Listener {
    
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        //CorePlugin.getBattleGlobal(dbp.getPlayer()).onSlotChange(dbp, event.getNewSlot());
    }

    /**
     * Prevent in-battle damage
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            CorePlayer cp = Core.getInstance().getPlayers().get((Player) event.getEntity());
            if (cp.isInBattle()) {
                cp.getPlayer().setHealth(20);
                cp.getPlayer().setFireTicks(0);
                event.setCancelled(true);
            }
        }
    }

    /**
     * Check movement of ingame player
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (cp.isInBattle()) {
            cp.getBattle().onMove(cp, event);
        }
    }

    /**
     * Check in-battle player interaction events
     * TODO: Move this to packet adaptors?
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (cp.isInBattle()) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                cp.getBattle().onRightClick(cp);
            }
        }
    }

    /**
     * Prevent player from leaving spectator mode
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerStopSpectate(PlayerToggleSneakEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (cp.getPlayer().getGameMode() == GameMode.SPECTATOR &&
                cp.getBattleState() == BattleState.SPECTATOR) {
            event.setCancelled(true);
        }
    }

    /**
     * When a player is teleported, hard fix the spigot spectator bug
     * TODO: Find better way to do this
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (cp.getBattleState() == BattleState.BATTLER) {
            cp.getBattle().fixSpectators(cp);
        }
    }
    
}
