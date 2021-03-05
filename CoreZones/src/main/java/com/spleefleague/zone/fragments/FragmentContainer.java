package com.spleefleague.zone.fragments;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.util.concurrent.AtomicDouble;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.MathUtils;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.core.world.global.GlobalWorld;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.player.ZonePlayer;
import com.spleefleague.zone.player.fragments.PlayerFragments;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class FragmentContainer extends DBEntity {

    private final Map<ChunkCoord, FragmentChunk> fragmentChunkMap = new HashMap<>();

    private boolean modified = false;

    @DBField private Integer uncollectedCmd = 1;
    @DBField private Integer collectedCmd = 2;
    @DBField private Integer menuCmd = 15;
    @DBField private Sound pickupSound = Sound.BLOCK_NOTE_BLOCK_BIT;
    @DBField private String displayName = "";
    @DBField private String description = "";

    private org.bukkit.inventory.ItemStack uncollectedItem;
    private org.bukkit.inventory.ItemStack collectedItem;
    private org.bukkit.inventory.ItemStack menuItem;

    private net.minecraft.server.v1_15_R1.ItemStack uncollectedItemNMS;
    private net.minecraft.server.v1_15_R1.ItemStack collectedItemNMS;

    private int total = 0;

    public FragmentContainer() {

    }

    public FragmentContainer(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void afterLoad() {
        initItems();
    }

    public int getTotal() {
        return total;
    }

    public int getStage(int deposited) {
        return (int) (Math.min(1, Math.max(0, (double) deposited / total)) * 5);
    }

    public void onPlayerJoin(UUID uuid) {
        loadedChunks.put(uuid, new HashSet<>());
    }

    public void onPlayerQuit(UUID uuid) {
        loadedChunks.remove(uuid);
    }

    public org.bukkit.inventory.ItemStack getUncollectedItem() {
        return uncollectedItem;
    }

    public ItemStack getUncollectedItemNMS() {
        return uncollectedItemNMS;
    }

    public org.bukkit.inventory.ItemStack getCollectedItem() {
        return collectedItem;
    }

    public ItemStack getCollectedItemNMS() {
        return collectedItemNMS;
    }

    public org.bukkit.inventory.ItemStack getMenuItem() {
        return menuItem;
    }

    private void initItems() {
        uncollectedItem = InventoryMenuUtils.createCustomItem(Material.HONEYCOMB, uncollectedCmd);
        uncollectedItemNMS = CraftItemStack.asNMSCopy(uncollectedItem);
        collectedItem = InventoryMenuUtils.createCustomItem(Material.HONEYCOMB, collectedCmd);
        collectedItemNMS = CraftItemStack.asNMSCopy(collectedItem);
        menuItem = InventoryMenuUtils.createCustomItem(Material.HONEYCOMB, menuCmd);
        for (FragmentChunk chunk : fragmentChunkMap.values()) {
            chunk.setItems(uncollectedItemNMS, collectedItemNMS);
        }
    }

    public void setItemCmds(int uncollectedCmd, int collectedCmd) {
        this.uncollectedCmd = uncollectedCmd;
        this.collectedCmd = collectedCmd;
        initItems();
        modified = true;
    }

    public void setMenuCmd(int menuCmd) {
        this.menuCmd = menuCmd;
        initItems();
        modified = true;
    }

    public void setPickupSound(Sound sound) {
        pickupSound = sound;
        modified = true;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        modified = true;
    }

    public void setDescription(String description) {
        this.description = description;
        modified = true;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @DBSave(fieldName = "data")
    protected List<Document> saveData() {
        List<Document> docList = new ArrayList<>();
        Iterator<Map.Entry<ChunkCoord, FragmentChunk>> it = fragmentChunkMap.entrySet().iterator();
        total = 0;
        while (it.hasNext()) {
            FragmentChunk chunk = it.next().getValue();
            if (chunk.isEmpty()) {
                it.remove();
                continue;
            }
            total += chunk.getFragments().size();
            docList.add(chunk.save());
        }
        return docList;
    }

    @DBLoad(fieldName = "data")
    protected void loadData(List<Document> docs) {
        total = 0;
        for (Document doc : docs) {
            FragmentChunk chunk = new FragmentChunk(doc);
            fragmentChunkMap.put(new ChunkCoord(chunk.chunkX, chunk.chunkZ), chunk);
            total += chunk.getFragments().size();
        }
    }

    public Set<ChunkCoord> getUsedChunks() {
        return fragmentChunkMap.keySet();
    }

    public boolean addFragment(ChunkCoord chunkCoord, short pos) {
        boolean chunkAdd = false;
        if (!fragmentChunkMap.containsKey(chunkCoord)) {
            FragmentChunk chunk = new FragmentChunk((short) chunkCoord.x, (short) chunkCoord.z);
            chunk.setItems(uncollectedItemNMS, collectedItemNMS);
            fragmentChunkMap.put(chunkCoord, chunk);
            chunkAdd = true;
        }
        if (fragmentChunkMap.get(chunkCoord).add(pos)) {
            modified = true;
            total++;
        }
        return chunkAdd;
    }

    public boolean removeFragment(ChunkCoord chunkCoord, short pos) {
        if (!fragmentChunkMap.containsKey(chunkCoord) || !fragmentChunkMap.get(chunkCoord).remove(pos)) return false;
        if (fragmentChunkMap.get(chunkCoord).isEmpty()) {
            fragmentChunkMap.remove(chunkCoord);
        }
        modified = true;
        total--;
        return true;
    }

    public void checkContained(ZonePlayer zp, ChunkCoord chunkCoord, short pos) {
        if (fragmentChunkMap.containsKey(chunkCoord)) {
            Fragment fragment = fragmentChunkMap.get(chunkCoord).checkContained(pos);
            if (fragment != null) {
                int collected = zp.getFragments().add(getIdentifier(), fragment.fullId);
                if (collected != -1) {
                    zp.getPlayer().playSound(zp.getPlayer().getLocation(), pickupSound, 1, zp.getPickupComboPitchAndIncrement());
                    Core.sendPacketSilently(zp.getPlayer(), new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY, new PacketPlayOutEntityDestroy(fragment.entityId)));
                }
            }
        }
    }

    public boolean onSave() {
        if (modified) {
            modified = false;
            return true;
        }
        return false;
    }

    private final Map<UUID, Set<ChunkCoord>> loadedChunks = new HashMap<>();

    public void sendAnimationPackets(byte rotation) {
        for (FragmentChunk chunk : fragmentChunkMap.values()) {
            chunk.sendUpdatePackets(rotation);
        }
    }

    public void setLoaded(Set<Long> collected, Player p, ChunkCoord chunkCoord) {
        if (fragmentChunkMap.containsKey(chunkCoord)) {
            fragmentChunkMap.get(chunkCoord).addViewer(collected, p);
        }
        if (!loadedChunks.containsKey(p.getUniqueId())) {
            loadedChunks.put(p.getUniqueId(), new HashSet<>());
        }
        loadedChunks.get(p.getUniqueId()).add(chunkCoord);
    }

    public void setUnloaded(Player p, ChunkCoord chunkCoord) {
        if (fragmentChunkMap.containsKey(chunkCoord)) {
            fragmentChunkMap.get(chunkCoord).removeViewer(p);
        }
        loadedChunks.get(p.getUniqueId()).remove(chunkCoord);
    }

    public InventoryMenuItem createMenu() {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemStatic()
                .setDisplayItem(uncollectedItem)
                .setName(displayName)
                .setDescription(description)
                .createLinkedContainer(displayName);

        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {

                });

        return menuItem;
    }

    public Vector getClosest(CorePlayer corePlayer) {
        List<FragmentChunk> chunks = new ArrayList<>();
        Point point = new Point(corePlayer.getPlayer().getLocation());
        Set<Long> collected = CoreZones.getInstance().getPlayers().get(corePlayer).getFragments().getCollected(getIdentifier());
        Optional<FragmentChunk> optionalChunk = fragmentChunkMap.values().stream()
                .sorted(Comparator.comparingDouble(chunk -> chunk.getCenter().distance(point)))
                .filter(chunk -> chunk.hasRemaining(collected))
                .findFirst();
        if (optionalChunk.isPresent()) {
            FragmentChunk fragmentChunk = optionalChunk.get();
            Point relativePoint = new Point(
                    point.x - fragmentChunk.chunkX * 16,
                    point.y,
                    point.z - fragmentChunk.chunkZ * 16);
            Optional<Fragment> optionalFragment = fragmentChunk
                    .getFragments()
                    .stream()
                    .sorted(Comparator.comparingDouble(fragment -> relativePoint.distance(new Point(fragment.x, fragment.y, fragment.z))))
                    .filter(fragment -> !collected.contains(fragment.fullId))
                    .findFirst();
            if (optionalFragment.isPresent()) {
                Fragment fragment = optionalFragment.get();
                return new Vector(
                        fragment.x + fragmentChunk.offsetX,
                        fragment.y + 0.5,
                        fragment.z + fragmentChunk.offsetZ);
            }
        }
        return null;
    }

}
