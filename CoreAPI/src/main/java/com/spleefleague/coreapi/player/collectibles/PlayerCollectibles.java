package com.spleefleague.coreapi.player.collectibles;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 */
public class PlayerCollectibles extends DBEntity {

    protected final SortedMap<String, Map<String, CollectibleInfo>> collectibleMap = new TreeMap<>();
    @DBField protected final Map<String, String> activeMap = new HashMap<>();
    @DBField protected final Map<String, Boolean> enabledMap = new HashMap<>();

    public PlayerCollectibles() {

    }

    @DBLoad(fieldName = "collectibles")
    protected void loadCollectibles(Document doc) {
        if (doc != null) {
            collectibleMap.clear();
            for (Map.Entry<String, Object> typePair : doc.entrySet()) {
                String type = typePair.getKey();
                Document collection = (Document) typePair.getValue();
                collectibleMap.put(type, new TreeMap<>());
                for (Map.Entry<String, Object> collectionPair : collection.entrySet()) {
                    CollectibleInfo collectibleInfo = new CollectibleInfo();
                    collectibleInfo.load((Document) collectionPair.getValue());
                    collectibleMap.get(type).put(collectionPair.getKey(), collectibleInfo);
                }
            }
        }
    }

    @DBSave(fieldName = "collectibles")
    protected Document saveCollectibles() {
        Document doc = new Document();
        for (Map.Entry<String, Map<String, CollectibleInfo>> collectibleType : collectibleMap.entrySet()) {
            Document collectionFullDoc = new Document();
            for (Map.Entry<String, CollectibleInfo> collectible : collectibleType.getValue().entrySet()) {
                collectionFullDoc.append(collectible.getKey(), collectible.getValue().toDocument());
            }
            doc.append(collectibleType.getKey(), collectionFullDoc);
        }
        return doc;
    }

    public boolean add(String type, String identifier) {
        return getInfo(type, identifier).unlockBase();
    }

    public boolean remove(String type, String identifier) {
        if (getInfo(type, identifier).lockBase()) {
            deactivate(type, identifier);
            return true;
        }
        return false;
    }

    public boolean removeSkin(String type, String identifier, String skin) {
        return getInfo(type, identifier).removeSkin(skin);
    }

    public int addSkin(String type, String identifier, String skin) {
        return getInfo(type, identifier).addSkin(skin, System.currentTimeMillis()) ? 0 : 2;
    }

    public CollectibleInfo getInfo(String type, String identifier) {
        if (!collectibleMap.containsKey(type)) {
            collectibleMap.put(type, new HashMap<>());
        }
        collectibleMap.get(type).putIfAbsent(identifier, new CollectibleInfo());
        return collectibleMap.get(type).get(identifier);
    }

    public void setActiveItem(String type, String identifier) {
        activeMap.put(type, identifier);
    }

    public void setActiveItem(String type, String identifier, String affix) {
        activeMap.put(type + affix, identifier);
    }

    public void setSkin(String type, String identifier, String skin) {
        getInfo(type, identifier).setSelectedSkin(skin);
    }

    public void removeActiveItem(String type) {
        activeMap.remove(type);
    }

    public void removeActiveItem(String type, String affix) {
        activeMap.remove(type + affix);
    }

    protected void deactivate(String type, String identifier) {
        if (activeMap.containsKey(type) &&
                activeMap.get(type).equalsIgnoreCase(identifier)) {
            activeMap.remove(type);
        }
    }

    public void setEnabled(String type, boolean state) {
        enabledMap.put(type, state);
    }

}
