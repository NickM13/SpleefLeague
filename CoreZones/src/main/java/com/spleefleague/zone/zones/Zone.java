package com.spleefleague.zone.zones;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.ChunkCoord;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.zone.CoreZones;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class Zone extends DBEntity {

    @DBField private String parent = "";
    @DBField private String displayName = "";
    @DBField private Double priority = 0D;
    @DBField private Boolean naturalRegen = true;
    @DBField private Boolean weather = false;
    @DBField private Integer rain = 0;
    @DBField private Integer thunder = 0;
    @DBField private Set<String> permissions = new HashSet<>();
    @DBField private Set<String> exclusivePermissions = new HashSet<>();

    private boolean main = false;

    private final Set<CorePlayer> viewers = new HashSet<>();

    private boolean modified = false;

    private final Map<ChunkCoord, ZoneChunk> chunkMap = new HashMap<>();

    public Zone() {

    }

    public Zone(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void afterLoad() {
        main = priority <= 0.2;
        if (main) {
            CoreZones.getInstance().getZoneManager().addMain(this);
        }
    }

    public boolean isMain() {
        return main;
    }

    public boolean addViewer(CorePlayer cp) {
        if (viewers.add(cp)) {
            sendFull(cp);
            return true;
        }
        viewers.remove(cp);
        sendUnview(cp);
        return false;
    }

    public void sendFull(CorePlayer cp) {
        for (Map.Entry<ChunkCoord, ZoneChunk> chunkCoordZoneChunkEntry : chunkMap.entrySet()) {
            ZoneChunk chunk = chunkCoordZoneChunkEntry.getValue();
            sendChunk(cp, chunk);
        }
    }

    public void sendUpdate() {
        for (Map.Entry<ChunkCoord, ZoneChunk> chunkCoordZoneChunkEntry : chunkMap.entrySet()) {
            ZoneChunk chunk = chunkCoordZoneChunkEntry.getValue();
            if (chunk.onModified()) {
                for (CorePlayer viewer : viewers) {
                    sendChunk(viewer, chunk);
                }
            }
        }
    }

    public void sendChunk(CorePlayer cp, ZoneChunk chunk) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeInt(0);
        output.writeUTF(identifier);
        output.writeDouble(priority);
        output.write(chunk.toPacket());
        cp.getPlayer().sendPluginMessage(CoreZones.getInstance(), "slcore:zones", output.toByteArray());
    }

    public void sendUnview(CorePlayer cp) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeInt(1);
        output.writeUTF(identifier);
        cp.getPlayer().sendPluginMessage(CoreZones.getInstance(), "slcore:zones", output.toByteArray());
    }

    public Set<ChunkCoord> getUsedChunks() {
        return chunkMap.keySet();
    }

    @DBSave(fieldName = "data")
    protected List<Document> saveData() {
        sendUpdate();
        List<Document> docList = new ArrayList<>();
        Iterator<Map.Entry<ChunkCoord, ZoneChunk>> it = chunkMap.entrySet().iterator();
        while (it.hasNext()) {
            ZoneChunk chunk = it.next().getValue();
            if (chunk.isEmpty()) {
                it.remove();
                continue;
            }
            docList.add(chunk.save());
        }
        return docList;
    }

    @DBLoad(fieldName = "data")
    protected void loadData(List<Document> docs) {
        for (Document doc : docs) {
            ZoneChunk chunk = new ZoneChunk(doc);
            chunkMap.put(new ChunkCoord(chunk.chunkX, chunk.chunkZ), chunk);
        }
    }

    public boolean isContained(ChunkCoord chunkCoord, int h, int y) {
        return chunkMap.containsKey(chunkCoord) && chunkMap.get(chunkCoord).isContained(h, y);
    }

    public void setData(ChunkCoord chunkCoord, long data, int startY, int height) {
        int cap = Math.min(128, startY + height);
        if (!chunkMap.containsKey(chunkCoord)) {
            chunkMap.put(chunkCoord, new ZoneChunk(chunkCoord.x, chunkCoord.z));
            CoreZones.getInstance().getZoneManager().addZoneChunk(chunkCoord, this);
        }
        ZoneChunk chunk = chunkMap.get(chunkCoord);
        chunk.setModified();
        for (int i = Math.max(0, startY); i < cap; i++) {
            chunkMap.get(chunkCoord).set(i, data);
        }
        modified = true;
    }

    public void unsetData(ChunkCoord chunkCoord, long data, int startY, int height) {
        int cap = Math.min(128, startY + height);
        if (chunkMap.containsKey(chunkCoord)) {
            ZoneChunk chunk = chunkMap.get(chunkCoord);
            chunk.setModified();
            for (int i = Math.max(0, startY); i < cap; i++) {
                chunk.unset(i, data);
            }
            if (chunk.isEmpty()) {
                chunkMap.remove(chunkCoord);
                CoreZones.getInstance().getZoneManager().removeZoneChunk(chunkCoord, this);
            }
            modified = true;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void setPriority(double priority) {
        this.priority = priority;
        main = priority <= 0.2;
        if (main) {
            CoreZones.getInstance().getZoneManager().addMain(this);
        } else {
            CoreZones.getInstance().getZoneManager().removeMain(this);
        }
    }

    public double getPriority() {
        return priority;
    }

    public void setNaturalRegen(boolean regen) {
        this.naturalRegen = regen;
    }

    public boolean allowNaturalRegen() {
        return naturalRegen;
    }

    public void setWeather(boolean weather) {
        this.weather = weather;
    }

    public boolean hasWeather() {
        return weather;
    }

    public void setRain(int rain) {
        this.rain = rain;
    }

    public int getRain() {
        return rain;
    }

    public void setThunder(int thunder) {
        this.thunder = thunder;
    }

    public int getThunder() {
        return thunder;
    }

    public boolean hasPermission(String usageZone) {
        return usageZone.equals(identifier) || usageZone.equals(parent);
    }

    public boolean onSave() {
        if (modified) {
            modified = false;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "identifier='" + identifier + '\'' +
                ", parent='" + parent + '\'' +
                ", displayName='" + displayName + '\'' +
                ", priority=" + priority +
                '}';
    }

}
