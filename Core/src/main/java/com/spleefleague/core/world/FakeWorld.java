package com.spleefleague.core.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.spleefleague.core.Core;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.core.util.PacketUtils;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import net.minecraft.server.v1_15_R1.EnumDirection;
import net.minecraft.server.v1_15_R1.PacketPlayInUseItem;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public abstract class FakeWorld<FWP extends FakeWorldPlayer> {
    
    private final static Map<UUID, Set<ChunkCoord>> loadedChunks = new HashMap<>();
    private static GlobalWorld globalFakeWorld;
    private final static Set<FakeWorld<?>> FAKE_WORLDS = new HashSet<>();

    public static void init() {
        BuildWorld.init();
        FakeBlock.init();
        GlobalWorld.init();

        Core.getInstance().addTask(Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            Core.getInstance().getPlayers().getOnline().forEach(cp -> {
                if (FakeUtils.isOnGround(cp)) {
                    net.minecraft.server.v1_15_R1.Entity entity = ((CraftEntity) cp.getPlayer()).getHandle();
                    entity.setMot(entity.getMot().getX(), 0, entity.getMot().getZ());
                }
            });
        }, 10, 2));
    
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                // TODO: Set up way to find out if block was instantly broken or not
                EnumWrappers.PlayerDigType digType = event.getPacket().getPlayerDigTypes().read(0);
                if (digType == EnumWrappers.PlayerDigType.START_DESTROY_BLOCK) {
                    BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                    CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                    Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
                    while (fit.hasNext()) {
                        FakeWorld<?> fakeWorld = fit.next();
                        FakeBlock fakeBlock = fakeWorld.getFakeBlocks().get(pos);
                        if (fakeBlock != null
                                && !fakeBlock.getBlockData().getMaterial().isAir()) {
                            if (fakeWorld.onBlockPunch(cp, pos)) {
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                } else if (digType == EnumWrappers.PlayerDigType.STOP_DESTROY_BLOCK) {
                    BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                    CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                    Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
                    while (fit.hasNext()) {
                        FakeWorld<?> fakeWorld = fit.next();
                        FakeBlock fakeBlock = fakeWorld.getFakeBlocks().get(pos);
                        if (fakeBlock != null
                                && !fakeBlock.getBlockData().getMaterial().isAir()) {
                            if (fakeWorld.onBlockPunch(cp, pos)) {
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                }
            }
        });
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
                while (fit.hasNext()) {
                    FakeWorld<?> fakeWorld = fit.next();
                    //event.setCancelled(true);
                    break;
                }
            }
        });
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.USE_ITEM) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketPlayInUseItem packetPlayInUseItem = (PacketPlayInUseItem) event.getPacket().getHandle();
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                net.minecraft.server.v1_15_R1.BlockPosition nmsBlockPosition = packetPlayInUseItem.c().getBlockPosition();
                BlockPosition blockPosition = new BlockPosition(nmsBlockPosition.getX(), nmsBlockPosition.getY(), nmsBlockPosition.getZ());
                EnumDirection direction = packetPlayInUseItem.c().getDirection();
                BlockPosition blockRelative = blockPosition.add(new BlockPosition(direction.getAdjacentX(), direction.getAdjacentY(), direction.getAdjacentZ()));
                Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
                while (fit.hasNext()) {
                    FakeWorld<?> fakeWorld = fit.next();
                    if (fakeWorld.onItemUse(cp, blockPosition, blockRelative)) {
                        event.setCancelled(true);
                        boolean fixed = false;
                        Iterator<FakeWorld<?>> fit2 = cp.getFakeWorlds();
                        while (fit2.hasNext()) {
                            FakeWorld<?> fakeWorld2 = fit2.next();
                            if (fakeWorld2.updateBlock(blockRelative)) {
                                fixed = true;
                                break;
                            }
                        }
                        if (!fixed) {
                            WrappedBlockData wbd;
                            wbd = WrappedBlockData.createData(cp.getPlayer().getWorld().getBlockAt(
                                    blockRelative.getX(), blockRelative.getY(), blockRelative.getZ()).getType());
                            PacketContainer fakeBlockPacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
                            fakeBlockPacket.getBlockPositionModifier().write(0, blockRelative);
                            fakeBlockPacket.getBlockData().write(0, wbd);
                                Core.sendPacket(cp, fakeBlockPacket);
                        }
                        break;
                    }
                }
            }
        });
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer mapChunkPacket = event.getPacket();
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                ChunkCoord chunkCoord = new ChunkCoord(mapChunkPacket.getIntegers().read(0), mapChunkPacket.getIntegers().read(1));
                Map<Short, FakeBlock> fakeBlocks = new HashMap<>();
                Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
                while (fit.hasNext()) {
                    FakeWorld<?> fakeWorld = fit.next();
                    Set<BlockPosition> fakeChunk = fakeWorld.getFakeChunk(chunkCoord);
                    if (fakeChunk != null) {
                        for (BlockPosition blockPosition : fakeChunk) {
                            short relativeLoc = 0;
                            relativeLoc += (blockPosition.getX() & 15) << 12;
                            relativeLoc += (blockPosition.getZ() & 15) << 8;
                            relativeLoc += blockPosition.getY();
                            FakeBlock fakeBlock = fakeWorld.getFakeBlocks().get(blockPosition);
                            if (!fakeBlocks.containsKey(relativeLoc)) {
                                fakeBlocks.put(relativeLoc, fakeBlock);
                            }
                        }
                    }
                }
                if (!fakeBlocks.isEmpty()) {
                    PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(
                            chunkCoord,
                            fakeBlocks);
                    Core.sendPacket(event.getPlayer(), multiBlockChangePacket);
                    if (event.getPlayer().getLocation().getChunk().getX() == chunkCoord.x &&
                            event.getPlayer().getLocation().getChunk().getZ() == chunkCoord.z) {
                        Location oldLoc = event.getPlayer().getLocation().clone().add(0, 0.2, 0);
                        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> {
                            event.getPlayer().teleport(oldLoc);
                        }, 20L);
                    }
                }
                loadedChunks.get(cp.getUniqueId()).add(chunkCoord);
            }
        });
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.UNLOAD_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer unloadChunkPacket = event.getPacket();
                ChunkCoord chunkCoord = new ChunkCoord(unloadChunkPacket.getIntegers().read(0), unloadChunkPacket.getIntegers().read(1));
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                loadedChunks.get(cp.getUniqueId()).remove(chunkCoord);
            }
        });

        Core.getInstance().addTask(Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> FAKE_WORLDS.forEach(FakeWorld::pushChanges), 1L, 1L));

        globalFakeWorld = new GlobalWorld(Core.DEFAULT_WORLD);
    }
    
    public static void close() {

    }
    
    public static void onPlayerJoin(Player player) {
        loadedChunks.put(player.getUniqueId(), new HashSet<>());
    }
    
    public static void onReload() {
        for (Player player : Bukkit.getOnlinePlayers()) {
        
        }
    }
    
    public static GlobalWorld getGlobalFakeWorld() {
        return globalFakeWorld;
    }

    protected final int priority;
    private final World world;
    private final Class<FWP> fakePlayerClass;
    protected final Map<UUID, FWP> playerMap;
    protected final Map<BlockPosition, FakeBlock> fakeBlocks;
    protected final Map<ChunkCoord, Set<BlockPosition>> fakeChunks;
    protected final Map<ChunkCoord, Map<Short, FakeBlock>> chunkChanges;
    protected boolean destroyed = false;

    protected FakeWorld(int priority, World world, Class<FWP> fakePlayerClass) {
        this.priority = priority;
        this.world = world;
        this.fakePlayerClass = fakePlayerClass;
        this.playerMap = new HashMap<>();
        this.fakeBlocks = new HashMap<>();
        this.fakeChunks = new HashMap<>();
        this.chunkChanges = new HashMap<>();
        FAKE_WORLDS.add(this);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Clears all blocks from the FakeWorld without removing players.
     */
    public void clear() {
        Map<BlockPosition, FakeBlock> fakeBlocks2 = new HashMap<>(fakeBlocks);
        for (Map.Entry<BlockPosition, FakeBlock> entry : fakeBlocks2.entrySet()) {
            removeBlock(entry.getKey());
        }
        fakeBlocks.clear();
        fakeChunks.clear();
    }

    public void reset() {

    }

    /**
     * Stops tasks and packet adapters and removes players from the world
     */
    public void destroy() {
        FAKE_WORLDS.remove(this);
        destroyed = true;
        Iterator<FWP> fwpit = playerMap.values().iterator();
        while (fwpit.hasNext()) {
            FWP fwp = fwpit.next();
            fwp.getCorePlayer().leaveFakeWorld(this);
            clearPlayer(fwp.getCorePlayer());
            fwpit.remove();
        }
        fakeBlocks.clear();
        fakeChunks.clear();
        playerMap.clear();
    }

    public final Map<UUID, FWP> getPlayerMap() {
        return playerMap;
    }

    public final World getWorld() {
        return world;
    }
    
    public final Map<BlockPosition, FakeBlock> getFakeBlocks() {
        return fakeBlocks;
    }

    public final Set<BlockPosition> getFakeChunk(ChunkCoord chunkCoord) {
        return fakeChunks.get(chunkCoord);
    }
    
    protected abstract boolean onBlockPunch(CorePlayer cp, BlockPosition pos);
    
    /**
     * On player item use.
     *
     * @param cp Core Player
     * @param blockPosition Click Block
     * @param blockRelative Placed Block
     * @return Cancel Event
     */
    protected abstract boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative);

    /**
     * Hide/show players whether they're in this fake world or not.
     *
     * @param cp Core Player
     */
    protected final void applyVisibility(CorePlayer cp) {
        // Hide and hide from all players not in this GameWorld
        if (cp.getOnlineState() != DBPlayer.OnlineState.HERE) return;
        for (CorePlayer cp2 : Core.getInstance().getPlayers().getOnline()) {
            if (!cp.equals(cp2)) {
                if (!playerMap.containsKey(cp2.getPlayer().getUniqueId())) {
                    cp.getPlayer().hidePlayer(Core.getInstance(), cp2.getPlayer());
                    cp2.getPlayer().hidePlayer(Core.getInstance(), cp.getPlayer());
                } else {
                    cp.getPlayer().showPlayer(Core.getInstance(), cp2.getPlayer());
                    cp2.getPlayer().showPlayer(Core.getInstance(), cp.getPlayer());
                }
            }
        }
    }

    /**
     * Add a player to the fake world.
     *
     * @param cp Core Player
     */
    public void addPlayer(CorePlayer cp) {
        cp.joinFakeWorld(this);
        try {
            playerMap.put(cp.getUniqueId(), fakePlayerClass.getConstructor(CorePlayer.class).newInstance(cp));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            CoreLogger.logError("Unable to create player from class " + fakePlayerClass, exception);
            return;
        }
        cp.getPlayer().setCollidable(false);
        applyVisibility(cp);
        updateAll(cp);
    }

    /**
     * Clear all blocks from the player and send them back to
     * the real world.
     *
     * @param cp Core Player
     */
    public void clearPlayer(CorePlayer cp) {
        for (Map.Entry<ChunkCoord, Set<BlockPosition>> entry : fakeChunks.entrySet()) {
            Map<Short, FakeBlock> fakeBlocks = new HashMap<>();
            for (BlockPosition pos : entry.getValue()) {
                short relativeLoc = (short) (((pos.getX() & 15) << 12) + ((pos.getZ() & 15) << 8) + pos.getY());
                if (!fakeBlocks.containsKey(relativeLoc)) {
                    fakeBlocks.put(relativeLoc, new FakeBlock(getHighestBlock(pos, cp)));
                }
            }
            if (fakeBlocks.size() > 0) {
                PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(
                        entry.getKey(),
                        fakeBlocks);
                Core.sendPacket(cp, multiBlockChangePacket);
            }
        }

        Core.getInstance().applyVisibilities(cp);
    }

    /**
     * Remove a player from the fake world
     *
     * @param cp Core Player
     * @return Removed
     */
    public boolean removePlayer(CorePlayer cp) {
        if (playerMap.containsKey(cp.getUniqueId())) {
            cp.leaveFakeWorld(this);
            clearPlayer(cp);
            playerMap.remove(cp.getUniqueId());
            return true;
        }
        return false;
    }

    /**
     * Plays a sound effect at the target location for all fake world players.
     *
     * @param location Location
     * @param sound Sound
     * @param volume Volume
     * @param pitch Pitch
     */
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        for (FWP fwp : playerMap.values()) {
            fwp.getPlayer().playSound(location, sound, volume, pitch);
        }
    }

    /**
     * Spawns the particle (the number of times specified by count) at the target location.
     * The position of each particle will be randomized positively and negatively by the
     * offset parameters on each axis.
     *
     * @param particle Particle
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param count Number of particles
     */
    public void spawnParticles(Particle particle, double x, double y, double z, int count) {
        for (FWP fwp : playerMap.values()) {
            fwp.getPlayer().spawnParticle(particle, x, y, z, count);
        }
    }

    /**
     * Spawns the particle (the number of times specified by count) at the target location.
     * The position of each particle will be randomized positively and negatively by the
     * offset parameters on each axis.
     *
     * @param particle Particle
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param count Number of particles
     * @param offsetX Random offset on X axis
     * @param offsetY Random offset on Y axis
     * @param offsetZ Random offset on Z axis
     */
    public void spawnParticles(Particle particle, double x, double y, double z, int count,
                               double offsetX, double offsetY, double offsetZ) {
        for (FWP fwp : playerMap.values()) {
            fwp.getPlayer().spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ);
        }
    }

    /**
     * Spawns the particle (the number of times specified by count) at the target location.
     * The position of each particle will be randomized positively and negatively by the
     * offset parameters on each axis.
     *
     * @param particle Particle
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param count Number of particles
     * @param offsetX Random offset on X axis
     * @param offsetY Random offset on Y axis
     * @param offsetZ Random offset on Z axis
     * @param extra Extra data (normally speed)
     */
    public void spawnParticles(Particle particle, double x, double y, double z, int count,
                               double offsetX, double offsetY, double offsetZ, double extra) {
        for (FWP fwp : playerMap.values()) {
            fwp.getPlayer().spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra);
        }
    }

    /**
     * Spawns the particle (the number of times specified by count) at the target location.
     * The position of each particle will be randomized positively and negatively by the
     * offset parameters on each axis.
     *
     * @param particle Particle
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param count Number of particles
     * @param offsetX Random offset on X axis
     * @param offsetY Random offset on Y axis
     * @param offsetZ Random offset on Z axis
     * @param extra Extra data (normally speed)
     * @param options Particle Options
     */
    public void spawnParticles(Particle particle, double x, double y, double z, int count,
                               double offsetX, double offsetY, double offsetZ, double extra,
                               Particle.DustOptions options) {
        for (FWP fwp : playerMap.values()) {
            fwp.getPlayer().spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, options);
        }
    }

    /**
     * Returns true if the fake block is air or if there is no fake
     * block stored at the position
     *
     * @param pos Block Position
     * @return Solidity
     */
    public final boolean isBlockSolid(BlockPosition pos) {
        return fakeBlocks.containsKey(pos) && !fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.AIR);
    }

    /**
     * Sets a fake block status in the world with the option to
     * instantly update and display or not
     *
     * @param pos Block Position
     * @param blockData Block Data
     */
    public boolean setBlock(BlockPosition pos, BlockData blockData) {
        if (!world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType().isAir()) {
            return false;
        }
        ChunkCoord coord = ChunkCoord.fromBlockPos(pos);
        FakeBlock fb = new FakeBlock(blockData);
        fakeBlocks.put(pos, fb);
        if (!fakeChunks.containsKey(coord)) {
            fakeChunks.put(coord, new HashSet<>());
        }
        fakeChunks.get(coord).add(pos);
        updateBlock(pos);
        return true;
    }

    /**
     * Removes a block from the FakeWorld
     *
     * @param pos Block Position
     */
    public void removeBlock(BlockPosition pos) {
        ChunkCoord coord = ChunkCoord.fromBlockPos(pos);
        if (fakeBlocks.remove(pos) != null) {
            fakeChunks.get(coord).remove(pos);
            if (fakeChunks.get(coord).isEmpty()) {
                fakeChunks.remove(coord);
            }
            updateBlock(pos);
        }
    }

    /**
     * Fill fake blocks with a set of fake blocks
     * and push an updateAll call
     *
     * @param blocks Fake Blocks
     */
    public void setBlocks(Map<BlockPosition, FakeBlock> blocks) {
        for (Map.Entry<BlockPosition, FakeBlock> entry : blocks.entrySet()) {
            if (entry.getValue().getBlockData().getMaterial().isAir()) {
                removeBlock(entry.getKey());
            } else {
                setBlock(entry.getKey(), entry.getValue().getBlockData());
            }
        }
    }

    /**
     * Like setBlocks, but for clearing and setting new blocks without having to send two packets
     *
     * @param blocks Fake Blocks
     */
    public void overwriteBlocks(Map<BlockPosition, FakeBlock> blocks) {
        Map<BlockPosition, FakeBlock> prevBlocks = new HashMap<>(fakeBlocks);
        for (Map.Entry<BlockPosition, FakeBlock> entry : prevBlocks.entrySet()) {
            BlockPosition pos = entry.getKey();
            if (!blocks.containsKey(pos)) {
                removeBlock(entry.getKey());
            } else {
                if (blocks.get(pos).getBlockData().getMaterial().isAir()) {
                    removeBlock(pos);
                } else {
                    setBlock(pos, blocks.get(pos).getBlockData());
                }
                blocks.remove(pos);
            }
        }
        for (Map.Entry<BlockPosition, FakeBlock> entry : blocks.entrySet()) {
            if (entry.getValue().getBlockData().getMaterial().isAir()) {
                removeBlock(entry.getKey());
            } else {
                setBlock(entry.getKey(), entry.getValue().getBlockData());
            }
        }
    }

    /**
     * Replace all non-air fake blocks with new block
     *
     * @param blockPositions
     * @param toBlock
     * @return Set of changed blocks
     */
    public Map<BlockPosition, FakeBlock> replaceBlocks(Set<BlockPosition> blockPositions, BlockData toBlock) {
        Map<BlockPosition, FakeBlock> prevBlocks = new HashMap<>();
        for (BlockPosition pos : blockPositions) {
            FakeBlock fb = fakeBlocks.get(pos);
            if (fb != null && !fb.getBlockData().getMaterial().isAir()) {
                prevBlocks.put(pos, fb);
                setBlock(pos, toBlock);
            }
        }
        return prevBlocks;
    }

    /**
     * Replace all air blocks with new block
     *
     * @param blocks
     * @return Set of changed blocks
     */
    public Set<BlockPosition> replaceAir(Map<BlockPosition, FakeBlock> blocks) {
        Set<BlockPosition> prevBlocks = new HashSet<>();
        for (Map.Entry<BlockPosition, FakeBlock> block : blocks.entrySet()) {
            FakeBlock fb = fakeBlocks.get(block.getKey());
            if (fb == null || fb.getBlockData().getMaterial().isAir()) {
                prevBlocks.add(block.getKey());
                setBlock(block.getKey(), block.getValue().getBlockData());
            }
        }
        return prevBlocks;
    }

    /**
     * Break blocks in a radius
     *
     * @param pos Origin
     * @param radius Radius
     * @return Number of Successes
     */
    public int breakBlocks(BlockPosition pos, double radius, double percent) {
        double dx, dy, dz;
        int broken = 0;
        Random random = new Random();
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double)x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double)y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double)z) / radius;
                    if (Math.sqrt(dx*dx + dy*dy + dz*dz) < 1
                            && random.nextDouble() <= percent) {
                        broken += breakBlock(pos.add(new BlockPosition(x, y, z)), null) ? 1 : 0;
                    }
                }
            }
        }
        return broken;
    }

    /**
     * Break blocks in a radius
     *
     * @param pos Origin
     * @param minRadius Minimum Radius
     * @param maxRadius Maximum Radius
     * @return Number of Successes
     */
    public int breakBlocks(BlockPosition pos, double minRadius, double maxRadius, double percent) {
        double dx, dy, dz;
        int broken = 0;
        Random random = new Random();
        for (int x = -(int) Math.ceil(maxRadius); x <= (int) Math.ceil(maxRadius); x++) {
            dx = ((double)x) / maxRadius;
            for (int y = -(int) Math.ceil(maxRadius); y <= (int) Math.ceil(maxRadius); y++) {
                dy = ((double)y) / maxRadius;
                for (int z = -(int) Math.ceil(maxRadius); z <= (int) Math.ceil(maxRadius); z++) {
                    dz = ((double)z) / maxRadius;
                    double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                    if (dist < 1 && dist > minRadius / maxRadius
                            && random.nextDouble() <= percent) {
                        broken += breakBlock(pos.add(new BlockPosition(x, y, z)), null) ? 1 : 0;
                    }
                }
            }
        }
        return broken;
    }

    public int breakBlocks(BoundingBox boundingBox) {
        int broken = 0;
        for (int x = (int) Math.floor(boundingBox.getMinX()); x < Math.ceil(boundingBox.getMaxX()); x++) {
            for (int y = (int) Math.floor(boundingBox.getMinY()); y < Math.ceil(boundingBox.getMaxY()); y++) {
                for (int z = (int) Math.floor(boundingBox.getMinZ()); z < Math.ceil(boundingBox.getMaxZ()); z++) {
                    broken += breakBlock(new BlockPosition(x, y, z), null) ? 1 : 0;
                }
            }
        }
        return broken;
    }

    /**
     * Breaks a block and plays breaking block sound
     *
     * @param pos Block Position
     * @param cp Core Player
     * @return Success
     */
    public boolean breakBlock(BlockPosition pos, CorePlayer cp) {
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null && !fb.getBlockData().getMaterial().equals(Material.AIR)) {
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(world).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, fb.getBlockData());
                if (cp != null && !cp.equals(fwp.getCorePlayer()))
                    fwp.getPlayer().playSound(pos.toLocation(world), fb.getBreakSound(), 1, 1);
            });
            removeBlock(pos);
            return true;
        } else {
            updateBlock(pos);
        }
        return false;
    }
    
    /**
     * Place a block in the fake world if the block is air
     *
     * @param pos Block Position
     * @param material Material
     * @param cp Placer
     * @return Success
     */
    public boolean placeBlock(BlockPosition pos, Material material, CorePlayer cp) {
        if ((!fakeBlocks.containsKey(pos)
                || fakeBlocks.get(pos).getBlockData().getMaterial().isAir())
                && getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).isEmpty()) {
            setBlock(pos, material.createBlockData());
            getPlayerMap().values().forEach(fwp -> {
                if (cp != null && !cp.equals(fwp.getCorePlayer()))
                    fwp.getPlayer().playSound(pos.toLocation(world), fakeBlocks.get(pos).getPlaceSound(), 1, 1);
            });
            return true;
        } else {
            updateBlock(pos);
        }
        return false;
    }
    
    /**
     * Send a fake block packet to all players
     *
     * @param pos Block Position
     * @return Success
     */
    public boolean updateBlock(BlockPosition pos) {
        WrappedBlockData wbd;
        FakeBlock fb = fakeBlocks.get(pos);
        ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
        if (!chunkChanges.containsKey(chunkCoord)) {
            chunkChanges.put(chunkCoord, new HashMap<>());
        }
        chunkChanges.get(chunkCoord).put((short) ((pos.getX() & 15) + (pos.getZ() & 15) + (pos.getY())), fb);
        return fb != null;
    }
    
    /**
     * Returns the highest priority FakeBlock for a player
     *
     * @param pos Block Position
     * @param cp Core Player
     * @return Highest Priority Fake Block
     */
    public static BlockData getHighestBlock(BlockPosition pos, CorePlayer cp) {
        Iterator<FakeWorld<?>> it = cp.getFakeWorlds();
        FakeBlock fakeBlock;
        while (it.hasNext()) {
            FakeWorld<?> fakeWorld = it.next();
            if ((fakeBlock = fakeWorld.getFakeBlocks().get(pos)) != null) {
                return fakeBlock.getBlockData();
            }
        }
        return cp.getLocation().getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getBlockData();
    }

    public void updateAll(CorePlayer cp) {
        for (Map.Entry<ChunkCoord, Set<BlockPosition>> entry : fakeChunks.entrySet()) {
            Map<Short, FakeBlock> fakeBlocks = new HashMap<>();
            for (BlockPosition pos : entry.getValue()) {
                short relativeLoc = (short) (((pos.getX() & 15) << 12) + ((pos.getZ() & 15) << 8) + pos.getY());
                if (!fakeBlocks.containsKey(relativeLoc)) {
                    fakeBlocks.put(relativeLoc, new FakeBlock(getHighestBlock(pos, cp)));
                }
            }
            if (fakeBlocks.size() > 0) {
                PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(
                        entry.getKey(),
                        fakeBlocks);
                Core.sendPacket(cp, multiBlockChangePacket);
            }
        }
    }

    /*
    public void updateAll() {
        for (FWP fwp : getPlayerMap().values()) {
            updateAll(fwp.getCorePlayer());
        }
    }
    */

    public void pushChanges() {
        chunkChanges.forEach(((chunkCoord, blockChanges) -> {
            PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(chunkCoord, blockChanges);
            for (FWP fwp : playerMap.values()) {
                Core.sendPacket(fwp.getCorePlayer(), multiBlockChangePacket);
            }
        }));
        chunkChanges.clear();
    }

}
