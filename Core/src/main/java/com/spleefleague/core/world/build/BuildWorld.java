package com.spleefleague.core.world.build;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeWorld;
import com.spleefleague.core.world.build.tool.BreakTool;
import com.spleefleague.core.world.build.tool.PlaceTool;
import com.spleefleague.core.world.build.tool.SelectTool;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
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
public class BuildWorld extends FakeWorld {
    
    private static final Set<BuildWorld> BUILD_WORLDS = new HashSet<>();
    private static final Map<UUID, BuildWorld> PLAYER_BUILD_WORLDS = new HashMap<>();
    private static final Set<BuildTool> BUILD_TOOLS = new HashSet<>();
    
    /**
     * Initialize build tools
     */
    public static void init() {
        BUILD_TOOLS.add(new BreakTool());
        BUILD_TOOLS.add(new PlaceTool());
        BUILD_TOOLS.add(new SelectTool());
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
    
    public static void removePlayerGlobal(CorePlayer cp) {
        BuildWorld buildWorld = PLAYER_BUILD_WORLDS.get(cp);
        buildWorld.removePlayer(cp);
    }
    
    private final Set<Material> buildMaterials;
    private final CorePlayer owner;

    public BuildWorld(World world, CorePlayer owner, Set<Material> buildMaterials) {
        super(world, BuildWorldPlayer.class);
        initBuildTools();
        this.buildMaterials = buildMaterials;
        this.owner = owner;
        addPlayer(owner);
        BUILD_WORLDS.add(this);
    }
    
    private void initBuildTools() {
    }
    
    public final CorePlayer getOwner() {
        return owner;
    }
    
    public final Set<Material> getBuildMaterials() { return buildMaterials; }
    
    /**
     * Check block break event
     *
     * @param cp CorePlayer
     * @param pos Block Position
     * @return Cancel Event
     */
    @Override
    protected boolean onBlockPunch(CorePlayer cp, BlockPosition pos) {
        return breakBlock(pos);
    }
    
    /**
     * Check item usage event (both place block and right click from distance)
     *
     * @param cp CorePlayer
     * @return Cancel Event
     */
    @Override
    protected boolean onItemUse(CorePlayer cp) {
        ItemStack heldItem = cp.getPlayer().getInventory().getItemInMainHand();
        String hotbarTag = InventoryMenuAPI.getHotbarTag(heldItem);
        for (BuildTool builderTool : BUILD_TOOLS) {
            if (builderTool.getHotbarItem().getHotbarIdentifier().equals(hotbarTag)) {
                builderTool.use(cp, this);
            }
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
        cp.setGameMode(GameMode.ADVENTURE);
        cp.getPlayer().setAllowFlight(true);
        cp.getPlayer().getInventory().clear();
        ((BuildWorldPlayer) getPlayerMap().get(cp.getUniqueId())).setSelectedMaterial(buildMaterials.iterator().next());
        BUILD_TOOLS.forEach(tool -> {
            cp.getPlayer().getInventory().setItem(tool.getHotbarItem().getSlot(), tool.getHotbarItem().createItem(cp));
        });
        PLAYER_BUILD_WORLDS.put(cp.getUniqueId(), this);
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
        if (super.removePlayer(cp)) {
            if (cp.equals(owner)) {
                destroy();
            }
            return true;
        }
        return false;
    }

}
