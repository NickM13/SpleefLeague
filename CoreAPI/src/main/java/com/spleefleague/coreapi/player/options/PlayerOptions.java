package com.spleefleague.coreapi.player.options;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

/**
 * @author NickM13
 */
public class PlayerOptions extends DBEntity {

    public enum OptionType {

        BOOLEAN(Boolean.class),
        INTEGER(Integer.class),
        DOUBLE(Double.class),
        STRING(String.class);

        Class<?> clazz;

        OptionType(Class<?> clazz) {
            this.clazz = clazz;
        }

    }

    @DBField protected Document optionMap = new Document();

    public void setBoolean(String option, boolean obj) {
        optionMap.put(option, obj);
    }

    public void setInteger(String option, int obj) {
        optionMap.put(option, obj);
    }

    public void setDouble(String option, double obj) {
        optionMap.put(option, obj);
    }

    public void setString(String option, String obj) {
        optionMap.put(option, obj);
    }

    public Boolean getBoolean(String option) {
        if (!(optionMap.get(option) instanceof Boolean)) {
            optionMap.put(option, true);
        }
        return optionMap.getBoolean(option);
    }

    public Integer getInteger(String option) {
        if (!(optionMap.get(option) instanceof Integer)) {
            optionMap.put(option, 0);
        }
        return optionMap.getInteger(option);
    }

    public Double getDouble(String option) {
        if (!(optionMap.get(option) instanceof Double)) {
            optionMap.put(option, 0D);
        }
        return optionMap.getDouble(option);
    }

    public String getString(String option) {
        if (!(optionMap.get(option) instanceof String)) {
            optionMap.put(option, "");
        }
        return optionMap.getString(option);
    }

    public Integer addInteger(String option, int value) {
        if (optionMap.getInteger(option) == null) optionMap.put(option, 0D);
        int newVal = optionMap.getInteger(option) + value;
        optionMap.put(option, newVal);
        return newVal;
    }

    public Integer addInteger(String option, int value, int min, int max) {
        if (optionMap.getInteger(option) == null) optionMap.put(option, 0D);
        int newVal = optionMap.getInteger(option) + value;
        if (newVal > max) newVal = min;
        else if (newVal < min) newVal = max;
        return (Integer) optionMap.put(option, newVal);
    }

    public Double addDouble(String option, double value) {
        if (optionMap.getDouble(option) == null) optionMap.put(option, 0D);
        double newVal = optionMap.getDouble(option) + value;
        optionMap.put(option, newVal);
        return newVal;
    }

    public Double addDouble(String option, double value, double min, double max) {
        if (optionMap.getDouble(option) == null) optionMap.put(option, 0D);
        double newVal = optionMap.getDouble(option) + value;
        if (newVal > max) newVal = min;
        else if (newVal < min) newVal = max;
        return (Double) optionMap.put(option, newVal);
    }

    public Boolean toggle(String option) {
        if (!optionMap.containsKey(option)) {
            return (Boolean) optionMap.put(option, false);
        }
        optionMap.put(option, !optionMap.getBoolean(option));
        return optionMap.getBoolean(option);
    }

}
