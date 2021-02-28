package com.spleefleague.coreapi.player.crate;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 2/1/2021
 */
public class PlayerCrates extends DBEntity {

    @DBField private Map<String, Integer> owned = new HashMap<>();
    @DBField private Map<String, Integer> opened = new HashMap<>();

    public int getCrateCount(String crate) {
        return owned.getOrDefault(crate, 0);
    }

    public void setCrateCount(String crate, int count) {
        owned.put(crate, count);
    }

    public void changeCrateCount(String crate, int amount) {
        owned.put(crate, owned.getOrDefault(crate, 0) + amount);
    }

    public int getOpenedCrates(String crate) {
        return opened.get(crate);
    }

    public void addOpenedCrates(String crate, int amount) {
        this.opened.put(crate, Math.abs(this.opened.getOrDefault(crate, 0)) + amount);
    }

}
