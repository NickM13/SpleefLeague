/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.listener;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuItemHotbar;
import com.spleefleague.core.menu.hotbars.SLMainHotbar;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.CoreRankManager;
import com.spleefleague.core.util.variable.Warp;
import com.spleefleague.core.vendor.Vendors;
import com.spleefleague.core.world.global.lock.GlobalLock;
import com.spleefleague.core.world.global.vehicle.GlobalVehicle;
import com.spleefleague.core.world.global.vehicle.LaunchPad;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftSign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

/**
 * Player listener for out of game actions
 *
 * @author NickM13
 */
public class EnvironmentListener implements Listener {
    
    /**
     * Converts lines 1, 2, and 3 to doubles
     *
     * @param sign Sign
     * @return Vector
     */
    private Vector getSignVector(CraftSign sign) {
        Vector vec = new Vector();
        vec.setX(Double.parseDouble(sign.getLine(1)));
        vec.setY(Double.parseDouble(sign.getLine(2)));
        vec.setZ(Double.parseDouble(sign.getLine(3)));
        return vec;
    }

    /**
     * Returns a set of blocks below a block
     *
     * @param block Origin
     * @return Block Set
     */
    private Set<Block> getBlocksBelow(Block block) {
        Set<Block> blocks = new HashSet<>();
        if (block == null) return blocks;
        Block rel = block;
        int BLOCKS_BELOW = 5;
        for (int i = 0; i < BLOCKS_BELOW; i++) {
            blocks.add(rel);
            rel = rel.getRelative(BlockFace.DOWN);
        }
        return blocks;
    }
    
    private void stopHorizontalMovement(PlayerMoveEvent event) {
        if (event.getTo() != null) {
            Location newLoc = event.getTo().clone();
            newLoc.setX(event.getFrom().getX());
            newLoc.setZ(event.getFrom().getZ());
            event.setTo(newLoc);
        }
    }

    private final Map<UUID, Long> jumpCooldowns = new HashMap<>();
    
    /**
     * Perform some actions if a player walks into or above a sign
     * with specific text on it
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerMoveSign(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        for (Block block : getBlocksBelow(event.getTo().getBlock())) {
            BlockState state = block.getState();
            if (state instanceof CraftSign) {
                CraftSign sign = (CraftSign) state;
                switch (sign.getLine(0).toLowerCase()) {
                    case "[jump_1]":
                        if ((!jumpCooldowns.containsKey(event.getPlayer().getUniqueId())
                                || jumpCooldowns.get(event.getPlayer().getUniqueId()) < System.currentTimeMillis())
                                && !event.getPlayer().isInsideVehicle()) {
                            Vector vec = getSignVector(sign).multiply(0.3);
                            LaunchPad.launchEntity(new Vector(block.getX() + 0.5, block.getY() + 3., block.getZ() + 0.5),
                                    vec,
                                    event.getPlayer());
                            jumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 2000);
                        }
                        break;
                    case "[jump_2]":
                        if ((!jumpCooldowns.containsKey(event.getPlayer().getUniqueId())
                                || jumpCooldowns.get(event.getPlayer().getUniqueId()) < System.currentTimeMillis())
                                && !event.getPlayer().isInsideVehicle()) {
                            Vector vec = getSignVector(sign).multiply(0.3);
                            LaunchPad.launchEntity(new Vector(block.getX() + 0.5, block.getY() + 1., block.getZ() + 0.5),
                                    vec,
                                    event.getPlayer());
                            jumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 2000);
                        }
                        break;
                    case "[jump_3]":
                        if ((!jumpCooldowns.containsKey(event.getPlayer().getUniqueId())
                                || jumpCooldowns.get(event.getPlayer().getUniqueId()) < System.currentTimeMillis())
                                && !event.getPlayer().isInsideVehicle()) {
                            Vector vec = getSignVector(sign).multiply(0.3);
                            LaunchPad.launchEntity(new Vector(block.getX() + 0.5, block.getY() + 2.5, block.getZ() + 0.5),
                                    vec,
                                    event.getPlayer());
                            jumpCooldowns.put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 2000);
                        }
                        break;
                    case "[teleport]":
                        cp.teleport(getSignVector(sign));
                        break;
                    case "[min-rank]":
                        // TODO: Would be cool if FakeWorld was incorporated to set fake block walls
                        if (!cp.getRank().hasPermission(Core.getInstance().getRankManager().getRank(sign.getLine(1)))) {
                            stopHorizontalMovement(event);
                        }
                        break;
                    case "[max-rank]":
                        if (cp.getRank().hasPermission(Core.getInstance().getRankManager().getRank(sign.getLine(1)))) {
                            stopHorizontalMovement(event);
                        }
                        break;
                    case "[warp]":
                        Warp warp = Warp.getWarp(sign.getLine(1));
                        if (warp != null) {
                            cp.warp(warp);
                        }
                        break;
                    case "[effect]":
                        try {
                            PotionEffectType type = PotionEffectType.getByName(sign.getLine(1));
                            if (type == null) break;
                            int duration = Integer.parseInt(sign.getLine(2));
                            int amplifier = Integer.parseInt(sign.getLine(3));
                            event.getPlayer().addPotionEffect(type.createEffect(duration, amplifier));
                        } catch (NumberFormatException ignored) {

                        }
                        break;
                    default:
                        continue;
                }
                break;
            }
        }
    }
    
    private static final List<String> WISHES = Lists.newArrayList(
            "I wish I would grow up, it feels like I've been 10 for years.",
            "I wish to know how you survived my wrath, little fountain.",
            "Already, I've a kingdom in my prospects, a land to rule.  What to ask for?  Perhaps a frozen scone...",
            "I wish someday to retire to my own tidy little estate, with a bunch of pet dogs to keep me company.",
            "Brann Bronzebeard was here.",
            "Just once, I wish someone would greet me without making a stupid joke about gnomes or time travel.",
            "Sometimes... I wish someone would come along and just give me a big, long hug.",
            "Arthas, my love, please come back to me.",
            "Listen, little wishing fountain.  You'll give me all your power if you know what's good for you.",
            "That young apprentice Jaina is quite attractive for a human.  I hope she recognizes my elegance and power.",
            "I hope my sisters and I can grow up and get married together.",
            "Wishes are a fool's pastime.  Take this coin as payment for your precious Eye.",
            "Grom, may you rest well, old friend.",
            "It is my wish that my dear Taelan will grow strong enough to defend the people he cares for.",
            "Young Arthas must learn patience to temper his power.  I will pray that he one day learns how to lead with care rather than force.");
    /**
     * Prevent non building players from dropping blocks
     *
     * @param event Event
     */
    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (!cp.canBuild()) {
            event.setCancelled(true);
            Block block = cp.getPlayer().getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
            if (block != null
                    && block.getType().equals(Material.WATER)
                    && SLMainHotbar.getItemHotbar().createItem(cp).equals(event.getItemDrop().getItemStack())) {
                Random rand = new Random();
                Core.getInstance().sendMessage(cp, WISHES.get(rand.nextInt(WISHES.size())));
            }
        } else {
            if (InventoryMenuItemHotbar.isHotbarItem(event.getItemDrop().getItemStack())) {
                event.getItemDrop().remove();
            }
        }
    }

    /**
     * @param event Event
     */
    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            CorePlayer cp = Core.getInstance().getPlayers().get(event.getEntity().getUniqueId());
            GlobalVehicle.remove(event.getDismounted().getEntityId());
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        LivingEntity entity = GlobalVehicle.remove(projectile.getEntityId());
        if (entity != null) {
            entity.eject();
            entity.teleport(new Location(projectile.getWorld(),
                    projectile.getLocation().getX(),
                    projectile.getLocation().getY() + 2,
                    projectile.getLocation().getZ(),
                    entity.getLocation().getYaw(),
                    entity.getLocation().getPitch()));
        }
    }

    /**
     * Perform effects based on the type of damage
     * a player takes
     *
     * @param event Event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                event.setCancelled(true);
            }
        }
    }

    private static final Set<EntityType> damageableMobs = Sets.newHashSet(EntityType.BLAZE, EntityType.BOAT, EntityType.CAVE_SPIDER,
            EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON,
            EntityType.EVOKER, EntityType.GHAST, EntityType.GIANT, EntityType.GUARDIAN, EntityType.HUSK, EntityType.ILLUSIONER, EntityType.MAGMA_CUBE,
            EntityType.MINECART, EntityType.PHANTOM, EntityType.PIG_ZOMBIE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER,
            EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH,
            EntityType.WITHER, EntityType.WITHER_SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER);

    /**
     * Handle certain entity damaging events
     *
     * @param event Event
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                if (!Core.getInstance().getPlayers().get((Player) projectile.getShooter()).canBuild()) {
                    if (!damageableMobs.contains(event.getEntityType()))
                        event.setCancelled(true);
                }
            }
        } else if (event.getDamager() instanceof Player) {
            if (!Core.getInstance().getPlayers().get((Player)event.getDamager()).canBuild()) {
                if (!damageableMobs.contains(event.getEntityType()))
                    event.setCancelled(true);
            } else {
                Vendors.punchEvent(event);
            }
        } else if (!event.getEntityType().equals(EntityType.PLAYER) && !damageableMobs.contains(event.getEntityType())) {
            event.setCancelled(true);
        }
    }

    private static final Set<EntityType> interactableEntities = Sets.newHashSet();

    /**
     * Handle certain player entity interactions such
     * as interacting with vendors
     *
     * @param event Event
     */
    @EventHandler
    public void onEntityInteractEntity(PlayerInteractEntityEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (!cp.canBuild()) {
            if (!interactableEntities.contains(event.getRightClicked().getType())) {
                event.setCancelled(true);
            }
        }
        if (event.getHand() == EquipmentSlot.HAND) {
            Vendors.interactEvent(event);
        }
    }

    /**
     * Prevent player food from dropping
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            p.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    /**
     * Save location of death for /back usage
     * TODO: Do we really need this?
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getEntity());
        cp.saveLastLocation();
        event.setDeathMessage(cp.getDisplayName() + " somehow died on SpleefLeague?  Seems kinda sus.  Oh well, here's the real reason: " + event.getDeathMessage());
        Core.getInstance().sendMessage(event.getDeathMessage());
    }

    private static final Set<CreatureSpawnEvent.SpawnReason> noSpawnReasons = Sets.newHashSet(CreatureSpawnEvent.SpawnReason.BREEDING,
            CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM, CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN, CreatureSpawnEvent.SpawnReason.BUILD_WITHER, CreatureSpawnEvent.SpawnReason.CURED, /*SpawnReason.CUSTOM,*/ /*SpawnReason.DEFAULT,*/
            CreatureSpawnEvent.SpawnReason.DROWNED, CreatureSpawnEvent.SpawnReason.DISPENSE_EGG, CreatureSpawnEvent.SpawnReason.EGG, CreatureSpawnEvent.SpawnReason.ENDER_PEARL, CreatureSpawnEvent.SpawnReason.EXPLOSION, CreatureSpawnEvent.SpawnReason.INFECTION,
            CreatureSpawnEvent.SpawnReason.JOCKEY, CreatureSpawnEvent.SpawnReason.LIGHTNING, CreatureSpawnEvent.SpawnReason.MOUNT, CreatureSpawnEvent.SpawnReason.NATURAL, CreatureSpawnEvent.SpawnReason.NETHER_PORTAL, CreatureSpawnEvent.SpawnReason.OCELOT_BABY,
            CreatureSpawnEvent.SpawnReason.PATROL, CreatureSpawnEvent.SpawnReason.RAID, CreatureSpawnEvent.SpawnReason.REINFORCEMENTS, CreatureSpawnEvent.SpawnReason.SHEARED, /*SpawnReason.SHOULDER_ENTITY,*/ CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK,
            CreatureSpawnEvent.SpawnReason.SLIME_SPLIT, CreatureSpawnEvent.SpawnReason.SPAWNER, /*SpawnReason.SPAWNER_EGG,*/ CreatureSpawnEvent.SpawnReason.TRAP, CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE, CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION);

    /**
     * Prevent mobs from spawning naturally
     *
     * @param event Event
     */
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (noSpawnReasons.contains(event.getSpawnReason())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent non-building players from picking up items
     *
     * @param event Event
     */
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            CorePlayer cp = Core.getInstance().getPlayers().get(event.getEntity().getName());
            if (!cp.canBuild()) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Prevent players from picking up arrows (since we use them for launch pads)
     *
     * @param event Event
     */
    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        event.getArrow().remove();
        event.setCancelled(true);
    }

    /**
     * Prevent non-building players from breaking blocks
     * Fixes issue with swords breaking blocks in creative
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (!cp.canBreak()) {
            event.setCancelled(true);
        } else {
            event.setDropItems(false);
            event.setExpToDrop(0);
        }
    }

    /**
     * Prevent non-building players from placing blocks
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());

        if (!cp.canBuild()) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevent entities from dropping anything on death
     *
     * @param event Event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    /**
     * Prevent player items from taking durability
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        event.setCancelled(true);
    }

    /**
     * Prevent gamemode from being changed by other plugins
     * (looking at you MultiVerse)
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (!event.getNewGameMode().equals(cp.getGameMode())) {
            event.setCancelled(true);
        }
    }

    /**
     * Force player to spawn at a designated spawn location
     * as opposed to world spawn
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        event.setRespawnLocation(cp.getSpawnLocation());
    }

    /**
     * Not sure what this one is preventing but I'm leaving
     * it here anyway - Nick
     *
     * @param event Event
     */
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.setCancelled(true);
    }

    /**
     * Allow creeper and tnt explosions but don't destroy blocks
     *
     * @param event Event
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    /**
     * Prevent non-creative (maybe do by role?) players
     * from being able to use elytras, for when they
     * become a cosmetic item
     *
     * @param event Event
     */
    @EventHandler
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        if (event.isGliding()
                && event.getEntityType().equals(EntityType.PLAYER)) {
            Player p = (Player) event.getEntity();
            CorePlayer cp = Core.getInstance().getPlayers().get(p);
            if (!cp.getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
            }
        }
    }
    
    /**
     * Prevents campfires from cooking food
     *
     * @param event Event
     */
    @EventHandler
    public void onBlockCook(BlockCookEvent event) {
        event.setCancelled(true);
    }

    /**
     * Prevent coral blocks from dying outside of water
     *
     * @param event Event
     */
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    private static final Set<Material> interactables = Sets.newHashSet(Material.STONE_BUTTON, Material.OAK_BUTTON, Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON, Material.JUNGLE_BUTTON, Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON, Material.LEVER,
            Material.ACACIA_DOOR, Material.BIRCH_DOOR, Material.SPRUCE_DOOR, Material.OAK_DOOR, Material.JUNGLE_DOOR, Material.DARK_OAK_DOOR);

    /**
     * Prevent most of world from being interacted with
     *
     * @param event Event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
        if (!cp.canBuild()) {
            if (event.getClickedBlock() != null) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (!interactables.contains(event.getClickedBlock().getType())
                            || GlobalLock.isLocked(new BlockPosition(
                                    event.getClickedBlock().getX(),
                                    event.getClickedBlock().getY(),
                                    event.getClickedBlock().getZ()))) {
                        event.setCancelled(true);
                    }
                }
                if (event.getAction() == Action.PHYSICAL) {
                    // Cancel trample event
                    if (event.getClickedBlock().getType().equals(Material.FARMLAND)) {
                        event.setCancelled(true);
                    }
                }
                // Cancel fire punch event
                if (event.getClickedBlock().getRelative(event.getBlockFace()).getType().equals(Material.FIRE)) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
