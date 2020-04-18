package com.spleefleague.spleef.field;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.spleefleague.core.world.FakeBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class Field {

    private static final Map<String, Field> FIELDS = new HashMap<>();
    
    public static void createField(String fieldName) {
        FIELDS.put(fieldName, new Field(fieldName));
    }
    
    public static Field getField(String fieldName) {
        return FIELDS.get(fieldName);
    }
    
    public static Set<String> getFieldNames() {
        return FIELDS.keySet();
    }
    
    private final String name;
    private Map<BlockPosition, FakeBlock> fakeBlocks;
    private boolean underConstruction = false;

    public Field(String name) {
        this.name = name;
        fakeBlocks = new HashMap<>();
    }
    
    public final String getName() {
        return name;
    }
    
    public boolean isUnderConstruction() {
        return underConstruction;
    }
    public void setUnderConstruction(boolean state) {
        underConstruction = state;
    }

    public void setBlock(FakeBlock fb) {
        fakeBlocks.put(fb.getBlockPosition(), fb);
    }

}
