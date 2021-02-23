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
import com.spleefleague.core.util.variable.MultiBlockChange;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.core.util.PacketUtils;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import net.minecraft.server.v1_15_R1.EnumDirection;
import net.minecraft.server.v1_15_R1.PacketPlayInUseItem;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public abstract class FakeWorld<FWP extends FakeWorldPlayer> {

    private static GlobalWorld globalFakeWorld;
    private final static Set<FakeWorld<?>> FAKE_WORLDS = new HashSet<>();

    public static void init() {
        BuildWorld.init();
        FakeBlock.init();
        GlobalWorld.init();

        Core.getInstance().addTask(Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            Core.getInstance().getPlayers().getAllLocal().forEach(cp -> {
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
                        FakeBlock fakeBlock = fakeWorld.getFakeBlock(pos);
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
                        FakeBlock fakeBlock = fakeWorld.getFakeBlock(pos);
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

            }
        });
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.USE_ITEM) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                if (cp == null) return;
                PacketPlayInUseItem packetPlayInUseItem = (PacketPlayInUseItem) event.getPacket().getHandle();
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
                            if (fakeWorld2.tryFix(cp, blockRelative)) {
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
                PacketContainer chunkDataPacket = event.getPacket();
                boolean fullChunk = chunkDataPacket.getBooleans().read(0);
                if (!fullChunk) {
                    return;
                }
                ChunkCoord chunkCoord = new ChunkCoord(chunkDataPacket.getIntegers().read(0), chunkDataPacket.getIntegers().read(1));
                LOADED_CHUNKS.get(event.getPlayer().getUniqueId()).add(chunkCoord);
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                if (cp == null) return;
                Map<Short, FakeBlock> fakeBlocks = new HashMap<>();
                Iterator<FakeWorld<?>> fit = cp.getFakeWorlds();
                while (fit.hasNext()) {
                    FakeWorld<?> fakeWorld = fit.next();
                    Map<Short, FakeBlock> fakeChunk = fakeWorld.getFakeChunk(chunkCoord);
                    if (fakeChunk != null) {
                        for (Map.Entry<Short, FakeBlock> entry : fakeChunk.entrySet()) {
                            if (!fakeBlocks.containsKey(entry.getKey())) {
                                fakeBlocks.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                }
                if (!fakeBlocks.isEmpty()) {
                    PacketUtils.writeFakeChunkDataPacket(chunkDataPacket, fakeBlocks);
                    if (event.getPlayer().getLocation().getChunk().getX() == chunkCoord.x &&
                            event.getPlayer().getLocation().getChunk().getZ() == chunkCoord.z) {
                        event.getPlayer().setGravity(false);
                        event.getPlayer().setVelocity(new Vector(0, 0, 0));
                        event.getPlayer().teleport(event.getPlayer().getLocation().clone().add(0, 0.2, 0));
                        Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> event.getPlayer().setGravity(true), 20L);
                    }
                }
            }
        });
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.UNLOAD_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer unloadChunkPacket = event.getPacket();
                ChunkCoord chunkCoord = new ChunkCoord(unloadChunkPacket.getIntegers().read(0), unloadChunkPacket.getIntegers().read(1));
                LOADED_CHUNKS.get(event.getPlayer().getUniqueId()).remove(chunkCoord);
            }
        });

        globalFakeWorld = new GlobalWorld(Core.DEFAULT_WORLD);
    }

    public static void close() {

    }

    private static final Map<UUID, Set<ChunkCoord>> LOADED_CHUNKS = new HashMap<>();

    public static void onPlayerJoin(UUID uuid) {
        LOADED_CHUNKS.put(uuid, new HashSet<>());
    }

    public static void onPlayerQuit(UUID uuid) {
        LOADED_CHUNKS.remove(uuid);
    }

    public static GlobalWorld getGlobalFakeWorld() {
        return globalFakeWorld;
    }

    protected static AtomicInteger worldCounter = new AtomicInteger();

    protected final int worldId;
    protected final int priority;
    private final World world;
    private final Class<FWP> fakePlayerClass;
    protected final Map<UUID, FWP> playerMap;
    protected final Map<ChunkCoord, Map<Short, FakeBlock>> fakeChunks;
    protected boolean destroyed = false;

    private static class RepeatingTask {
        Runnable runnable;
        int repeats;
        final int delay;
        int cursor = 0;

        public RepeatingTask(Runnable runnable, int repeats, int delay) {
            this.runnable = runnable;
            this.repeats = repeats;
            this.delay = delay;
        }

        public boolean onRun() {
            if (++cursor >= delay) {
                cursor = 0;
                runnable.run();
                return --repeats > 0;
            }
            return true;
        }

    }

    protected final List<RepeatingTask> repeatingTasks = new ArrayList<>();
    protected BukkitTask repeatingTask;

    protected FakeWorld(int priority, World world, Class<FWP> fakePlayerClass) {
        this.worldId = worldCounter.getAndIncrement();
        this.priority = priority;
        this.world = world;
        this.fakePlayerClass = fakePlayerClass;
        this.playerMap = new HashMap<>();
        this.fakeChunks = new HashMap<>();
        FAKE_WORLDS.add(this);

        repeatingTask = Bukkit.getScheduler().runTaskTimer(
                Core.getInstance(),
                () -> repeatingTasks.removeIf(repeatingTask -> !repeatingTask.onRun()),
                1L, 1L);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * Clears all blocks from the FakeWorld without removing players.
     */
    public void clear() {
        for (Map.Entry<ChunkCoord, Map<Short, FakeBlock>> entry : fakeChunks.entrySet()) {
            List<MultiBlockChange> blockChanges = new ArrayList<>();
            for (short pos : entry.getValue().keySet()) {
                blockChanges.add(new MultiBlockChange(pos));
            }
            sendMultiBlockChangePacket(entry.getKey(), blockChanges);
        }
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
        clear();
        while (fwpit.hasNext()) {
            FWP fwp = fwpit.next();
            fwp.getCorePlayer().leaveFakeWorld(this);
            fwpit.remove();
        }
        fakeChunks.clear();
        playerMap.clear();
        repeatingTasks.clear();
        repeatingTask.cancel();
    }

    public void addRepeatingTask(Runnable runnable, int repeats, int delay) {
        repeatingTasks.add(new RepeatingTask(runnable, repeats, delay));
    }

    public final Map<UUID, FWP> getPlayerMap() {
        return playerMap;
    }

    public final World getWorld() {
        return world;
    }

    public final FakeBlock getFakeBlock(BlockPosition pos) {
        ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
        if (!fakeChunks.containsKey(chunkCoord)) return null;
        return fakeChunks.get(chunkCoord).get(getChunkRelativePos(pos));
    }

    public final Map<Short, FakeBlock> getFakeChunk(ChunkCoord chunkCoord) {
        return fakeChunks.get(chunkCoord);
    }

    protected abstract boolean onBlockPunch(CorePlayer cp, BlockPosition pos);

    /**
     * On player item use.
     *
     * @param cp            Core Player
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
    protected void applyVisibility(CorePlayer cp) {
        // Hide and hide from all players not in this GameWorld
        if (cp.getOnlineState() != DBPlayer.OnlineState.HERE) return;
        for (CorePlayer cp2 : Core.getInstance().getPlayers().getAllLocal()) {
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
        applyVisibility(cp);
        updateAll(cp);
    }

    /**
     * Clear all blocks from the player and send them back to
     * the real world.
     *
     * @param fwp Fake World Player
     */
    public void clearPlayer(FWP fwp) {
        for (Map.Entry<ChunkCoord, Map<Short, FakeBlock>> entry : fakeChunks.entrySet()) {
            List<MultiBlockChange> blockChanges = new ArrayList<>();
            for (short pos : entry.getValue().keySet()) {
                blockChanges.add(new MultiBlockChange(pos, AIR));
            }
            sendMultiBlockChangePacket(entry.getKey(), blockChanges, fwp);
        }

        Core.getInstance().applyVisibilities(fwp.getCorePlayer());
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
            clearPlayer(playerMap.get(cp.getUniqueId()));
            playerMap.remove(cp.getUniqueId());
            return true;
        }
        return false;
    }

    /**
     * Plays a sound effect at the target location for all fake world players.
     *
     * @param location Location
     * @param sound    Sound
     * @param volume   Volume
     * @param pitch    Pitch
     */
    @SuppressWarnings("unused")
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
     * @param x        X position
     * @param y        Y position
     * @param z        Z position
     * @param count    Number of particles
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
     * @param x        X position
     * @param y        Y position
     * @param z        Z position
     * @param count    Number of particles
     * @param offsetX  Random offset on X axis
     * @param offsetY  Random offset on Y axis
     * @param offsetZ  Random offset on Z axis
     */
    @SuppressWarnings("unused")
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
     * @param x        X position
     * @param y        Y position
     * @param z        Z position
     * @param count    Number of particles
     * @param offsetX  Random offset on X axis
     * @param offsetY  Random offset on Y axis
     * @param offsetZ  Random offset on Z axis
     * @param extra    Extra data (normally speed)
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
     * @param x        X position
     * @param y        Y position
     * @param z        Z position
     * @param count    Number of particles
     * @param offsetX  Random offset on X axis
     * @param offsetY  Random offset on Y axis
     * @param offsetZ  Random offset on Z axis
     * @param extra    Extra data (normally speed)
     * @param options  Particle Options
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
        FakeBlock fb = getFakeBlock(pos);
        return fb != null && !fb.getBlockData().getMaterial().equals(Material.AIR);
    }

    @SuppressWarnings("unused")
    public final boolean isReallySolid(BlockPosition pos) {
        return isBlockSolid(pos) || !getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getBlockData().getMaterial().isAir();
    }

    /**
     * Sets a fake block status in the world with the option to
     * instantly update and display or not
     *
     * @param pos       Block Position
     * @param fakeBlock Fake Block Data
     */
    public boolean setBlock(BlockPosition pos, FakeBlock fakeBlock) {
        if (!world.getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getType().isAir()) {
            return false;
        }
        ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
        short relPos = getChunkRelativePos(pos);
        if (!fakeChunks.containsKey(chunkCoord)) {
            fakeChunks.put(chunkCoord, new HashMap<>());
        }
        fakeChunks.get(chunkCoord).put(relPos, fakeBlock);
        sendBlockChangePacket(chunkCoord, pos, fakeBlock);
        return true;
    }

    private static short getChunkRelativePos(BlockPosition pos) {
        return (short) ((pos.getX() & 0xF) + ((pos.getZ() & 0xF) << 4) + ((pos.getY() & 0xFF) << 8));
    }

    /**
     * Sets a fake block status in the world with the option to
     * instantly update and display or not
     *  @param pos       Block Position
     * @param blockData Block Data
     */
    public void setBlockForced(BlockPosition pos, BlockData blockData) {
        ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
        FakeBlock fb = new FakeBlock(blockData);
        short relPos = getChunkRelativePos(pos);
        if (!fakeChunks.containsKey(chunkCoord)) {
            fakeChunks.put(chunkCoord, new HashMap<>());
        }
        fakeChunks.get(chunkCoord).put(relPos, fb);
        sendBlockChangePacket(chunkCoord, pos, fb);
    }

    /**
     * Removes a block from the FakeWorld
     *
     * @param pos Block Position
     */
    public void removeBlock(BlockPosition pos) {
        ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
        if (fakeChunks.containsKey(chunkCoord)) {
            short relPos = getChunkRelativePos(pos);
            fakeChunks.get(chunkCoord).remove(relPos);
            if (fakeChunks.get(chunkCoord).isEmpty()) {
                fakeChunks.remove(chunkCoord);
            }
        }
        sendBlockChangePacket(chunkCoord, pos, AIR);
    }

    @SuppressWarnings("unused")
    protected void removeBlocks(ChunkCoord chunkCoord, List<Short> blockPositions) {
        blockPositions.forEach(fakeChunks.get(chunkCoord)::remove);
        if (fakeChunks.get(chunkCoord).isEmpty()) {
            fakeChunks.remove(chunkCoord);
        }
    }

    /**
     * Fill fake blocks with a set of fake blocks
     * and push an updateAll call
     *
     * @param blocks Fake Blocks
     */
    public void setBlocks(Map<BlockPosition, FakeBlock> blocks) {
        Map<ChunkCoord, List<MultiBlockChange>> blockChanges = new HashMap<>();
        for (Map.Entry<BlockPosition, FakeBlock> entry : blocks.entrySet()) {
            ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(entry.getKey());
            short relPos = getChunkRelativePos(entry.getKey());
            blockChanges.putIfAbsent(chunkCoord, new ArrayList<>());
            if (entry.getValue().getBlockData().getMaterial().isAir()) {
                blockChanges.get(chunkCoord).add(new MultiBlockChange(relPos));
            } else {
                blockChanges.get(chunkCoord).add(new MultiBlockChange(relPos, entry.getValue()));
            }
        }
        setChunks(blockChanges);
    }

    protected void setChunks(Map<ChunkCoord, List<MultiBlockChange>> blocks) {
        blocks.forEach(this::setChunkBlocks);
    }

    protected void setChunkBlocks(ChunkCoord chunkCoord, List<MultiBlockChange> blocks) {
        if (!fakeChunks.containsKey(chunkCoord)) {
            fakeChunks.put(chunkCoord, new HashMap<>());
        }
        Map<Short, FakeBlock> chunk = fakeChunks.get(chunkCoord);
        for (MultiBlockChange change : blocks) {
            if (change.air) {
                chunk.remove(change.pos);
            } else {
                chunk.put(change.pos, change.fakeBlock);
            }
        }
        sendMultiBlockChangePacket(chunkCoord, blocks);
    }

    public void setBlocksForced(Map<BlockPosition, FakeBlock> blocks) {
        for (Map.Entry<BlockPosition, FakeBlock> entry : blocks.entrySet()) {
            if (entry.getValue().getBlockData().getMaterial().isAir()) {
                removeBlock(entry.getKey());
            } else {
                setBlockForced(entry.getKey(), entry.getValue().getBlockData());
            }
        }
    }

    /**
     * Like setBlocks, but for clearing and setting new blocks with minimal packets sent
     *
     * @param blocks Fake Blocks
     */
    public void overwriteBlocks(Map<BlockPosition, FakeBlock> blocks) {
        clear();
        setBlocks(blocks);
        /*
        Map<ChunkCoord, List<MultiBlockChange>> blockChanges = new HashMap<>();
        for (Map.Entry<BlockPosition, FakeBlock> entry : prevBlocks.entrySet()) {
            BlockPosition pos = entry.getKey();
            short relPos = (short) ((pos.getX() & 0xF) + (pos.getZ() & 0xF) * 16 + (pos.getY() & 0xF) * 256);
            ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
            if (!blockChanges.containsKey(chunkCoord)) {
                blockChanges.put(chunkCoord, new ArrayList<>());
            }
            if (!blocks.containsKey(pos)) {
                blockChanges.get(chunkCoord).add(new MultiBlockChange(relPos));
            } else {
                if (blocks.get(pos).getBlockData().getMaterial().isAir()) {
                    blockChanges.get(chunkCoord).add(new MultiBlockChange(relPos));
                } else {
                    blockChanges.get(chunkCoord).add(new MultiBlockChange(relPos, blocks.get(pos)));
                }
                blocks.remove(pos);
            }
        }
        for (Map.Entry<BlockPosition, FakeBlock> entry : blocks.entrySet()) {
            BlockPosition pos = entry.getKey();
            short relPos = (short) ((pos.getX() & 0xF) + (pos.getZ() & 0xF) * 16 + (pos.getY() & 0xF) * 256);
            ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
            blockChanges.get(chunkCoord).add(new MultiBlockChange(relPos, entry.getValue()));
        }
        setBlocks(blockChanges);
         */
    }

    /**
     * Replace all non-air fake blocks with new block
     *
     * @param blockPositions Block Positions
     * @param fakeBlock     Replacement Fake Block
     * @return Set of changed blocks
     */
    @SuppressWarnings("UnusedReturnValue")
    public Map<BlockPosition, FakeBlock> replaceBlocks(Set<BlockPosition> blockPositions, FakeBlock fakeBlock) {
        Map<BlockPosition, FakeBlock> prevBlocks = new HashMap<>();
        Map<ChunkCoord, List<MultiBlockChange>> blockChanges = new HashMap<>();
        for (BlockPosition pos : blockPositions) {
            ChunkCoord chunkCoord = ChunkCoord.fromBlockPos(pos);
            short relPos = getChunkRelativePos(pos);
            FakeBlock fb = fakeChunks.get(chunkCoord).get(relPos);
            if (fb != null && !fb.getBlockData().getMaterial().isAir()) {
                if (!blockChanges.containsKey(chunkCoord)) {
                    blockChanges.put(chunkCoord, new ArrayList<>());
                }
                prevBlocks.put(pos, fb);
                blockChanges.get(chunkCoord).add(new MultiBlockChange(relPos, fakeBlock));
            }
        }

        blockChanges.forEach(this::sendMultiBlockChangePacket);

        return prevBlocks;
    }

    /**
     * Replace all air blocks with new block
     *
     * @param blocks Fake Blocks
     * @return Set of changed blocks
     */
    @SuppressWarnings("unused")
    public Set<BlockPosition> replaceAir(Map<BlockPosition, FakeBlock> blocks) {
        Set<BlockPosition> prevBlocks = new HashSet<>();
        for (Map.Entry<BlockPosition, FakeBlock> block : blocks.entrySet()) {
            FakeBlock fakeBlock = getFakeBlock(block.getKey());
            if ((fakeBlock == null || fakeBlock.getBlockData().getMaterial().isAir()) && getWorld().getBlockAt(block.getKey().toLocation(getWorld())).getType().isAir()) {
                prevBlocks.add(block.getKey());
                setBlock(block.getKey(), block.getValue());
            }
        }

        return prevBlocks;
    }

    /**
     * Break blocks in a radius
     *
     * @param pos    Origin
     * @param radius Radius
     * @return Number of Successes
     */
    public int breakBlocks(BlockPosition pos, double radius, double percent) {
        double dx, dy, dz;
        int broken = 0;
        Random random = new Random();
        for (int x = -(int) Math.ceil(radius); x <= (int) Math.ceil(radius); x++) {
            dx = ((double) x) / radius;
            for (int y = -(int) Math.ceil(radius); y <= (int) Math.ceil(radius); y++) {
                dy = ((double) y) / radius;
                for (int z = -(int) Math.ceil(radius); z <= (int) Math.ceil(radius); z++) {
                    dz = ((double) z) / radius;
                    if (Math.sqrt(dx * dx + dy * dy + dz * dz) < 1
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
     * @param pos       Origin
     * @param minRadius Minimum Radius
     * @param maxRadius Maximum Radius
     * @return Number of Successes
     */
    public int breakBlocks(BlockPosition pos, double minRadius, double maxRadius, double percent) {
        double dx, dy, dz;
        int broken = 0;
        Random random = new Random();
        for (int x = -(int) Math.ceil(maxRadius); x <= (int) Math.ceil(maxRadius); x++) {
            dx = ((double) x) / maxRadius;
            for (int y = -(int) Math.ceil(maxRadius); y <= (int) Math.ceil(maxRadius); y++) {
                dy = ((double) y) / maxRadius;
                for (int z = -(int) Math.ceil(maxRadius); z <= (int) Math.ceil(maxRadius); z++) {
                    dz = ((double) z) / maxRadius;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
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
     * @param cp  Core Player
     * @return Success
     */
    public boolean breakBlock(BlockPosition pos, CorePlayer cp) {
        FakeBlock fakeBlock = getFakeBlock(pos);
        if (fakeBlock != null && !fakeBlock.getBlockData().getMaterial().equals(Material.AIR)) {
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(world).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, fakeBlock.getBlockData());
                if (cp != null && !cp.equals(fwp.getCorePlayer())) {
                    fwp.getPlayer().playSound(pos.toLocation(world), fakeBlock.getBreakSound(), 1, 1);
                }
            });
            removeBlock(pos);
            return true;
        }
        return false;
    }

    /**
     * Place a block in the fake world if the block is air
     *
     * @param pos      Block Position
     * @param material Material
     * @param cp       Placer
     * @return Success
     */
    public boolean placeBlock(BlockPosition pos, Material material, CorePlayer cp) {
        FakeBlock fakeBlock = getFakeBlock(pos);
        if ((fakeBlock == null
                || fakeBlock.getBlockData().getMaterial().isAir())
                && getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()).isEmpty()) {
            FakeBlock placed = new FakeBlock(material.createBlockData());
            setBlock(pos, placed);
            getPlayerMap().values().forEach(fwp -> {
                if (cp != null && !cp.equals(fwp.getCorePlayer())) {
                    fwp.getPlayer().playSound(pos.toLocation(world), placed.getPlaceSound(), 1, 1);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public static final FakeBlock AIR = new FakeBlock(Material.AIR.createBlockData());

    /**
     * Returns the highest priority FakeBlock for a player
     *
     * @param pos Block Position
     * @param cp  Core Player
     * @return Highest Priority Fake Block
     */
    @SuppressWarnings("unused")
    public static BlockData getPriorityBlock(BlockPosition pos, CorePlayer cp) {
        Iterator<FakeWorld<?>> it = cp.getFakeWorlds();
        FakeBlock fakeBlock;
        while (it.hasNext()) {
            FakeWorld<?> fakeWorld = it.next();
            if ((fakeBlock = fakeWorld.getFakeBlock(pos)) != null) {
                return fakeBlock.getBlockData();
            }
        }
        return Objects.requireNonNull(cp.getLocation().getWorld()).getBlockAt(pos.getX(), pos.getY(), pos.getZ()).getBlockData();
    }

    public void updateAll(CorePlayer cp) {
        Set<ChunkCoord> loadedChunks = LOADED_CHUNKS.get(cp.getUniqueId());
        for (Map.Entry<ChunkCoord, Map<Short, FakeBlock>> chunkEntry : fakeChunks.entrySet()) {
            if (!loadedChunks.contains(chunkEntry.getKey()) || chunkEntry.getValue().isEmpty()) continue;

            List<MultiBlockChange> blockChanges = new ArrayList<>();
            for (Map.Entry<Short, FakeBlock> blockEntry : chunkEntry.getValue().entrySet()) {
                blockChanges.add(new MultiBlockChange(blockEntry.getKey(), blockEntry.getValue()));
            }
            PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(
                    chunkEntry.getKey(),
                    blockChanges);
            Core.sendPacket(cp, multiBlockChangePacket);
        }
    }

    public boolean tryFix(CorePlayer cp, BlockPosition pos) {
        FakeBlock fakeBlock = getFakeBlock(pos);
        if (fakeBlock != null) {
            Core.sendPacket(cp, PacketUtils.createBlockChangePacket(pos, fakeBlock));
            return true;
        }
        return false;
    }

    public void sendBlockChangePacket(ChunkCoord chunkCoord, BlockPosition pos, FakeBlock fb) {
        PacketContainer blockChangePacket = PacketUtils.createBlockChangePacket(pos, fb);
        for (FWP fwp : playerMap.values()) {
            if (LOADED_CHUNKS.get(fwp.getCorePlayer().getUniqueId()).contains(chunkCoord)) {
                Core.sendPacket(fwp.getPlayer(), blockChangePacket);
            }
        }
    }

    public void sendMultiBlockChangePacket(ChunkCoord chunkCoord, List<MultiBlockChange> fakeBlockChanges) {
        PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(chunkCoord, fakeBlockChanges);
        for (FWP fwp : playerMap.values()) {
            if (LOADED_CHUNKS.get(fwp.getCorePlayer().getUniqueId()).contains(chunkCoord)) {
                Core.sendPacket(fwp.getPlayer(), multiBlockChangePacket);
            }
        }
    }

    public void sendMultiBlockChangePacket(ChunkCoord chunkCoord, List<MultiBlockChange> fakeBlockChanges, FWP fwp) {
        PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(chunkCoord, fakeBlockChanges);
        if (LOADED_CHUNKS.get(fwp.getCorePlayer().getUniqueId()).contains(chunkCoord)) {
            Core.sendPacket(fwp.getPlayer(), multiBlockChangePacket);
        }
    }

    /*
    public void updateAll() {
        for (FWP fwp : getPlayerMap().values()) {
            updateAll(fwp.getCorePlayer());
        }
    }
    */

}
