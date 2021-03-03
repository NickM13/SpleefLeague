package com.spleefleague.zone.zones;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.util.variable.Point;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.monuments.Monument;
import com.spleefleague.zone.player.ZonePlayer;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class ZoneManager {

    private final Map<ChunkCoord, List<Zone>> chunkListMap = new HashMap<>();

    private final Map<String, Zone> zoneMap = new HashMap<>();
    private final Set<Zone> mainZones = new HashSet<>();

    private MongoCollection<Document> zoneCollection;

    private BukkitTask autoSaveTask;
    private BukkitTask hotbarTask;

    public void init() {
        zoneCollection = CoreZones.getInstance().getPluginDB().getCollection("Zones");

        for (Document doc : zoneCollection.find()) {
            Zone zone = new Zone();
            zone.load(doc);
            zoneMap.put(zone.getIdentifier(), zone);
            for (ChunkCoord chunkCoord : zone.getUsedChunks()) {
                if (!chunkListMap.containsKey(chunkCoord)) {
                    chunkListMap.put(chunkCoord, new ArrayList<>());
                    chunkListMap.get(chunkCoord).add(zone);
                } else {
                    chunkListMap.get(chunkCoord).add(zone);
                    chunkListMap.get(chunkCoord).sort(Comparator.comparingDouble(Zone::getPriority).reversed());
                }
            }
        }

        autoSaveTask = Bukkit.getScheduler().runTaskTimer(CoreZones.getInstance(), this::autosave, 40L, 40L);

        hotbarTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            for (ZonePlayer zp : CoreZones.getInstance().getPlayers().getAllLocal()) {
                zp.showZoneHotbar();
            }
        }, 20L, 20L);
    }

    public void close() {
        autoSaveTask.cancel();
        hotbarTask.cancel();
        autosave();
    }

    private void autosave() {
        for (Zone zone : zoneMap.values()) {
            if (zone.onSave()) {
                zone.save(zoneCollection);
            }
        }
    }

    public void addMain(Zone zone) {
        mainZones.add(zone);
    }

    public void removeMain(Zone zone) {
        mainZones.remove(zone);
    }

    public Set<Zone> getMainZones() {
        return mainZones;
    }

    public Set<String> getZoneNames() {
        return zoneMap.keySet();
    }

    private static final InventoryMenuItem ZONE_WAND = InventoryMenuAPI.createItemStatic()
            .setName("Zone Wand")
            .setDisplayItem(Material.IRON_AXE)
            .setDescription("Left click: unset zone\nRight click: set zone");

    public ItemStack getZoneWand() {
        return ZONE_WAND.createItem(null);
    }

    public boolean create(String identifier) {
        if (zoneMap.containsKey(identifier)) {
            return false;
        }
        Zone zone = new Zone(identifier);
        zoneMap.put(identifier, zone);
        zone.save(zoneCollection);
        return true;
    }

    public void addZoneChunk(ChunkCoord chunkCoord, Zone zone) {
        if (!chunkListMap.containsKey(chunkCoord)) {
            chunkListMap.put(chunkCoord, new ArrayList<>());
            chunkListMap.get(chunkCoord).add(zone);
        } else {
            chunkListMap.get(chunkCoord).add(zone);
            chunkListMap.get(chunkCoord).sort(Comparator.comparingDouble(Zone::getPriority).reversed());
        }
    }

    public void removeZoneChunk(ChunkCoord chunkCoord, Zone zone) {
        chunkListMap.get(chunkCoord).removeIf(z -> zone.getIdentifier().equals(z.getIdentifier()));
    }

    public void setDisplayName(String identifier, String displayName) {
        Zone zone = zoneMap.get(identifier);
        zone.setDisplayName(displayName);
        zone.save(zoneCollection);
    }

    public void setParent(String identifier, String parent) {
        Zone zone = zoneMap.get(identifier);
        zone.setParent(parent);
        zone.save(zoneCollection);
    }

    public boolean addZoneViewer(String identifier, CorePlayer cp) {
        Zone zone = zoneMap.get(identifier);
        return zone.addViewer(cp);
    }

    public Zone get(String identifier) {
        return zoneMap.get(identifier);
    }

    public void setPriority(String identifier, double priority) {
        Zone zone = zoneMap.get(identifier);
        zone.setPriority(priority);
        zone.save(zoneCollection);
    }

    public void setHealthRegen(String identifier, boolean state) {
        Zone zone = zoneMap.get(identifier);
        zone.setNaturalRegen(state);
        zone.save(zoneCollection);
    }

    public void setWeather(String identifier, boolean state) {
        Zone zone = zoneMap.get(identifier);
        zone.setWeather(state);
        zone.save(zoneCollection);
    }

    public void setRain(String identifier, int rain) {
        Zone zone = zoneMap.get(identifier);
        zone.setRain(rain);
        zone.save(zoneCollection);
    }

    public void setThunder(String identifier, int thunder) {
        Zone zone = zoneMap.get(identifier);
        zone.setThunder(thunder);
        zone.save(zoneCollection);
    }

    public void onPlayerJoin(ZonePlayer zonePlayer) {
        onPlayerMove(zonePlayer.getPlayer(), zonePlayer.getPlayer().getLocation());
    }

    public void onPlayerRightClick(CorePlayer cp, Point point, Vector direction) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(cp.getUniqueId());
        Zone zone = zoneMap.get(editor.getTargetZone());
        if (zone == null) {
            CoreZones.getInstance().sendMessage(cp, "No target zone set! Use /z b z <zoneName>");
            return;
        }

        BlockRaycastResult result = null;
        World world = cp.getPlayer().getWorld();
        /*
        for (BlockRaycastResult blockResult : point.castBlocks(direction, 32)) {
            BlockPosition pos = blockResult.getBlockPos();
            int horiz = (((pos.getX() & 0xF) / 2) << 3) + (pos.getZ() & 0xF) / 2;
            if (!pos.toLocation(world).getBlock().isPassable() ||
                    zone.isContained(new ChunkCoord(pos.getX() >> 4, pos.getZ() >> 4), horiz, pos.getY() / 2)) {
                result = blockResult;
                break;
            }
        }
        */
        for (BlockRaycastResult blockResult : point.castBlocks(direction, 32)) {
            if (!blockResult.getBlockPos().toLocation(world).getBlock().isPassable()) {
                result = blockResult;
                break;
            }
        }
        if (result == null) return;

        BlockPosition original = result.getRelative();
        Map<ChunkCoord, Long> chunkCubeMap = createCylindricalData(original, editor.getBrushSize());

        int chunkX = original.getX() >> 4;
        int chunkZ = original.getZ() >> 4;
        int offsetY = original.getY() / 2 - editor.getBrushDrop();

        for (Map.Entry<ChunkCoord, Long> entry : chunkCubeMap.entrySet()) {
            zone.setData(
                    new ChunkCoord(entry.getKey().x + chunkX, entry.getKey().z + chunkZ),
                    entry.getValue(),
                    offsetY,
                    editor.getBrushHeight());

            long data = entry.getValue();
            double x = 0, z = 0;
            double ox = (entry.getKey().x + chunkX) * 16;
            double oz = (entry.getKey().z + chunkZ) * 16;
            for (int i = 0; i < 64; i++) {
                if ((data & 1) != 0) {
                    double posX = (ox + x * 2 + 1);
                    double posZ = (oz + z * 2 + 1);
                    if (Math.sqrt(Math.pow(posX - original.getX(), 2) + Math.pow(posZ - original.getZ(), 2)) > editor.getBrushSize() * 2 - 2) {
                        for (int y = 0; y < editor.getBrushHeight(); y++) {
                            cp.getPlayer().getWorld().spawnParticle(Particle.TOTEM, posX, (y + offsetY) * 2 + 1, posZ, 1, 0, 0, 0, 0, null, true);
                        }
                    }
                }
                data = data >> 1;
                if (i % 8 == 7) {
                    x++;
                    z = 0;
                } else {
                    z++;
                }
            }
        }
    }

    public void onPlayerLeftClick(CorePlayer cp, Point point, Vector direction) {
        ZonePlayer editor = CoreZones.getInstance().getPlayers().get(cp.getUniqueId());
        Zone zone = zoneMap.get(editor.getTargetZone());
        if (zone == null) {
            CoreZones.getInstance().sendMessage(cp, "No target zone set! Use /z b z <zoneName>");
            return;
        }

        BlockRaycastResult result = null;
        World world = cp.getPlayer().getWorld();
        for (BlockRaycastResult blockResult : point.castBlocks(direction, 32)) {
            BlockPosition pos = blockResult.getBlockPos();
            int horiz = (((pos.getX() & 0xF) / 2) << 3) + (pos.getZ() & 0xF) / 2;
            if (!pos.toLocation(world).getBlock().isPassable() ||
                    zone.isContained(new ChunkCoord(pos.getX() >> 4, pos.getZ() >> 4), horiz, pos.getY() / 2)) {
                result = blockResult;
                break;
            }
        }
        if (result == null) return;

        BlockPosition original = result.getBlockPos();
        Map<ChunkCoord, Long> chunkCubeMap = createCylindricalData(original, editor.getBrushSize());

        int chunkX = original.getX() >> 4;
        int chunkZ = original.getZ() >> 4;
        int offsetY = original.getY() / 2 - editor.getBrushDrop();

        for (Map.Entry<ChunkCoord, Long> entry : chunkCubeMap.entrySet()) {
            zone.unsetData(
                    new ChunkCoord(entry.getKey().x + chunkX, entry.getKey().z + chunkZ),
                    0xFFFFFFFF - entry.getValue(),
                    offsetY,
                    editor.getBrushHeight());

            long data = entry.getValue();
            double x = 0, z = 0;
            double ox = (entry.getKey().x + chunkX) * 16;
            double oz = (entry.getKey().z + chunkZ) * 16;
            for (int i = 0; i < 64; i++) {
                if ((data & 1) != 0) {
                    double posX = (ox + x * 2 + 1);
                    double posZ = (oz + z * 2 + 1);
                    if (Math.sqrt(Math.pow(posX - original.getX(), 2) + Math.pow(posZ - original.getZ(), 2)) > editor.getBrushSize() * 2 - 2) {
                        for (int y = 0; y < editor.getBrushHeight(); y++) {
                            cp.getPlayer().getWorld().spawnParticle(Particle.SWEEP_ATTACK, (ox + x * 2 + 1), (y + offsetY) * 2 + 1, (oz + z * 2 + 1), 1, 0, 0, 0, 0, null, true);
                        }
                    }
                }
                data = data >> 1;
                if (i % 8 == 7) {
                    x++;
                    z = 0;
                } else {
                    z++;
                }
            }
        }
    }

    public void onPlayerMove(Player player, Location loc) {
        ZonePlayer zp = CoreZones.getInstance().getPlayers().get(player.getUniqueId());

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        if (y < 0 || y > 255) {
            zp.setZone(null);
            return;
        }

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        short cubeY = (short) ((y & 0xFF) / 2);

        int horiz = (((x & 0xF) / 2) << 3) + (z & 0xF) / 2;

        ChunkCoord chunkCoord = new ChunkCoord(chunkX, chunkZ);

        Zone inZone = null;
        if (chunkListMap.containsKey(chunkCoord)) {
            for (Zone zone : chunkListMap.get(chunkCoord)) {
                if (zone.isContained(chunkCoord, horiz, cubeY)) {
                    inZone = zone;
                    break;
                }
            }
        }
        zp.setZone(inZone);
    }

    private Map<ChunkCoord, Long> createCylindricalData(BlockPosition start, int bSize) {
        Map<ChunkCoord, Long> chunkCubeMap = new HashMap<>();

        int offsetX = (start.getX() & 0xF) / 2;
        int offsetZ = (start.getZ() & 0xF) / 2;

        for (int x = -bSize; x <= bSize; x++) {
            for (int z = -bSize; z <= bSize; z++) {
                if (Math.sqrt(x * x + z * z) / bSize < 1) {
                    int x2 = x + offsetX;
                    int z2 = z + offsetZ;
                    ChunkCoord chunkCoord = new ChunkCoord(x2 >> 3, z2 >> 3);
                    long insert = (1L << (((x2 & 7) << 3) + (z2 & 7)));
                    chunkCubeMap.put(chunkCoord, chunkCubeMap.getOrDefault(chunkCoord, 0L) | insert);
                }
            }
        }

        return chunkCubeMap;
    }

    private static String longToBinary(long l) {
        StringBuilder binaryNumber = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            if (i % 8 == 0 && i != 0) binaryNumber.append("\n");
            binaryNumber.append(l & 1);
            l = l >> 1;
        }
        binaryNumber = binaryNumber.reverse();
        return binaryNumber.toString();
    }

}
