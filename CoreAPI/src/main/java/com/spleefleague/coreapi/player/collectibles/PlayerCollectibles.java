package com.spleefleague.coreapi.player.collectibles;

import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 */
public class PlayerCollectibles extends DBEntity {

    protected final SortedMap<String, Map<String, CollectibleInfo>> collectibleMap;
    protected final SortedMap<String, String> activeMap;

    public PlayerCollectibles() {
        this.collectibleMap = new TreeMap<>();
        this.activeMap = new TreeMap<>();
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

    @DBLoad(fieldName = "active")
    protected void loadActive(Document doc) {
        activeMap.clear();
        for (Map.Entry<String, Object> activePair : doc.entrySet()) {
            activeMap.put(activePair.getKey(), (String) activePair.getValue());
        }
    }

    @DBSave(fieldName = "active")
    protected Document saveActive() {
        Document doc = new Document();
        for (Map.Entry<String, String> active : activeMap.entrySet()) {
            doc.append(active.getKey(), active.getValue());
        }
        return doc;
    }

    public boolean add(String type, String identifier) {
        if (!collectibleMap.containsKey(type)) {
            collectibleMap.put(type, new HashMap<>());
        }
        collectibleMap.get(type).put(identifier, new CollectibleInfo(System.currentTimeMillis()));
        return true;
    }

    public boolean removeSkin(String type, String identifier, String skin) {
        if (collectibleMap.containsKey(type)) {
            if (collectibleMap.get(type).containsKey(identifier)) {
                return (collectibleMap.get(type).get(identifier).removeSkin(skin));
            }
        }
        return false;
    }

    public int addSkin(String type, String identifier, String skin) {
        if (!collectibleMap.containsKey(type) ||
                !collectibleMap.get(type).containsKey(identifier)) {
            add(type, identifier);
        }
        return collectibleMap.get(type).get(identifier).addSkin(skin, System.currentTimeMillis()) ? 0 : 2;
    }

    public boolean contains(String type, String identifier) {
        if (collectibleMap.containsKey(type)) {
            return collectibleMap.get(type).get(identifier) != null;
        }
        return false;
    }

    public CollectibleInfo getInfo(String type, String identifier) {
        if (!contains(type, identifier)) {
            add(type, identifier);
        }
        return collectibleMap.get(type).get(identifier);
    }

    public boolean remove(String type, String identifier) {
        if (collectibleMap.containsKey(type)) {
            collectibleMap.get(type).remove(identifier);
            deactivate(type, identifier);
            return true;
        }
        return false;
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

}
