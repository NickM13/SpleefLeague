/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.core.util.database.DBPlayer;
import com.spleefleague.core.vendor.Vendor;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class GameListener implements Listener {
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(p);
            if (dbp != null && dbp.getBattleState() == BattleState.BATTLER) {
                dbp.getBattle().checkProjectile(e);
            }
        }
    }
    
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        
        if (e.getItemDrop() != null && !cp.canBuild()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent e) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(e.getPlayer());
        if (dbp != null) {
            dbp.getBattle().onSlotChange(dbp, e.getNewSlot());
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(player);
            if (dbp != null) {
                player.setHealth(20);
                player.setFireTicks(0);
                e.setCancelled(true);
            } else {
                if (e.getCause().equals(DamageCause.FALL)) {
                    e.setDamage(e.getFinalDamage() / 2);
                }
            }
        }
    }
    
    @EventHandler
    public void onArmorEquip(InventoryClickEvent e) {
        if (e.getSlotType().equals(SlotType.ARMOR)) {
            ItemStack itemStack = e.getClickedInventory().getItem(e.getSlot());
            if (e.getClickedInventory().getHolder() instanceof Player) {
                Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                    Core.getInstance().getPlayers().get((Player)e.getClickedInventory().getHolder()).updateArmor();
                }, 10L);
            }
        }
    }
    
    private static final Set<EntityType> damageableMobs = Sets.newHashSet(EntityType.BLAZE, EntityType.BOAT, EntityType.CAVE_SPIDER,
            EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON,
            EntityType.EVOKER, EntityType.GHAST, EntityType.GIANT, EntityType.GUARDIAN, EntityType.HUSK, EntityType.ILLUSIONER, EntityType.MAGMA_CUBE,
            EntityType.MINECART, EntityType.PHANTOM, EntityType.PIG_ZOMBIE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER,
            EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH,
            EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER);
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.SNOWBALL) {
            if (e.getDamager() instanceof Player) {
                if (!Core.getInstance().getPlayers().get((Player)e.getDamager()).canBuild()) {
                    if (!damageableMobs.contains(e.getEntityType()))
                        e.setCancelled(true);
                } else {
                    Vendor.punchEvent(e);
                }
            } else if (!e.getEntityType().equals(EntityType.PLAYER) && !damageableMobs.contains(e.getEntityType())) {
                e.setCancelled(true);
            }
        }
    }
    
    private static final Set<EntityType> interactableEntities = Sets.newHashSet();
    
    @EventHandler
    public void onEntityInteractEntity(PlayerInteractEntityEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        if (!cp.canBuild()) {
            if (!interactableEntities.contains(e.getRightClicked().getType())) {
                e.setCancelled(true);
            }
        }
        if (e.getHand() == EquipmentSlot.HAND) {
            Vendor.interactEvent(e);
        }
    }
    
    @EventHandler
    public void onPlayerFoodChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            p.setFoodLevel(20);
            e.setCancelled(true);
        }
    }
    
    private static final Set<SpawnReason> noSpawnReasons = Sets.newHashSet(SpawnReason.BEEHIVE, SpawnReason.BREEDING,
    SpawnReason.BUILD_IRONGOLEM, SpawnReason.BUILD_SNOWMAN, SpawnReason.BUILD_WITHER, SpawnReason.CURED, /*SpawnReason.CUSTOM,*/ /*SpawnReason.DEFAULT,*/
    SpawnReason.DROWNED, SpawnReason.DISPENSE_EGG, SpawnReason.EGG, SpawnReason.ENDER_PEARL, SpawnReason.EXPLOSION, SpawnReason.INFECTION,
    SpawnReason.JOCKEY, SpawnReason.LIGHTNING, SpawnReason.MOUNT, SpawnReason.NATURAL, SpawnReason.NETHER_PORTAL, SpawnReason.OCELOT_BABY,
    SpawnReason.PATROL, SpawnReason.RAID, SpawnReason.REINFORCEMENTS, SpawnReason.SHEARED, /*SpawnReason.SHOULDER_ENTITY,*/ SpawnReason.SILVERFISH_BLOCK,
    SpawnReason.SLIME_SPLIT, SpawnReason.SPAWNER, /*SpawnReason.SPAWNER_EGG,*/ SpawnReason.TRAP, SpawnReason.VILLAGE_DEFENSE, SpawnReason.VILLAGE_INVASION);
    
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        if (noSpawnReasons.contains(e.getSpawnReason())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (e.getEntityType().equals(EntityType.PLAYER)) {
            CorePlayer cp = Core.getInstance().getPlayers().get(e.getEntity().getName());
            if (!cp.canBuild()) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        if (!cp.canBreak()) {
            e.setCancelled(true);
        } else {
            e.setDropItems(false);
            e.setExpToDrop(0);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        
        if (!cp.canBuild()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockDropItem(BlockDropItemEvent e) {
        if (CorePlugin.isInBattleGlobal(e.getPlayer())) {
            //e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        e.getDrops().clear();
        e.setDroppedExp(0);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemDamage(PlayerItemDamageEvent e) {
        e.setCancelled(true);
        if (Core.getInstance().getPlayerBattle(e.getPlayer()) != null) {
            
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        if (!e.getNewGameMode().equals(cp.getGameMode())) {
            e.setCancelled(true);
        }
    }
    
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent e) {
        // TODO: Make sure player is in a battle, then check movements based on arena
        
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(e.getPlayer());
        if (dbp != null) {
            dbp.getBattle().onMove(dbp, e);
        }
    }
    
    private static final Set<Material> interactables = Sets.newHashSet(Material.STONE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON, Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON, Material.LEVER,
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.SPRUCE_DOOR, Material.OAK_DOOR, Material.JUNGLE_DOOR, Material.DARK_OAK_DOOR);
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(e.getPlayer());
        if (dbp != null) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                dbp.getBattle().onRightClick(dbp);
            } else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                dbp.getBattle().onBlockBreak(dbp);
            }
        }
        CorePlayer cp = Core.getInstance().getPlayers().get(e.getPlayer());
        if (!cp.canBuild()) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (!interactables.contains(e.getClickedBlock().getType())) {
                    e.setCancelled(true);
                }
            }
            if (e.getAction() == Action.PHYSICAL) {
                // Cancel trample event
                if (e.getClickedBlock().getType().equals(Material.FARMLAND)) {
                    e.setCancelled(true);
                }
            }
            // Cancel fire punch event
            //if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (e.getClickedBlock() != null &&
                    e.getClickedBlock().getRelative(e.getBlockFace()).getType().equals(Material.FIRE)) {
                e.setCancelled(true);
            }
            //}
        }
    }
    
    @EventHandler
    public void onPlayerStopSpectate(PlayerToggleSneakEvent e) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(e.getPlayer());
        if (dbp != null) {
            if (dbp.getPlayer().getGameMode() == GameMode.SPECTATOR &&
                    dbp.getBattleState() == BattleState.SPECTATOR) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        DBPlayer dbp = CorePlugin.getBattlePlayerGlobal(e.getPlayer());
        if (dbp != null) {
            if (dbp.getBattleState() == BattleState.BATTLER) {
                dbp.getBattle().fixSpectators(dbp);
            }
        }
    }
    
}
