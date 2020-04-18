package com.spleefleague.spleef.field;

import com.google.common.collect.Sets;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.build.BuildWorld;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author NickM13
 * @since 4/16/2020
 */
public class FieldBuilder {

    private static Map<CorePlayer, FieldBuilder> builderMap = new HashMap<>();
    private static final Set<Material> buildMaterials = Sets.newHashSet(Material.GRASS_BLOCK);

    public static void createField(CorePlayer cp, String fieldName) {
        Field.createField(fieldName);
        editField(cp, fieldName);
    }
    
    public static void editField(CorePlayer cp, String fieldName) {
        Field field = Field.getField(fieldName);
        if (field != null) {
            builderMap.put(cp, new FieldBuilder(cp, field));
        }
    }

    public static void saveField(CorePlayer cp) {
        if (builderMap.containsKey(cp))
            builderMap.get(cp).save();
    }
    
    public static void leaveField(CorePlayer cp) {
        if (builderMap.containsKey(cp)) {
            builderMap.get(cp).leave(cp);
        }
    }
    
    private final CorePlayer owner;
    private final BuildWorld buildWorld;
    private final Field field;

    public FieldBuilder(CorePlayer cp, Field field) {
        this.owner = cp;
        this.buildWorld = new BuildWorld(cp.getLocation().getWorld(), cp, buildMaterials);
        this.field = field;
        this.field.setUnderConstruction(true);
    }

    public void save() {
        buildWorld.getFakeBlocks().forEach((pos, fb) -> {
            field.setBlock(fb);
        });
    }
    
    public void leave(CorePlayer cp) {
        save();
        BuildWorld.removePlayerGlobal(cp);
        if (cp.equals(owner)) {
            field.setUnderConstruction(false);
            BuildWorld.closeBuildWorld(buildWorld);
        }
    }
    
}
