package com.spleefleague.zone.player.fragments;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.zone.CoreZones;
import com.spleefleague.zone.fragments.FragmentContainer;
import com.spleefleague.zone.monuments.Monument;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 2/14/2021
 */
public class PlayerFragments extends DBEntity {

    private final Map<String, Set<Long>> collected = new HashMap<>();
    @DBField private final Map<String, Integer> deposited = new HashMap<>();
    @DBField private final Map<String, Integer> undeposited = new HashMap<>();
    @DBField private final Map<String, Integer> stages = new HashMap<>();
    private final Map<String, Integer> undepositedBuffer = new HashMap<>();

    @Override
    public void afterLoad() {
        super.afterLoad();
        undepositedBuffer.clear();
        undepositedBuffer.putAll(undeposited);
    }

    @DBSave(fieldName = "collected")
    private Document saveCollected() {
        Document doc = new Document();
        collected.forEach(doc::append);
        return doc;
    }

    @DBLoad(fieldName = "collected")
    private void loadCollected(Document doc) {
        collected.clear();
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            Set<Long> set = new HashSet<>();
            for (Object o : ((List<?>) entry.getValue())) {
                set.add((long) o);
            }
            collected.put(entry.getKey(), set);
        }
    }

    public double getCollectedPercent(FragmentContainer fragment) {
        if (!collected.containsKey(fragment.getIdentifier())) return 0;
        return (double) collected.get(fragment.getIdentifier()).size() / fragment.getTotal();
    }

    public void clearStages() {
        stages.clear();
    }

    public void clear(String identifier) {
        if (collected.containsKey(identifier)) {
            collected.get(identifier).clear();
            deposited.put(identifier, 0);
            undeposited.put(identifier, 0);
            undepositedBuffer.put(identifier, 0);
        }
    }

    public void admin(String identifier) {
        collected.put(identifier, new HashSet<>());
        undeposited.put(identifier, CoreZones.getInstance().getFragmentManager().getContainer(identifier).getTotal());
        undepositedBuffer.put(identifier, CoreZones.getInstance().getFragmentManager().getContainer(identifier).getTotal());
        deposited.put(identifier, 0);
    }

    public int add(String identifier, long pos) {
        if (!collected.containsKey(identifier)) {
            collected.put(identifier, new HashSet<>());
            undeposited.put(identifier, 0);
            undepositedBuffer.put(identifier, 0);
            deposited.put(identifier, 0);
        }
        if (collected.get(identifier).add(pos)) {
            undeposited.put(identifier, undeposited.getOrDefault(identifier, 0) + 1);
            undepositedBuffer.put(identifier, undepositedBuffer.getOrDefault(identifier, 0) + 1);
            return collected.get(identifier).size();
        }
        return -1;
    }

    public Map<String, Set<Long>> getCollected() {
        return collected;
    }

    private static final Set<Long> EMPTY = new HashSet<>();

    public Set<Long> getCollected(String identifier) {
        return collected.getOrDefault(identifier, EMPTY);
    }

    public int getCollectedCount(String identifier) {
        return collected.getOrDefault(identifier, EMPTY).size();
    }

    public int getDeposited(String identifier) {
        return deposited.getOrDefault(identifier, 0);
    }

    public int getUndeposited(String identifier) {
        return undeposited.getOrDefault(identifier, 0);
    }

    public boolean onDeposit(String identifier) {
        if (undeposited.getOrDefault(identifier, 0) > 0) {
            deposited.put(identifier, deposited.getOrDefault(identifier, 0) + 1);
            undeposited.put(identifier, undeposited.get(identifier) - 1);
            return true;
        }
        return false;
    }

    public boolean onDepositBuffer(String identifier) {
        if (undepositedBuffer.getOrDefault(identifier, 0) > 0) {
            undepositedBuffer.put(identifier, undepositedBuffer.get(identifier) - 1);
            return true;
        }
        return false;
    }

    public int getStage(Monument monument) {
        return stages.getOrDefault(monument.getIdentifier(), -1);
    }

    public void incrementStage(Monument monument) {
        stages.put(monument.getIdentifier(), getStage(monument) + 1);
    }

}
