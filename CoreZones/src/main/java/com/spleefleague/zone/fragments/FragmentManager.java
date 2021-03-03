package com.spleefleague.zone.fragments;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.core.world.FakeBlock;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.player.ZonePlayer;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.*;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class FragmentManager {

    private final Map<ChunkCoord, Set<FragmentContainer>> chunkListMap = new HashMap<>();

    private final Map<String, FragmentContainer> fragmentContainerMap = new HashMap<>();

    private MongoCollection<Document> fragmentColl;

    private BukkitTask autoSaveTask;

    public void init() {
        fragmentColl = CoreZones.getInstance().getPluginDB().getCollection("Fragments");

        for (Document doc : fragmentColl.find()) {
            FragmentContainer fragment = new FragmentContainer();
            fragment.load(doc);
            fragmentContainerMap.put(fragment.getIdentifier(), fragment);
            for (ChunkCoord chunkCoord : fragment.getUsedChunks()) {
                if (!chunkListMap.containsKey(chunkCoord)) {
                    chunkListMap.put(chunkCoord, new HashSet<>());
                }
                chunkListMap.get(chunkCoord).add(fragment);
            }
        }

        autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreZones.getInstance(), this::autosave, 40L, 40L);

        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer mapChunkPacket = event.getPacket();
                ChunkCoord chunkCoord = new ChunkCoord(mapChunkPacket.getIntegers().read(0), mapChunkPacket.getIntegers().read(1));
                loadedChunks.get(event.getPlayer().getUniqueId()).add(chunkCoord);
                if (!chunkListMap.containsKey(chunkCoord)) return;
                ZonePlayer zp = CoreZones.getInstance().getPlayers().get(event.getPlayer());
                if (zp == null) return;
                Map<String, Set<Long>> collectedMap = zp.getFragments().getCollected();
                for (FragmentContainer container : chunkListMap.get(chunkCoord)) {
                    Set<Long> collected = collectedMap.getOrDefault(container.getIdentifier(), EMPTY_COLLECTED);
                    container.setLoaded(collected, event.getPlayer(), chunkCoord);
                }
            }
        });
        Core.addProtocolPacketAdapter(new PacketAdapter(Core.getInstance(), PacketType.Play.Server.UNLOAD_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer unloadChunkPacket = event.getPacket();
                ChunkCoord chunkCoord = new ChunkCoord(unloadChunkPacket.getIntegers().read(0), unloadChunkPacket.getIntegers().read(1));
                loadedChunks.get(event.getPlayer().getUniqueId()).remove(chunkCoord);
                if (!chunkListMap.containsKey(chunkCoord)) return;
                for (FragmentContainer container : chunkListMap.get(chunkCoord)) {
                    container.setUnloaded(event.getPlayer(), chunkCoord);
                }
            }
        });
    }

    private final Map<UUID, Set<ChunkCoord>> loadedChunks = new HashMap<>();

    private static final Set<Long> EMPTY_COLLECTED = new HashSet<>();

    public void close() {
        autoSaveTask.cancel();
    }

    public FragmentContainer getContainer(String fragment) {
        return fragmentContainerMap.get(fragment);
    }

    public Collection<FragmentContainer> getAll() {
        return fragmentContainerMap.values();
    }

    public void onPlayerJoin(UUID uuid) {
        loadedChunks.put(uuid, new HashSet<>());
        for (FragmentContainer container : fragmentContainerMap.values()) {
            container.onPlayerJoin(uuid);
        }
    }

    public void onPlayerLoaded(ZonePlayer zonePlayer) {
        synchronized (loadedChunks) {
            if (loadedChunks.containsKey(zonePlayer.getUniqueId())) {
                Player player = zonePlayer.getPlayer();
                for (ChunkCoord chunkCoord : loadedChunks.get(zonePlayer.getUniqueId())) {
                    if (chunkListMap.containsKey(chunkCoord)) {
                        Map<String, Set<Long>> collectedMap = zonePlayer.getFragments().getCollected();
                        for (FragmentContainer container : chunkListMap.get(chunkCoord)) {
                            Set<Long> collected = collectedMap.getOrDefault(container.getIdentifier(), EMPTY_COLLECTED);
                            container.setLoaded(collected, player, chunkCoord);
                        }
                    }
                }
            }
        }
    }

    public void onPlayerQuit(UUID uuid) {
        loadedChunks.put(uuid, new HashSet<>());
        for (FragmentContainer container : fragmentContainerMap.values()) {
            container.onPlayerQuit(uuid);
        }
    }

    public void setItem(String fragment, int uncollected, int collected) {
        fragmentContainerMap.get(fragment).setItemCmds(uncollected, collected);
    }

    public void setMenuItem(String fragment, int menuCmd) {
        fragmentContainerMap.get(fragment).setMenuCmd(menuCmd);
    }

    public void setPickupSound(String fragment, Sound sound) {
        fragmentContainerMap.get(fragment).setPickupSound(sound);
    }

    public void setDisplayName(String fragment, String displayName) {
        fragmentContainerMap.get(fragment).setDisplayName(displayName);
    }

    public void setDescription(String fragment, String description) {
        fragmentContainerMap.get(fragment).setDescription(description);
    }

    private void autosave() {
        for (FragmentContainer container : fragmentContainerMap.values()) {
            if (container.onSave()) {
                Bukkit.getScheduler().runTask(CoreZones.getInstance(), () -> container.save(fragmentColl));
            }
        }
    }

    public Set<String> getFragmentNames() {
        return fragmentContainerMap.keySet();
    }

    private static final InventoryMenuItem FRAGMENT_WAND = InventoryMenuAPI.createItemStatic()
            .setName("Fragment Wand")
            .setDisplayItem(Material.GOLDEN_AXE)
            .setDescription("Left click: unset zone\nRight click: set zone");

    public ItemStack getFragmentWand() {
        return FRAGMENT_WAND.createItem(null);
    }

    public boolean create(String identifier) {
        if (fragmentContainerMap.containsKey(identifier)) {
            return false;
        }
        FragmentContainer fragment = new FragmentContainer(identifier);
        fragmentContainerMap.put(identifier, fragment);
        fragment.save(fragmentColl);
        return true;
    }

    public void onPlayerRightClick(CorePlayer cp, Point point, org.bukkit.util.Vector direction) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(cp.getUniqueId());
        FragmentContainer fragment = fragmentContainerMap.get(editor.getTargetFragment());
        if (fragment == null) {
            CoreZones.getInstance().sendMessage(cp, "No target fragment set! Use /f b f <zoneName>");
            return;
        }

        BlockRaycastResult result = null;
        World world = cp.getPlayer().getWorld();
        for (BlockRaycastResult blockResult : point.castBlocks(direction, 32)) {
            if (!blockResult.getBlockPos().toLocation(world).getBlock().isPassable()) {
                result = blockResult;
                break;
            }
        }
        if (result == null) return;

        BlockPosition original = result.getRelative();

        int chunkX = original.getX() >> 4;
        int chunkZ = original.getZ() >> 4;

        short pos = (short) (((short) original.getY() << 8) +
                ((short) (original.getZ() & 0xF) << 4) +
                ((short) (original.getX() & 0xF)));

        ChunkCoord chunkCoord = new ChunkCoord(chunkX, chunkZ);
        if (fragment.addFragment(chunkCoord, pos)) {
            if (!chunkListMap.containsKey(chunkCoord)) {
                chunkListMap.put(chunkCoord, new HashSet<>());
            }
            chunkListMap.get(chunkCoord).add(fragment);
            for (Map.Entry<UUID, Set<ChunkCoord>> entry : loadedChunks.entrySet()) {
                if (entry.getValue().contains(chunkCoord)) {
                    fragment.setLoaded(EMPTY_COLLECTED, Bukkit.getPlayer(entry.getKey()), chunkCoord);
                }
            }
        }
    }

    public void onPlayerLeftClick(CorePlayer cp, Point point, org.bukkit.util.Vector direction) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(cp.getUniqueId());
        FragmentContainer fragment = fragmentContainerMap.get(editor.getTargetFragment());
        if (fragment == null) {
            CoreZones.getInstance().sendMessage(cp, "No target fragment set! Use /f b f <zoneName>");
            return;
        }

        for (BlockRaycastResult blockResult : point.castBlocks(direction, 32)) {
            BlockPosition blockPos = blockResult.getBlockPos();
            short pos = (short) (((short) blockPos.getY() << 8) + ((short) (blockPos.getZ() & 0xF) << 4) + ((short) (blockPos.getX() & 0xF)));
            if (fragment.removeFragment(new ChunkCoord(blockPos.getX() >> 4, blockPos.getZ() >> 4), pos)) {
                break;
            }
        }
    }

    private void checkFragmentBlock(ZonePlayer zp, BlockPosition pos) {
        if (pos.getY() < 0 || pos.getY() > 255) {
            zp.setZone(null);
            return;
        }

        short relPos = (short) (((short) pos.getY() << 8) + ((short) (pos.getZ() & 0xF) << 4) + ((short) (pos.getX() & 0xF)));
        ChunkCoord chunkCoord = new ChunkCoord(pos.getX() >> 4, pos.getZ() >> 4);

        if (chunkListMap.containsKey(chunkCoord)) {
            for (FragmentContainer fragment : chunkListMap.get(chunkCoord)) {
                fragment.checkContained(zp, chunkCoord, relPos);
            }
        }
    }

    public void onPlayerMove(Player player, Location loc) {
        if (!player.getGameMode().equals(GameMode.ADVENTURE)) return;
        ZonePlayer zp = CoreZones.getInstance().getPlayers().get(player.getUniqueId());

        for (BlockPosition pos : CoreUtils.getInsideBlocks(player.getBoundingBox())) {
            checkFragmentBlock(zp, pos);
        }
    }

}
