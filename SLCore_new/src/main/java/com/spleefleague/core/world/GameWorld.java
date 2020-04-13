/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.world;

import com.spleefleague.core.world.projectile.GameProjectile;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.RaycastResult;
import com.spleefleague.core.world.projectile.FakeProjectile;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 */
public class GameWorld {
    
    protected class FutureBlock {
        public long delay;
        public FakeBlock fakeBlock;
        
        public FutureBlock(long delay, FakeBlock fakeBlock) {
            this.delay = delay;
            this.fakeBlock = fakeBlock;
        }
    }
    
    protected class ChunkCoord {
        public int x, z;
        
        public ChunkCoord(int x, int z) {
            this.x = x;
            this.z = z;
        }
        
        @Override
        public boolean equals(Object cc) {
            if (cc == this) return true;
            if (cc != null && cc instanceof ChunkCoord) {
                return (((ChunkCoord)cc).x == x && ((ChunkCoord) cc).z == z);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + this.x;
            hash = 29 * hash + this.z;
            return hash;
        }
    }
    
    protected class PlayerBlast {
        Location loc;
        int time;
        
        PlayerBlast(Location loc, int time) {
            this.loc = loc;
            this.time = time;
        }
    }
    
    protected final Map<BlockPosition, FakeBlock> fakeBlocks;
    protected final Map<ChunkCoord, Set<BlockPosition>> fakeChunks;
    protected Map<Player, BattleState> players;
    protected final List<PacketAdapter> packetAdapters;
    protected final World world;
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
        this.world = world;
        fakeBlocks = new HashMap<>();
        fakeChunks = new HashMap<>();
        players = new HashMap<>();
        packetAdapters = new ArrayList<>();
        breakTools = new HashSet<>();
        breakables = new HashSet<>();
        edittable = false;
        futureBlocks = new HashMap<>();
        projectiles = new HashMap<>();
        
        projectileCollideTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            Iterator<Map.Entry<Integer, GameProjectile>> pit = projectiles.entrySet().iterator();
            while (pit.hasNext()) {
                GameProjectile projectile = pit.next().getValue();
                List<RaycastResult> result = projectile.cast();
                projectile.getEntity().setVelocity(projectile.getEntity().getVelocity().multiply(projectile.getDrag()));
                for (RaycastResult rr : result) {
                    if (fakeBlocks.containsKey(rr.blockPos)) {
                        FakeBlock fb = fakeBlocks.get(rr.blockPos);
                        if (!fb.getBlockData().getMaterial().equals(Material.AIR)) {
                            breakBlock(rr.blockPos, projectile.getProjectile().power);
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
        }, 0L, 1L);
        
        showSpectators = true;
        
        playerBlasts = new ArrayList<>();
        playerBlastTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            Iterator<PlayerBlast> pbit = playerBlasts.iterator();
            while (pbit.hasNext()) {
                PlayerBlast blast = pbit.next();
                players.keySet().forEach(player -> {
                    player.spawnParticle(Particle.SWEEP_ATTACK, blast.loc, 10, 0.5, 4, 0.5);
                });
                blast.time -= 2;
                if (blast.time < 0)
                    pbit.remove();
                blast.loc.add(0, 4, 0);
            }
        }, 0L, 2L);
        
        futureBlockTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
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
        }, 0L, 2L);
        
        addPacketAdapter(new PacketAdapter(Core.getInstance(),
                PacketType.Play.Client.BLOCK_DIG,
                PacketType.Play.Client.USE_ITEM,
                PacketType.Play.Server.NAMED_SOUND_EFFECT,
                PacketType.Play.Server.SPAWN_ENTITY,
                PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (players.containsKey(event.getPlayer())) {
                    if (event.getPacketType() == PacketType.Play.Client.BLOCK_DIG) {
                        BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                        if (fakeBlocks.containsKey(pos)) {
                            event.setCancelled(true);
                            ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
                            PlayerDigType pdt = event.getPacket().getPlayerDigTypes().read(0);
                            if (edittable &&
                                    breakables.contains(fakeBlocks.get(pos).getBlockData().getMaterial()) &&
                                    (heldItem != null && breakTools.contains(heldItem.getType())) &&
                                    (pdt.equals(PlayerDigType.START_DESTROY_BLOCK) ||
                                     pdt.equals(PlayerDigType.ABORT_DESTROY_BLOCK) ||
                                     pdt.equals(PlayerDigType.STOP_DESTROY_BLOCK))) {
                                for (Player p : players.keySet()) {
                                    if (!p.equals(event.getPlayer())) {
                                        p.playSound(new Location(world, pos.getX(), pos.getY(), pos.getZ()), fakeBlocks.get(pos).getBreakSound(), 1, 1);
                                    }
                                }
                                breakBlock(pos);
                            } else {
                                updateBlock(pos);
                            }
                        }
                    }
                    else if (event.getPacketType() == PacketType.Play.Client.USE_ITEM) {
                        event.setCancelled(true);
                    }
                }
            }
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
                    Entity entity = packet.getEntityModifier(event).read(0);
                    if (projectiles.containsKey(entity.getEntityId()) &&
                            !players.containsKey(event.getPlayer())) {
                        event.setCancelled(true);
                    }
                } else if (players.containsKey(event.getPlayer())) {
                    if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK) {
                        ChunkCoord coord = new ChunkCoord(event.getPacket().getIntegers().read(0), event.getPacket().getIntegers().read(1));
                        if (fakeChunks.containsKey(coord)) {
                            Bukkit.getServer().getScheduler().runTaskLater(Core.getInstance(), () -> {
                                fakeChunks.get(coord).forEach((pos) -> {
                                    updateBlock(pos);
                                });}, 1L);
                        }
                    }
                }
            }
        });
    }
    public void clear() {
        for (FakeBlock fb : fakeBlocks.values()) {
            this.setBlock(fb.getBlockPosition(), Material.AIR.createBlockData(), true);
        }
        this.updateAll();
        fakeBlocks.clear();
        fakeChunks.clear();
    }
    public void destroy() {
        futureBlockTask.cancel();
        playerBlastTask.cancel();
        while (!players.isEmpty()) {
            removePlayer((Player)players.keySet().toArray()[0]);
        }
        for (GameProjectile fp : projectiles.values()) {
            fp.getEntity().remove();
        }
        fakeBlocks.clear();
        fakeChunks.clear();
        players.clear();
        packetAdapters.forEach(packetAdapter -> { Core.getProtocolManager().removePacketListener(packetAdapter); });
    }
    
    public void showSpectators(boolean state) {
        showSpectators = state;
    }
    
    public void clearProjectiles() {
        for (GameProjectile fp : projectiles.values()) {
            fp.getEntity().remove();
        }
    }
    
    public void checkProjectile(ProjectileHitEvent e) {
        BlockPosition pos = new BlockPosition(e.getHitBlock().getLocation().toVector());
        if (fakeBlocks.containsKey(pos)) {
            breakBlock(pos);
        }
    }
    
    public void shootProjectile(Player p, FakeProjectile projectileType) {
        Location handLocation = p.getEyeLocation().clone().add(p.getLocation().getDirection().crossProduct(new Vector(0, 1, 0)).normalize().multiply(0.15).add(new Vector(0, -0.15, 0)));
        
        for (int i = 0; i < projectileType.count; i++) {
            Consumer<? extends Entity> consumer = (Entity e) -> {
                GameProjectile fp = new GameProjectile(e, projectileType);
                projectiles.put(e.getEntityId(), fp);
            };
            Entity entity;
            switch (projectileType.entityType) {
                default:
                    entity = world.spawn(handLocation, projectileType.entityType.getEntityClass(), (Consumer) consumer);
                    break;
            }
            projectiles.get(entity.getEntityId()).setShooter(p);
            Random rand = new Random();
            entity.setVelocity(p.getLocation()
                    .getDirection()
                    .add(new Vector(
                            (rand.nextDouble() - 0.5) * projectileType.spread,
                            (rand.nextDouble() - 0.5) * projectileType.spread,
                            (rand.nextDouble() - 0.5) * projectileType.spread))
                    .normalize().multiply(projectileType.range * 0.25));
            entity.setGravity(projectileType.gravity);
        }
        players.keySet().forEach(player -> {
            player.playSound(p.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1, 1);
        });
        
        //p.setVelocity(p.getVelocity().clone().add(p.getLocation().clone().getDirection().multiply(-projectileType.knockback)));
    }
    
    public void doFailBlast(Player p) {
        playerBlasts.add(new PlayerBlast(p.getLocation(), 20));
        players.keySet().forEach(player -> {
            player.playSound(player.getLocation(), Sound.ENTITY_DOLPHIN_DEATH, 15, 1);
        });
    }
    
    public void addBreakTool(Material tool) {
        breakTools.add(tool);
    }
    
    public void addBreakableBlock(Material material) {
        breakables.add(material);
    }
    
    public void setEdittable(boolean edittable) {
        this.edittable = edittable;
    }
    
    protected PacketAdapter addPacketAdapter(PacketAdapter packetAdapter) {
        Core.getProtocolManager().addPacketListener(packetAdapter);
        packetAdapters.add(packetAdapter);
        return packetAdapter;
    }
    
    public void addPlayer(Player player, BattleState state) {
        players.put(player, state);
        player.setCollidable(false);
        updateAll();
        
        // Hide and hide from all players not in this fakeworld
        for (CorePlayer cp : Core.getInstance().getPlayers().getOnline()) {
            if (!player.equals(cp.getPlayer())) {
                if (!players.containsKey(cp.getPlayer())) {
                    player.hidePlayer(Core.getInstance(), cp.getPlayer());
                    cp.getPlayer().hidePlayer(Core.getInstance(), player);
                } else {
                    player.showPlayer(Core.getInstance(), cp.getPlayer());
                    if ((state == BattleState.SPECTATOR && players.get(cp.getPlayer()) != BattleState.BATTLER) || showSpectators) {
                        cp.getPlayer().showPlayer(Core.getInstance(), player);
                    }
                }
            }
        }
    }
    public void removePlayer(Player player) {
        WrappedBlockData wbd;
        for (Map.Entry<BlockPosition, FakeBlock> fb : fakeBlocks.entrySet()) {
            wbd = WrappedBlockData.createData(world.getBlockAt(new Location(world, fb.getKey().getX(), fb.getKey().getY(), fb.getKey().getZ())).getType());
            PacketContainer fakeBlockPacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            fakeBlockPacket.getBlockPositionModifier().write(0, fb.getKey());
            fakeBlockPacket.getBlockData().write(0, wbd);
            try {
                Core.getProtocolManager().sendServerPacket(player, fakeBlockPacket);
            } catch (InvocationTargetException ex) { 
                Logger.getLogger(GameWorld.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Core.getInstance().returnToWorld(Core.getInstance().getPlayers().get(player));
        
        players.remove(player);
    }
    
    public void fixSpectators(Player target) {
        for (Map.Entry<Player, BattleState> p : players.entrySet()) {
            if (p.getValue() == BattleState.SPECTATOR &&
                    p.getKey().getGameMode() == GameMode.SPECTATOR &&
                    p.getKey().getSpectatorTarget() == target) {
                p.getKey().setSpectatorTarget(null);
                Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> p.getKey().teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN), 2L);
                Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                    if (p.getKey().getPlayer().getGameMode() == GameMode.SPECTATOR)
                        p.getKey().getPlayer().setSpectatorTarget(target.getPlayer());
                }, 8L);
            }
        }
    }
    public void setSpectator(Player spectator, Player target) {
        CorePlayer cp = Core.getInstance().getPlayers().get(spectator);
        cp.setGameMode(GameMode.SPECTATOR);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> spectator.getPlayer().setSpectatorTarget(null), 2L);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> spectator.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN), 4L);
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                    if (spectator.getGameMode() == GameMode.SPECTATOR)
                        spectator.setSpectatorTarget(target.getPlayer());
                }, 8L);
    }
    
    public boolean isBlockSolid(BlockPosition pos) {
        return fakeBlocks.containsKey(pos) && !fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.AIR);
    }
    public void setBlock(BlockPosition pos, BlockData blockData) {
        setBlock(pos, blockData, false);
    }
    public boolean breakBlock(BlockPosition pos, int radius) {
        double dx, dy, dz;
        for (int x = -radius; x <= radius; x++) {
            dx = ((double)x) / radius;
            for (int y = -radius; y <= radius; y++) {
                dy = ((double)y) / radius;
                for (int z = -radius; z <= radius; z++) {
                    dz = ((double)z) / radius;
                    if (Math.sqrt(dx*dx + dy*dy + dz*dz) < 1) {
                        breakBlock(pos.add(new BlockPosition(x, y, z)));
                    }
                }
            }
        }
        return true;
    }
    public boolean breakBlock(BlockPosition pos) {
        if (fakeBlocks.containsKey(pos) && !fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.AIR)) {
            players.keySet().forEach(player -> {
                player.spawnParticle(Particle.BLOCK_DUST, pos.toLocation(world).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, fakeBlocks.get(pos).getBlockData());
                player.playSound(pos.toLocation(world), fakeBlocks.get(pos).getBreakSound(), 1, 1);
            });
            setBlock(pos, Material.AIR.createBlockData(), false);
            return true;
        }
        return false;
    }
    public boolean chipBlock(BlockPosition pos, int amount) {
        if (fakeBlocks.containsKey(pos)
                && (fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.SNOW)
                || fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.SNOW_BLOCK))) {
            players.keySet().forEach(player -> {
                player.spawnParticle(Particle.BLOCK_DUST, pos.toLocation(world).add(0.5, 0.5, 0.5), amount * 2, 0.25, 0.25, 0.25, fakeBlocks.get(pos).getBlockData());
                player.playSound(pos.toLocation(world), fakeBlocks.get(pos).getBreakSound(), amount * 0.1f, 1);
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
        return false;
    }
    public void setBlock(BlockPosition pos, BlockData blockData, boolean ignoreUpdate) {
        Location loc = new Location(world, pos.getX(), pos.getY(), pos.getZ());
        if (!loc.getBlock().getType().equals(Material.AIR)) {
            loc.getBlock().setType(Material.AIR);
        }
        fakeBlocks.put(pos, new FakeBlock(pos, blockData));
        ChunkCoord coord = new ChunkCoord(pos.getX() / 16, pos.getZ() / 16);
        if (!fakeChunks.containsKey(coord)) {
            fakeChunks.put(coord, new HashSet<>());
        }
        fakeChunks.get(coord).add(pos);
        if (!ignoreUpdate) updateBlock(pos);
    }
    public void setBlockDelayed(BlockPosition pos, BlockData blockData, double secondsPerBlock, List<Location> locations) {
        Random random = new Random();
        double closest = -1;
        for (Location loc : locations) {
            double d = loc.distance(new Location(world, pos.getX(), (pos.getY() - loc.getY()) * 8 + loc.getY(), pos.getZ()));
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
    
    
    public void updateBlock(BlockPosition pos) {
        WrappedBlockData wbd;
        FakeBlock fb;
        if ((fb = fakeBlocks.get(pos)) != null) {
            if (fb == null) {
                wbd = WrappedBlockData.createData(world.getBlockAt(new Location(world, pos.getX(), pos.getY(), pos.getZ())).getType());
            } else {
                wbd = WrappedBlockData.createData(fb.getBlockData());
            }
            PacketContainer fakeBlockPacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            fakeBlockPacket.getBlockPositionModifier().write(0, pos);
            fakeBlockPacket.getBlockData().write(0, wbd);
            for (Player p : players.keySet()) {
                try {
                    Core.getProtocolManager().sendServerPacket(p, fakeBlockPacket);
                } catch (InvocationTargetException ex) { 
                    Logger.getLogger(GameWorld.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public void updateAll() {
        for (Map.Entry<BlockPosition, FakeBlock> fb : fakeBlocks.entrySet()) {
            updateBlock(fb.getKey());
        }
    }
}
