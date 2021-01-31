package com.spleefleague.coreapi.player.options;

import com.spleefleague.coreapi.database.annotation.DBField;
import org.bson.Document;

/**
 * @author NickM13
 */
public class PlayerOptions {

    @DBField protected Document optionMap = new Document();

    public void set(String option, Object obj) {
        optionMap.put(option, obj);
    }

    public Boolean getBoolean(String option) {
        return optionMap.get(option, Boolean.class);
    }

    public Integer getInteger(String option) {
        if (!optionMap.containsKey(option)) {
            optionMap.put(option, 0);
        }
        return optionMap.get(option, Integer.class);
    }

    public void toggle(String option) {
        if (!optionMap.containsKey(option)) {
            optionMap.put(option, false);
        } else {
            optionMap.put(option, optionMap.getBoolean(option));
        }
    }

}
