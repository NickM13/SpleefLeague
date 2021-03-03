package com.spleefleague.spleef.game.battle.classic.affix;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.options.StaffToolsMenu;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixDecay;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixThunderdome;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class ClassicSpleefAffixes {

    public enum AffixType {

        DECAY("Decay", AffixDecay.class),
        THUNDERDOME("Thunderdome", AffixThunderdome.class);

        public String displayName;
        public Class<? extends ClassicSpleefAffix> clazz;

        AffixType(String displayName, Class<? extends ClassicSpleefAffix> clazz) {
            this.displayName = displayName;
            this.clazz = clazz;
        }

        public String getDisplayName() {
            return displayName;
        }

    }

    private static final Set<AffixType> activeAffixes = new HashSet<>();

    private static MongoCollection<Document> affixColl;

    public static void createMenu() {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Classic Spleef Affixes")
                .setDisplayItem(Material.DAYLIGHT_DETECTOR)
                .setDescription("Enable/Disable Affixes for Classic Spleef.  Only usable during the offseason!")
                .createLinkedContainer("Classic Spleef Affixes");
        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    for (AffixType type : AffixType.values()) {
                        container.addMenuItem(InventoryMenuAPI.createItemDynamic()
                                .setName(type.clazz.getSimpleName())
                                .setDescription("")
                                .setDisplayItem(cp2 -> new ItemStack(activeAffixes.contains(type) ? Material.GLOWSTONE : Material.REDSTONE_LAMP))
                                .setAction(cp2 -> toggle(type))
                                .setCloseOnAction(false));
                    }
                });
        StaffToolsMenu.getItem().getLinkedChest().addMenuItem(menuItem);
    }

    public static void init() {
        affixColl = Spleef.getInstance().getPluginDB().getCollection("Affixes");

        for (Document doc : affixColl.find()) {
            try {
                AffixType type = AffixType.valueOf(doc.getString("identifier"));
                activeAffixes.add(type);
            } catch (IllegalArgumentException ignored) {
                affixColl.deleteMany(doc);
            }
        }
    }

    public static void refresh() {
        activeAffixes.clear();
        for (Document doc : affixColl.find()) {
            try {
                AffixType type = AffixType.valueOf(doc.getString("identifier"));
                activeAffixes.add(type);
            } catch (IllegalArgumentException ignored) { }
        }
    }

    public static void toggle(AffixType type) {
        if (activeAffixes.contains(type)) {
            activeAffixes.remove(type);
            affixColl.deleteMany(new Document("identifier", type.name()));
        } else {
            activeAffixes.add(type);
            affixColl.insertOne(new Document("identifier", type.name()));
        }
    }

    public static String getActiveDisplayNames() {
        return CoreUtils.mergeSetString(activeAffixes.stream().map(AffixType::getDisplayName).collect(Collectors.toSet()));
    }

    public static List<ClassicSpleefAffix> startBattle(ClassicSpleefBattle battle) {
        List<ClassicSpleefAffix> affixes = new ArrayList<>();

        for (AffixType type : activeAffixes) {
            try {
                affixes.add(type.clazz.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return affixes;
    }

}
