/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world.game;

import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.*;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.Core;
import com.spleefleague.core.util.variable.RaycastResult;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class GameWorld extends FakeWorld {

    /**
     * Blocks that are added after a delay
     */
    protected static class FutureBlock {
        public long delay;
        public FakeBlock fakeBlock;
        
        public FutureBlock(long delay, FakeBlock fakeBlock) {
            this.delay = delay;
            this.fakeBlock = fakeBlock;
        }
    }

    /**
     * Player death effect
     */
    protected static class PlayerBlast {
        Location loc;
        int time;
        
        PlayerBlast(Location loc, int time) {
            this.loc = loc;
            this.time = time;
        }
    }

    protected final Set<Material> breakTools;
    protected final Set<Material> breakables;
    protected boolean edittable;
    
    protected final BukkitTask projectileCollideTask;
    protected Map<Integer, GameProjectile> projectiles;
    
    protected BukkitTask futureBlockTask;
    protected final Map<BlockPosition, FutureBlock> futureBlocks;
    
    protected final BukkitTask playerBlastTask;
    protected final List<PlayerBlast> playerBlasts;
    
    protected boolean showSpectators;
    
    public GameWorld(World world) {
        super(world, GameWorldPlayer.class);
        breakTools = new HashSet<>();
        breakables = new HashSet<>();
        edittable = false;
        futureBlocks = new HashMap<>();
        projectiles = new HashMap<>();
        
        projectileCollideTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updateProjectiles, 0L, 1L);
        
        showSpectators = true;
        
        playerBlasts = new ArrayList<>();
        playerBlastTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updatePlayerBlasts, 0L, 2L);
        
        futureBlockTask = Bukkit.getScheduler()
                .runTaskTimer(Core.getInstance(),
                        this::updateFutureBlocks, 0L, 2L);
        
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.SPAWN_ENTITY) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Entity entity = packet.getEntityModifier(event).read(0);
                if (projectiles.containsKey(entity.getEntityId()) &&
                        !getPlayerMap().containsKey(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        });
    }
    public void destroy() {
        super.destroy();
        futureBlockTask.cancel();
        playerBlastTask.cancel();
        clearProjectiles();
    }
    
    /**
     * Attempt to break a block if the player is holding the right item
     * and the block is a breakable block, if fails send a fake packet
     * to the player to make sure the block doesn't disappear for them
     *
     * @param cp Core Player
     * @param pos Block Position
     */
    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        if (!fakeBlocks.containsKey(pos)) return false;
        ItemStack heldItem = cp.getPlayer().getInventory().getItemInMainHand();
        if (edittable
                && breakables.contains(fakeBlocks.get(pos).getBlockData().getMaterial())
                && breakTools.contains(heldItem.getType())) {
            for (FakeWorldPlayer fwp : playerMap.values()) {
                if (!fwp.getPlayer().equals(cp.getPlayer())) {
                    fwp.getPlayer().playSound(new Location(getWorld(), pos.getX(), pos.getY(), pos.getZ()), fakeBlocks.get(pos).getBreakSound(), 1, 1);
                }
            }
            breakBlock(pos, cp);
        } else {
            updateBlock(pos);
        }
        return true;
    }

    /**
     * On player item use
     *
     * @param cp Core Player
     * @param blockPosition Click Block
     * @param blockRelative Placed Block
     * @return Cancel Event
     */
    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative) {
        return true;
    }

    protected void updateProjectiles() {
        Iterator<Map.Entry<Integer, GameProjectile>> pit = projectiles.entrySet().iterator();
        while (pit.hasNext()) {
            GameProjectile projectile = pit.next().getValue();
            List<RaycastResult> result = projectile.cast();
            projectile.getEntity().setVelocity(projectile.getEntity().getVelocity().multiply(projectile.getDrag()));
            for (RaycastResult rr : result) {
                if (fakeBlocks.containsKey(rr.blockPos)) {
                    FakeBlock fb = fakeBlocks.get(rr.blockPos);
                    if (!fb.getBlockData().getMaterial().equals(Material.AIR)) {
                        breakBlocks(rr.blockPos, projectile.getProjectile().power);
                        projectile.bounce();
                        if (!projectile.hasBounces()) {
                            projectile.getEntity().remove();
                            break;
                        } else {
                            if (projectile.doesBounce()) {
                                projectile.getEntity().teleport(new Location(projectile.getEntity().getWorld(),
                                        rr.intersection.getX(),
                                        rr.intersection.getY(),
                                        rr.intersection.getZ()));
                                projectile.setLastLoc();
                                switch (rr.axis) {
                                    case 1: projectile.getEntity().setVelocity(projectile.getEntity().getVelocity().multiply(new Vector(-projectile.getBouncePower(), 1.0, 1.0))); break;
                                    case 2: projectile.getEntity().setVelocity(projectile.getEntity().getVelocity().multiply(new Vector(1.0, -projectile.getBouncePower(), 1.0))); break;
                                    case 3: projectile.getEntity().setVelocity(projectile.getEntity().getVelocity().multiply(new Vector(1.0, 1.0, -projectile.getBouncePower()))); break;
                                }
                                break;
                            } else {
                                projectile.getEntity().setVelocity(projectile.getEntity().getVelocity().multiply(-projectile.getBouncePower()));
                            }
                        }
                    }
                }
            }
            if (projectile.getEntity().isDead()) {
                pit.remove();
            }
        }
    }

    protected void updatePlayerBlasts() {
        Iterator<PlayerBlast> pbit = playerBlasts.iterator();
        while (pbit.hasNext()) {
            PlayerBlast blast = pbit.next();
            playerMap.values().forEach(player -> {
                player.getPlayer().spawnParticle(Particle.SWEEP_ATTACK, blast.loc, 10, 0.5, 4, 0.5);
            });
            blast.time -= 2;
            if (blast.time < 0)
                pbit.remove();
            blast.loc.add(0, 4, 0);
        }
    }

    protected void updateFutureBlocks() {
        Iterator<Map.Entry<BlockPosition, GameWorld.FutureBlock>> it = futureBlocks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPosition, GameWorld.FutureBlock> e = it.next();
            e.getValue().delay -= 2;
            if (e.getValue().delay <= 0) {
                setBlock(e.getKey(), e.getValue().fakeBlock.getBlockData());
                updateBlock(e.getKey());
                it.remove();
            } else if (e.getValue().delay < 15 && e.getValue().fakeBlock.getBlockData().getMaterial().equals(Material.SNOW_BLOCK)) {
                Snow snow = (Snow) Material.SNOW.createBlockData();
                snow.setLayers((int)(8 - (e.getValue().delay / 2)));
                setBlock(e.getKey(), snow);
                updateBlock(e.getKey());
            }
        }
    }

    public void setShowSpectators(boolean state) {
        showSpectators = state;
    }
    
    public void clearProjectiles() {
        for (GameProjectile gp : projectiles.values()) {
            gp.getEntity().remove();
        }
    }
    
    public void shootProjectile(CorePlayer cp, FakeProjectile projectileType) {
        if (projectileType.entityType.getEntityClass() == null) return;
        // TODO: Rewrite this, very chunky
        Location handLocation = cp.getPlayer().getEyeLocation().clone()
                .add(cp.getPlayer().getLocation().getDirection()
                        .crossProduct(new Vector(0, 1, 0)).normalize()
                        .multiply(0.15).add(new Vector(0, -0.15, 0)));
        for (int i = 0; i < projectileType.count; i++) {
            Consumer<? extends Entity> consumer = (Entity e) -> {
                GameProjectile gp = new GameProjectile(e, projectileType);
                projectiles.put(e.getEntityId(), gp);
            };
            Entity entity = getWorld().spawn(handLocation, projectileType.entityType.getEntityClass(), (Consumer) consumer);
            projectiles.get(entity.getEntityId()).setShooter(cp.getPlayer());
            Random rand = new Random();
            entity.setVelocity(cp.getPlayer().getLocation()
                    .getDirection()
                    .add(new Vector(
                            (rand.nextDouble() - 0.5) * projectileType.spread,
                            (rand.nextDouble() - 0.5) * projectileType.spread,
                            (rand.nextDouble() - 0.5) * projectileType.spread))
                    .normalize().multiply(projectileType.range * 0.25));
            entity.setGravity(projectileType.gravity);
        }
        getPlayerMap().values().forEach(gwp -> {
            gwp.getPlayer().playSound(cp.getPlayer().getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1, 1);
        });
    }
    
    public void doFailBlast(CorePlayer cp) {
        playerBlasts.add(new PlayerBlast(cp.getPlayer().getLocation(), 20));
        getPlayerMap().values().forEach(gwp -> {
            gwp.getPlayer().playSound(gwp.getPlayer().getLocation(), Sound.ENTITY_DOLPHIN_DEATH, 15, 1);
        });
    }
    
    public void addBreakTool(Material tool) {
        breakTools.add(tool);
    }
    
    public void addBreakableBlock(Material material) {
        breakables.add(material);
    }
    
    public void setEditable(boolean editable) {
        this.edittable = editable;
    }

    public boolean chipBlock(BlockPosition pos, int amount) {
        FakeBlock fb = fakeBlocks.get(pos);
        breakBlock(pos, null);
        /*
        if (fakeBlocks.containsKey(pos)
                && (fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.SNOW)
                || fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.SNOW_BLOCK))) {
            players.values().forEach(gwp -> {
                gwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(world).add(0.5, 0.5, 0.5), amount * 2, 0.25, 0.25, 0.25, fakeBlocks.get(pos).getBlockData());
                gwp.getPlayer().playSound(pos.toLocation(world), fakeBlocks.get(pos).getBreakSound(), amount * 0.1f, 1);
            });
            int layers = 8;
            if (fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.SNOW)) {
                layers = ((Snow) fakeBlocks.get(pos).getBlockData()).getLayers();
            }
            layers -= amount;
            if (layers <= 0) {
                setBlock(pos, Material.AIR.createBlockData(), false);
            } else {
                Snow snow = (Snow) Material.SNOW.createBlockData();
                snow.setLayers(layers);
                setBlock(pos, snow);
            }
            return true;
        }
        */
        return false;
    }

    public void fixSpectators(CorePlayer target) {
        System.out.println("fixSpectators");
    }

    public void setSpectator(CorePlayer spectator, CorePlayer target) {
        spectator.getPlayer().teleport(target.getPlayer().getLocation());
    }

    /**
     * Sets a block to spawn in the future based on its distance
     * from the blocks
     *
     * @param pos Block Position
     * @param blockData Block Data
     * @param secondsPerBlock Seconds per block distance from locations
     * @param locations Locations
     */
    public void setBlockDelayed(BlockPosition pos, BlockData blockData, double secondsPerBlock, List<Location> locations) {
        Random random = new Random();
        double closest = -1;
        for (Location loc : locations) {
            double d = loc.distance(new Location(getWorld(), pos.getX(), (pos.getY() - loc.getY()) * 8 + loc.getY(), pos.getZ()));
            if (d < closest || closest < 0) {
                closest = d;
            }
        }
        closest -= 6;
        if (closest < 0) {
            setBlock(pos, blockData);
        } else {
            closest += random.nextInt(3);
            futureBlocks.put(pos, new FutureBlock((long) ((closest) * secondsPerBlock * 20D), new FakeBlock(pos, blockData)));
        }
    }

}
