package com.spleefleague.core.crate;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.purse.CoreCurrency;
import com.spleefleague.core.vendor.Vendorable;
import org.bson.Document;
import org.bukkit.Material;

import java.util.*;

/**
 * @author NickM13
 * @since 2/2/2021
 */
public class CrateManager {

    private MongoCollection<Document> crateCollection;

    private Map<String, Crate> crateMap = new HashMap<>();
    private List<Crate> sortedCrates = new ArrayList<>();

    public void init() {
        crateCollection = Core.getInstance().getPluginDB().getCollection("Crates");
        reload();
    }

    public void reload() {
        crateMap.clear();
        for (Document doc : crateCollection.find()) {
            Crate crate = new Crate();
            crate.load(doc);
            crateMap.put(crate.getIdentifier(), crate);
        }

        refreshSorted();
    }

    public void close() {

    }

    private void refreshSorted() {
        sortedCrates.clear();

        for (Crate crate : crateMap.values()) {
            sortedCrates.add(crate);
        }

        sortedCrates.sort(Comparator.comparingInt(Crate::getPriority));
    }

    public Set<String> getCrateNames() {
        return crateMap.keySet();
    }

    public List<Crate> getSortedCrates() {
        return sortedCrates;
    }

    public void saveChanges(Crate crate) {
        crate.save(crateCollection);
    }

    public Crate get(String identifier) {
        return crateMap.get(identifier);
    }

    public boolean create(String identifier, String displayName) {
        if (crateMap.containsKey(identifier)) return false;
        Crate crate = new Crate(identifier, displayName);
        crateMap.put(crate.getIdentifier(), crate);
        saveChanges(crate);
        refreshSorted();
        return true;
    }

    public boolean destroy(String identifier) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.unsave(crateCollection);
        crateMap.remove(crate.getIdentifier());
        refreshSorted();
        return true;
    }

    public boolean setName(String identifier, String displayName) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setDisplayName(displayName);
        saveChanges(crate);
        return true;
    }

    public boolean setDescription(String identifier, String description) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setDescription(description);
        saveChanges(crate);
        return true;
    }

    public boolean setMaterial(String identifier, Material material) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setMaterial(material);
        saveChanges(crate);
        return true;
    }

    public boolean setClosedCmd(String identifier, Integer cmd) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setClosedCmd(cmd);
        saveChanges(crate);
        return true;
    }

    public boolean setOpenedCmd(String identifier, Integer cmd) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setOpenedCmd(cmd);
        saveChanges(crate);
        return true;
    }

    public boolean setArtisanItem(String identifier, Material material, Integer cmd) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setArtisanItem(material, cmd);
        saveChanges(crate);
        return true;
    }

    public boolean setStyle(String identifier, String style) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setStyle(style);
        saveChanges(crate);
        return true;
    }

    public boolean setPriority(String identifier, Integer priority) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setPriority(priority);
        saveChanges(crate);
        refreshSorted();
        return true;
    }

    public boolean setHidden(String identifier, Boolean hidden) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setHidden(hidden);
        saveChanges(crate);
        return true;
    }

    public boolean setCollectibleWeight(String identifier, Vendorable.Rarity rarity, Double weight) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setCollectibleWeight(rarity, weight);
        saveChanges(crate);
        return true;
    }

    public boolean setCurrencyWeight(String identifier, CoreCurrency currency, Double weight) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setCurrencyWeight(currency, weight);
        saveChanges(crate);
        return true;
    }

    public boolean setCollectibleCaps(String identifier, Double min, Double max) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setCollectibleCaps(min, max);
        saveChanges(crate);
        return true;
    }

    public boolean setCurrencyCaps(String identifier, Double min, Double max) {
        if (!crateMap.containsKey(identifier)) return false;
        Crate crate = crateMap.get(identifier);
        crate.setCurrencyCaps(min, max);
        saveChanges(crate);
        return true;
    }

}
