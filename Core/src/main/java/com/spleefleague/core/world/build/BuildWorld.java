package com.spleefleague.core.world.build;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.logger.CoreLogger;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.Dimension;
import com.spleefleague.core.util.variable.Position;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.FakeUtils;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.tool.SelectTool;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * When a build world is created it is assigned an owner, and the
 * build world persists until the owner leaves
 *
 * @author NickM13
 * @since 4/16/2020
 */
public class BuildWorld extends FakeWorld<BuildWorldPlayer> {

    private static final Set<BuildWorld> BUILD_WORLDS = new HashSet<>();
    private static final Map<UUID, BuildWorld> PLAYER_BUILD_WORLDS = new HashMap<>();

    /**
     * Initialize structures and build tools
     */
    public static void init() {
        BuildStructures.init();
        //SelectTool.init();
    }

    public static boolean createBuildWorld(CorePlayer corePlayer, BuildStructure structure) {
        BuildWorld buildWorld = new BuildWorld(corePlayer.getLocation().getWorld(), corePlayer, structure);
        if (buildWorld.didLoadFail()) {
            return false;
        }
        BUILD_WORLDS.add(buildWorld);
        return true;
    }

    /**
     * Get the build world that a CorePlayer is a part of
     *
     * @param cp Core Player
     * @return Build World
     */
    public static BuildWorld getPlayerBuildWorld(CorePlayer cp) {
        return PLAYER_BUILD_WORLDS.get(cp.getUniqueId());
    }

    /**
     * Returns whether a player is in a build world
     *
     * @param player Player
     * @return Is Builder
     */
    public static boolean isBuilder(Player player) {
        return PLAYER_BUILD_WORLDS.containsKey(player.getUniqueId());
    }

    /**
     * Close all build worlds
     */
    public static void close() {
        BUILD_WORLDS.forEach(BuildWorld::closeBuildWorld);
    }

    /**
     * Remove all players from a build world and destroy it
     *
     * @param buildWorld Build World
     */
    public static void closeBuildWorld(BuildWorld buildWorld) {
        buildWorld.getPlayerMap().keySet().forEach(PLAYER_BUILD_WORLDS::remove);
        buildWorld.destroy();
    }

    public static boolean removePlayerGlobal(CorePlayer cp) {
        if (PLAYER_BUILD_WORLDS.containsKey(cp.getUniqueId())) {
            BuildWorld buildWorld = PLAYER_BUILD_WORLDS.get(cp.getUniqueId());
            PLAYER_BUILD_WORLDS.remove(cp.getUniqueId());
            buildWorld.removePlayer(cp);
            return true;
        }
        return false;
    }

    private final CorePlayer owner;
    private final BuildStructure structure;
    private BlockPosition origin;
    private BlockPosition lowest, highest;
    private boolean loadFail = false;

    private BuildWorld(World world, CorePlayer owner, BuildStructure structure) {
        super(2, world, BuildWorldPlayer.class);
        this.owner = owner;
        this.origin = new BlockPosition(
                owner.getLocation().getBlockX(),
                owner.getLocation().getBlockY(),
                owner.getLocation().getBlockZ());
        this.structure = structure;
        this.structure.setUnderConstruction(this);
        for (Map.Entry<BlockPosition, FakeBlock> entry : structure.getFakeBlocks().entrySet()) {
            if (!setBlock(entry.getKey().add(origin), entry.getValue().getBlockData())) {
                BlockPosition fail = entry.getKey().add(origin);
                CoreLogger.logError("Build World failed at " + fail.toString());
                loadFail = true;
                break;
            }
        }
        if (!loadFail && lowest != null) {
            if (lowest.getY() < 0) {
                loadFail = true;
                CoreLogger.logError("Build World due to going below world (" + (lowest.getY()) + ")");
            }
            if (highest.getY() > 255) {
                loadFail = true;
                CoreLogger.logError("Build World due to exceeding world height (" + (highest.getY()) + ")");
            }
        }
        if (!loadFail) {
            addPlayer(owner);
        } else {
            this.structure.setUnderConstruction(null);
        }
    }

    public boolean didLoadFail() {
        return loadFail;
    }

    @Override
    public void destroy() {
        saveToStructure();
        structure.setUnderConstruction(null);
        super.destroy();
        BUILD_WORLDS.remove(this);
    }

    public final CorePlayer getOwner() {
        return owner;
    }

    /**
     * Check block break event
     *
     * @param cp  CorePlayer
     * @param pos Block Position
     * @return Cancel Event
     */
    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        return breakBlock(pos, cp);
    }

    /**
     * Check item usage event (both place block and right click from distance)
     *
     * @param cp CorePlayer
     * @return Cancel Event
     */
    @Override
    protected boolean onItemUse(CorePlayer cp, BlockPosition blockPosition, BlockPosition blockRelative) {
        ItemStack heldItem = cp.getPlayer().getInventory().getItemInMainHand();
        if (heldItem.getType().isBlock()) {
            if (cp.getPlayer().isSneaking()) {

            }
            placeBlock(blockRelative, heldItem.getType(), cp);
        } else {
            cp.getPlayer().sendBlockChange(blockPosition.toLocation(getWorld()),
                    blockPosition.toLocation(getWorld()).getBlock().getBlockData());
        }
        return true;
    }

    /**
     * Add Player to build world and map their uuid
     *
     * @param cp Core Player
     */
    @Override
    public void addPlayer(CorePlayer cp) {
        super.addPlayer(cp);
        cp.setGameMode(GameMode.CREATIVE);
        cp.getPlayer().getInventory().clear();
        cp.refreshHotbar();
        PLAYER_BUILD_WORLDS.put(cp.getUniqueId(), this);

        if (!fakeBlocks.isEmpty()) {
            cp.getPlayer().teleport(origin.toLocation(getWorld()).add(0.5D, 0D, 0.5D));
        }
    }

    /**
     * Remove a player from the build world, if it's the owner of the
     * build world then destroy the build world
     *
     * @param cp Core Player
     * @return Removed
     */
    @Override
    public boolean removePlayer(CorePlayer cp) {
        if (cp.equals(owner)) {
            closeBuildWorld(this);
            return true;
        }
        return super.removePlayer(cp);
    }

    public boolean setBlock(BlockPosition pos, BlockData blockData) {
        if (fakeBlocks.isEmpty()) {
            lowest = pos;
            highest = pos;
        } else {
            lowest = new BlockPosition(
                    Math.min(pos.getX(), lowest.getX()),
                    Math.min(pos.getY(), lowest.getY()),
                    Math.min(pos.getZ(), lowest.getZ()));
            highest = new BlockPosition(
                    Math.max(pos.getX(), highest.getX()),
                    Math.max(pos.getY(), highest.getY()),
                    Math.max(pos.getZ(), highest.getZ()));
        }
        return super.setBlock(pos, blockData);
    }

    public void saveToStructure() {
        structure.getFakeBlocks().clear();
        for (Map.Entry<BlockPosition, FakeBlock> entry : fakeBlocks.entrySet()) {
            structure.setBlock(entry.getKey().subtract(origin), entry.getValue());
        }
        BuildStructures.save(structure);
    }

    public BuildStructure getStructure() {
        return structure;
    }

    public void setOrigin(BlockPosition origin) {
        BlockPosition shift = this.origin.subtract(origin);
        this.origin = origin;
        structure.shiftOrigin(shift);
    }

    public void shift(BlockPosition shift) {
        Map<BlockPosition, FakeBlock> shiftedBlocks = FakeUtils.translateBlocks(new HashMap<>(fakeBlocks), shift);
        overwriteBlocks(shiftedBlocks);
    }

    public void fill(Dimension fillBox, Material material) {
        FakeBlock fb = new FakeBlock(material.createBlockData());
        Map<BlockPosition, FakeBlock> fillBlocks = new HashMap<>();
        for (int x = (int) fillBox.getLow().x; x <= fillBox.getHigh().x; x++) {
            for (int y = (int) fillBox.getLow().y; y <= fillBox.getHigh().y; y++) {
                for (int z = (int) fillBox.getLow().z; z <= fillBox.getHigh().z; z++) {
                    fillBlocks.put(new BlockPosition(x, y, z), fb);
                }
            }
        }
        setBlocks(fillBlocks);
    }

    /**
     * Transfer the selected blocks to the build world
     *
     * @param fillBox Dimension
     */
    public void buildify(Dimension fillBox) {
        Map<BlockPosition, FakeBlock> fillBlocks = new HashMap<>();
        for (int x = (int) fillBox.getLow().x; x <= fillBox.getHigh().x; x++) {
            for (int y = (int) fillBox.getLow().y; y <= fillBox.getHigh().y; y++) {
                for (int z = (int) fillBox.getLow().z; z <= fillBox.getHigh().z; z++) {
                    fillBlocks.put(new BlockPosition(x, y, z), new FakeBlock(getWorld().getBlockAt(x, y, z).getBlockData()));
                    getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
        setBlocks(fillBlocks);
    }

    /**
     * Transfer the selected blocks to the real world
     *
     * @param fillBox Dimension
     */
    public void worldify(Dimension fillBox) {
        FakeBlock fb = new FakeBlock(Material.AIR.createBlockData());
        Map<BlockPosition, FakeBlock> fillBlocks = new HashMap<>();
        for (int x = (int) fillBox.getLow().x; x <= fillBox.getHigh().x; x++) {
            for (int y = (int) fillBox.getLow().y; y <= fillBox.getHigh().y; y++) {
                for (int z = (int) fillBox.getLow().z; z <= fillBox.getHigh().z; z++) {
                    FakeBlock fb2 = fakeBlocks.get(new BlockPosition(x, y, z));
                    if (fb2 != null) {
                        getWorld().getBlockAt(x, y, z).setBlockData(fb2.getBlockData());
                        fillBlocks.put(new BlockPosition(x, y, z), fb);
                    }
                }
            }
        }
        setBlocks(fillBlocks);
    }

}
