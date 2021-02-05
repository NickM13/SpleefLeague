package com.spleefleague.coreapi.player.collectibles;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 */
public class CollectibleInfo extends DBEntity {

    @DBField Long collectDate;
    @DBField String selectedSkin = "";
    @DBField String name = null;
    Map<String, Long> ownedSkins = new HashMap<>();

    /**
     * For creating an empty object to load info from document
     */
    public CollectibleInfo() {

    }

    /**
     * For newly unlocked collectibles
     *
     * @param collectDate Time Collected (millis)
     */
    public CollectibleInfo(Long collectDate) {
        this.collectDate = collectDate;
    }

    public void setSelectedSkin(String skin) {
        this.selectedSkin = skin;
    }

    public String getSelectedSkin() {
        return selectedSkin;
    }

    public boolean addSkin(String skin, Long collectDate) {
        if (ownedSkins.containsKey(skin)) return false;
        ownedSkins.put(skin, collectDate);
        return true;
    }

    public boolean removeSkin(String skin) {
        return ownedSkins.remove(skin) != null;
    }

    public Map<String, Long> getOwnedSkins() {
        return ownedSkins;
    }

    @DBLoad(fieldName = "ownedSkins")
    private void loadOwnedSkins(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            ownedSkins.put(entry.getKey(), (Long) entry.getValue());
        }
    }

    @DBSave(fieldName = "ownedSkins")
    private Document saveOwnedSkins() {
        Document doc = new Document();
        for (Map.Entry<String, Long> entry : ownedSkins.entrySet()) {
            doc.put(entry.getKey(), entry.getValue());
        }
        return doc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
