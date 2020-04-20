package com.spleefleague.core.world;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.game.GameWorld;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.PacketPlayInUseItem;
import net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk;
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
    
    private final static List<PacketAdapter> packetAdapters = new ArrayList<>();

    public static void init() {
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                BlockPosition pos = event.getPacket().getBlockPositionModifier().read(0);
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    event.setCancelled(fakeWorld.onBlockPunch(cp, pos));
                }
            }
        });
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                System.out.println("Block Place Event");
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    event.setCancelled(true);
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
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    if (fakeWorld.onItemUse(cp, blockPosition)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        });
        addPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                CorePlayer cp = Core.getInstance().getPlayers().get(event.getPlayer());
                ChunkCoord chunkCoord = new ChunkCoord(event.getPacket().getIntegers().read(0), event.getPacket().getIntegers().read(1));
                Map<BlockPosition, FakeBlock> fakeBlocks = new HashMap<>();
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    Set<BlockPosition> fakeChunk = fakeWorld.getFakeChunk(chunkCoord);
                    if (fakeChunk != null) {
                        for (BlockPosition blockPosition : fakeChunk) {
                            FakeBlock fakeBlock = fakeWorld.getFakeBlocks().get(blockPosition);
                            if (!fakeBlocks.containsKey(blockPosition) && !fakeBlock.getBlockData().getMaterial().isAir()) {
                                fakeBlocks.put(blockPosition, fakeBlock);
                            }
                        }
                    }
                }
                if (fakeBlocks.size() > 0) {
                    for (Map.Entry<BlockPosition, FakeBlock> fakeBlock : fakeBlocks.entrySet()) {
                        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.BLOCK_CHANGE);
                        packetContainer.getBlockPositionModifier().write(0, fakeBlock.getKey());
                        packetContainer.getBlockData().write(0, WrappedBlockData.createData(fakeBlock.getValue().getBlockData().getMaterial()));
                        Core.sendPacket(cp, packetContainer);
                    }
                    /*
                    MultiBlockChangeInfo[] mbci = new MultiBlockChangeInfo[fakeBlocks.size()];
                    int i = 0;
                    for (FakeBlock fakeBlock : fakeBlocks.values()) {
                        short location = (short) (((((fakeBlock.getBlockPosition().getX() & 15) << 4) + (fakeBlock.getBlockPosition().getZ() & 15)) << 8) + fakeBlock.getBlockPosition().getY());
                        mbci[i] = new MultiBlockChangeInfo(location,
                                WrappedBlockData.createData(fakeBlock.getBlockData().getMaterial()),
                                chunkCoord.toChunkCoordIntPair());
                        i++;
                    }
                    PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
                    packetContainer.getChunkCoordIntPairs().write(0, chunkCoord.toChunkCoordIntPair());
                    packetContainer.getMultiBlockChangeInfoArrays().write(0, mbci);
                    System.out.println(packetContainer);
                    Bukkit.getScheduler().runTaskLater(Core.getInstance(), () -> Core.sendPacket(cp, packetContainer), 60L);
                    */
                }
                /*
                net.minecraft.server.v1_15_R1.Chunk chunk = (net.minecraft.server.v1_15_R1.Chunk) event.getPlayer().getWorld().getChunkAt(new Location(event.getPlayer().getWorld(), chunkCoord.x * 16, 0, chunkCoord.z * 16));
                Map<BlockPosition, FakeBlock> fakeBlocks = new HashMap<>();
                int totalBlocks = event.getPacket().getIntegers().read(3);
                for (FakeWorld fakeWorld : cp.getFakeWorlds()) {
                    Set<BlockPosition> fakeChunk = fakeWorld.getFakeChunk(chunkCoord);
                    if (fakeChunk != null) {
                        for (BlockPosition blockPosition : fakeChunk) {
                            FakeBlock fakeBlock = fakeWorld.getFakeBlocks().get(blockPosition);
                            if (!fakeBlock.getBlockData().getMaterial().isAir()) {
                                if (!fakeBlocks.containsKey(blockPosition)
                                        && blockPosition.toLocation(cp.getPlayer().getWorld()).getBlock().isEmpty()) {
                                    totalBlocks++;
                                }
                                fakeBlocks.put(blockPosition, fakeBlock);
                            }
                        }
                    }
                }
                if (fakeBlocks.size() > 0) {
                    PacketPlayOutMapChunk packetPlayOutMapChunk = new PacketPlayOutMapChunk(chunk, totalBlocks);
                    event.setPacket(new PacketContainer(packetPlayOutMapChunk));
                }
                */
            }
        });
    }
    
    public static void close() {
        packetAdapters.forEach(Core.getProtocolManager()::removePacketListener);
    }
    
    protected static final void addPacketAdapter(PacketAdapter packetAdapter) {
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
    protected abstract boolean onItemUse(CorePlayer cp, BlockPosition pos);

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
        fakeBlocks.put(pos, new FakeBlock(pos, blockData));
        ChunkCoord coord = new ChunkCoord(pos.getX() / 16, pos.getZ() / 16);
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
    public int breakBlock(BlockPosition pos, int radius) {
        double dx, dy, dz;
        int broken = 0;
        for (int x = -radius; x <= radius; x++) {
            dx = ((double)x) / radius;
            for (int y = -radius; y <= radius; y++) {
                dy = ((double)y) / radius;
                for (int z = -radius; z <= radius; z++) {
                    dz = ((double)z) / radius;
                    if (Math.sqrt(dx*dx + dy*dy + dz*dz) < 1) {
                        broken += breakBlock(pos.add(new BlockPosition(x, y, z))) ? 1 : 0;
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
    public boolean breakBlock(BlockPosition pos) {
        if (fakeBlocks.containsKey(pos) && !fakeBlocks.get(pos).getBlockData().getMaterial().equals(Material.AIR)) {
            getPlayerMap().values().forEach(fwp -> {
                fwp.getPlayer().spawnParticle(Particle.BLOCK_DUST, pos.toLocation(world).add(0.5, 0.5, 0.5), 20, 0.25, 0.25, 0.25, fakeBlocks.get(pos).getBlockData());
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
     * Send a fake block packet to all players
     *
     * @param pos Block Position
     */
    public void updateBlock(BlockPosition pos) {
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
        }
    }
    
    /**
     * Updates all of the fakeblocks in the chunk
     *
     * @param chunkCoord Chunk Coordinates
     */
    /*
    public void loadChunk(ChunkCoord chunkCoord, CorePlayer cp) {
        if (!fakeChunks.containsKey(chunkCoord)) return;
        Set<BlockPosition> blockPositions = fakeChunks.get(chunkCoord);
        
        PacketContainer multiBlockPacket = new PacketContainer(PacketType.Play.Server.MULTI_BLOCK_CHANGE);
        multiBlockPacket.getChunkCoordIntPairs().write(0, chunkCoord.toChunkCoordIntPair());
        MultiBlockChangeInfo[] mbci = new MultiBlockChangeInfo[blockPositions.size()];
        int i = 0;
        for (BlockPosition blockPos : blockPositions) {
            mbci[i] = new MultiBlockChangeInfo(blockPos.toLocation(getWorld()), WrappedBlockData.createData(fakeBlocks.get(blockPos).getBlockData().getMaterial()));
            i++;
        }
        multiBlockPacket.getMultiBlockChangeInfoArrays().write(0, mbci);
        Bukkit.getServer().getScheduler().runTaskLater(Core.getInstance(), () ->
        Core.sendPacket(cp, multiBlockPacket), 5L);
    }
    */

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
