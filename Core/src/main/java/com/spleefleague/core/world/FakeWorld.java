package com.spleefleague.core.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.build.BuildWorld;
import com.spleefleague.core.world.game.GameWorld;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.core.util.PacketUtils;
import net.minecraft.server.v1_15_R1.EnumDirection;
import net.minecraft.server.v1_15_R1.PacketPlayInUseItem;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public abstract class FakeWorld {
    
    private final static Map<UUID, ChunkCoord> loadedChunks = new HashMap<>();
    private final static List<PacketAdapter> packetAdapters = new ArrayList<>();
    private static GlobalWorld globalFakeWorld;

    public static void init() {
        BuildWorld.init();
        FakeBlock.init();
        GlobalWorld.init();
        
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    if (fakeWorld.onBlockPunch(cp, pos)) {
                        event.setCancelled(true);
                    }
                }
            }
        });
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    //event.setCancelled(true);
                    break;
                }
            }
        });
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.USE_ITEM) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketPlayInUseItem packetPlayInUseItem = (PacketPlayInUseItem) event.getPacket().getHandle();
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                net.minecraft.server.v1_15_R1.BlockPosition nmsBlockPosition = packetPlayInUseItem.c().getBlockPosition();
                BlockPosition blockPosition = new BlockPosition(nmsBlockPosition.getX(), nmsBlockPosition.getY(), nmsBlockPosition.getZ());
                EnumDirection direction = packetPlayInUseItem.c().getDirection();
                BlockPosition blockRelative = blockPosition.add(new BlockPosition(direction.getAdjacentX(), direction.getAdjacentY(), direction.getAdjacentZ()));
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    if (fakeWorld.onItemUse(cp, blockPosition, blockRelative)) {
                        event.setCancelled(true);
                        boolean fixed = false;
                        for (FakeWorld fakeWorld2 : cp.getFakeWorlds()) {
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
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer mapChunkPacket = event.getPacket();
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                ChunkCoord chunkCoord = new ChunkCoord(mapChunkPacket.getIntegers().read(0), mapChunkPacket.getIntegers().read(1));
                Map<Short, FakeBlock> fakeBlocks = new HashMap<>();
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
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
                if (fakeBlocks.size() > 0) {
                    PacketContainer multiBlockChangePacket = PacketUtils.createMultiBlockChangePacket(chunkCoord, fakeBlocks);
                    Core.sendPacket(event.getPlayer(), multiBlockChangePacket);
                }
            }
        });
        
        globalFakeWorld = new GlobalWorld(Core.DEFAULT_WORLD);
    }
    
    public static void close() {
        packetAdapters.forEach(Core.getProtocolManager()::removePacketListener);
    }
    
    public static GlobalWorld getGlobalFakeWorld() {
        return globalFakeWorld;
    }
    
    protected static void addPacketAdapter(PacketAdapter packetAdapter) {
        Core.getProtocolManager().addPacketListener(packetAdapter);
        packetAdapters.add(packetAdapter);
    }
    
    private final World world;
    private final Class<? extends FakeWorldPlayer> fakePlayerClass;
    protected final Map<UUID, FakeWorldPlayer> playerMap;
    protected final Map<BlockPosition, FakeBlock> fakeBlocks;
    protected final Map<ChunkCoord, Set<BlockPosition>> fakeChunks;

    protected FakeWorld(World world, Class<? extends FakeWorldPlayer> fakePlayerClass) {
        this.world = world;
        this.fakePlayerClass = fakePlayerClass;
        playerMap = new HashMap<>();
        fakeBlocks = new HashMap<>();
        fakeChunks = new HashMap<>();
    }

    /**
     * Clears all blocks from the FakeWorld without removing players
     */
    public final void clear() {
        for (FakeBlock fb : fakeBlocks.values()) {
            setBlock(fb.getBlockPosition(), Material.AIR.createBlockData(), true);
        }
        updateAll();
        fakeBlocks.clear();
        fakeChunks.clear();
    }

    /**
     * Stops tasks and packet adapters and removes players from the world
     */
    public void destroy() {
        Iterator<FakeWorldPlayer> fwpit = playerMap.values().iterator();
        while (fwpit.hasNext()) {
            FakeWorldPlayer fwp = fwpit.next();
            clearPlayer(fwp.getCorePlayer());
            fwp.getCorePlayer().leaveFakeWorld(this);
            fwpit.remove();
        }
        fakeBlocks.clear();
        fakeChunks.clear();
        playerMap.clear();
    }

    public final Map<UUID, FakeWorldPlayer> getPlayerMap() {
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
     * On player item use
     *
     * @param cp Core Player
     * @param blockPosition Click Block
     * @param blockRelative Placed Block
     * @return Cancel Event
     */
    protected abstract boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative);

    /**
     * Hide/show players whether they're in this fake world or not
     *
     * @param cp Core Player
     */
    protected final void applyVisibility(CorePlayer cp) {
        // Hide and hide from all players not in this GameWorld
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
     * Add a player to the fake world
     *
     * @param cp Core Player
     */
    public void addPlayer(CorePlayer cp) {
        cp.joinFakeWorld(this);
        try {
            playerMap.put(cp.getUniqueId(), fakePlayerClass.getConstructor(CorePlayer.class).newInstance(cp));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        // TODO: Do we need this line?
        //dbp.getPlayer().setCollidable(false);
        applyVisibility(cp);
        updateAll();
    }

    /**
     * Clear all blocks from the player and send them back to
     * the real world
     *
     * @param cp Core Player
     */
    public void clearPlayer(CorePlayer cp) {
        WrappedBlockData wbd;
        for (Map.Entry<BlockPosition, FakeBlock> fb : fakeBlocks.entrySet()) {
            boolean success = false;
            for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                if (fakeWorld.updateBlock(fb.getKey(), cp)) {
                    success = true;
                }
            }
            if (!success) {
                wbd = WrappedBlockData.createData(world.getBlockAt(new Location(world, fb.getKey().getX(), fb.getKey().getY(), fb.getKey().getZ())).getType());
                PacketContainer fakeBlockPacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
                fakeBlockPacket.getBlockPositionModifier().write(0, fb.getKey());
                fakeBlockPacket.getBlockData().write(0, wbd);
                try {
                    Core.getProtocolManager().sendServerPacket(cp.getPlayer(), fakeBlockPacket);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(GameWorld.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        Core.getInstance().returnToWorld(cp);
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
     * Sets a fake block status in the world
     *
     * @param pos Block Position
     * @param blockData Block Data
     */
    public void setBlock(BlockPosition pos, BlockData blockData) {
        setBlock(pos, blockData, false);
    }

    /**
     * Sets a fake block status in the world with the option to
     * instantly update and display or not
     *
     * @param pos Block Position
     * @param blockData Block Data
     * @param ignoreUpdate Should Send Packet
     */
    public void setBlock(BlockPosition pos, BlockData blockData, boolean ignoreUpdate) {
        FakeBlock fb = new FakeBlock(pos, blockData);
        fakeBlocks.put(pos, fb);
        ChunkCoord coord = fb.getChunkCoord();
        if (!fakeChunks.containsKey(coord)) {
            fakeChunks.put(coord, new HashSet<>());
        }
        fakeChunks.get(coord).add(pos);
        if (!ignoreUpdate) updateBlock(pos);
    }

    /**
     * Fill fake blocks with a set of fake blocks
     * and push an updateAll call
     *
     * @param blocks Fake Blocks
     */
    public void setBlocks(Set<FakeBlock> blocks) {
        ChunkCoord coord;
        for (FakeBlock fb : blocks) {
            fakeBlocks.put(fb.getBlockPosition(), fb);
            coord = fb.getChunkCoord();
            if (!fakeChunks.containsKey(coord))
                fakeChunks.put(coord, new HashSet<>());
            fakeChunks.get(coord).add(fb.getBlockPosition());
        }
        updateAll();
    }

    /**
     * Break blocks in a radius
     *
     * @param pos Origin
     * @param radius Radius
     * @return Number of Successes
     */
    public int breakBlocks(BlockPosition pos, int radius) {
        double dx, dy, dz;
        int broken = 0;
        for (int x = -radius; x <= radius; x++) {
            dx = ((double)x) / radius;
            for (int y = -radius; y <= radius; y++) {
                dy = ((double)y) / radius;
                for (int z = -radius; z <= radius; z++) {
                    dz = ((double)z) / radius;
                    if (Math.sqrt(dx*dx + dy*dy + dz*dz) < 1) {
                        broken += breakBlock(pos.add(new BlockPosition(x, y, z)), null) ? 1 : 0;
                    }
                }
            }
        }
        return broken;
    }

    /**
     * Breaks a block and plays breaking block sound
     *
     * @param pos Block Position
     * @return Success
     */
    public boolean breakBlock(BlockPosition pos, CorePlayer cp) {
        if (fakeBlocks.containsKey(pos) && !fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.AIR)) {
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(world).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, fakeBlocks.get(pos).getBlockData());
                if (cp != null && !cp.equals(fwp.getCorePlayer()))
                    fwp.getPlayer().playSound(pos.toLocation(world), fakeBlocks.get(pos).getBreakSound(), 1, 1);
            });
            setBlock(pos, Material.AIR.createBlockData(), false);
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
            setBlock(pos, material.createBlockData(), false);
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
     */
    public boolean updateBlock(BlockPosition pos) {
        WrappedBlockData wbd;
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null) {
            wbd = WrappedBlockData.createData(fb.getBlockData());
            PacketContainer fakeBlockPacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            fakeBlockPacket.getBlockPositionModifier().write(0, pos);
            fakeBlockPacket.getBlockData().write(0, wbd);
            for (FakeWorldPlayer fwp : getPlayerMap().values()) {
                try {
                    Core.getProtocolManager().sendServerPacket(fwp.getPlayer(), fakeBlockPacket);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(GameWorld.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return true;
        }
        return false;
    }
    
    /**
     * Send a fake block packet to a players
     *
     * @param pos Block Position
     */
    public boolean updateBlock(BlockPosition pos, CorePlayer cp) {
        WrappedBlockData wbd;
        FakeBlock fb = fakeBlocks.get(pos);
        if (fb != null) {
            wbd = WrappedBlockData.createData(fb.getBlockData());
            PacketContainer fakeBlockPacket = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
            fakeBlockPacket.getBlockPositionModifier().write(0, pos);
            fakeBlockPacket.getBlockData().write(0, wbd);
            try {
                Core.getProtocolManager().sendServerPacket(cp.getPlayer(), fakeBlockPacket);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(GameWorld.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return false;
    }

    /**
     * Sends a fake block packet of all blocks in the fakeblock
     * map to every player in the fake world
     */
    public void updateAll() {
        for (Map.Entry<BlockPosition, FakeBlock> fb : fakeBlocks.entrySet()) {
            updateBlock(fb.getKey());
        }
    }

}
