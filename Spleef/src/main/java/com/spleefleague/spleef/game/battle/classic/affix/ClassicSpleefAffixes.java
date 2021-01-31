package com.spleefleague.spleef.game.battle.classic.affix;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.hotbars.main.options.StaffToolsMenu;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefBattle;
import com.spleefleague.spleef.game.battle.classic.ClassicSpleefPlayer;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixArtillery;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixCutDown;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixDecay;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixHotStreak;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixLateGame;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixPunch;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixSlowFall;
import com.spleefleague.spleef.game.battle.classic.affix.affixes.AffixThunderdome;
import org.bson.Document;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author NickM13
 * @since 5/15/2020
 */
public class ClassicSpleefAffixes {

    private static Map<String, ClassicSpleefAffix> affixMap = new HashMap<>();
    private static Set<ClassicSpleefAffix> activeAffixes = new HashSet<>();

    private static MongoCollection<Document> affixCol;

    public static void createMenu() {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItemDynamic()
                .setName("Classic Spleef Affixes")
                .setDisplayItem(Material.DAYLIGHT_DETECTOR)
                .setDescription("Enable/Disable Affixes for Classic Spleef.  Only usable during the offseason!")
                .createLinkedContainer("Classic Spleef Affixes");
        menuItem.getLinkedChest()
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    for (String affixName : new TreeSet<>(affixMap.keySet())) {
                        container.addMenuItem(affixMap.get(affixName).createMenuItem());
                    }
                });
        StaffToolsMenu.getItem().getLinkedChest().addMenuItem(menuItem);
    }

    public static void init() {
        affixCol = Core.getInstance().getPluginDB().getCollection("Affixes");
        initAffix(new AffixArtillery());
        initAffix(new AffixCutDown());
        initAffix(new AffixDecay());
        initAffix(new AffixHotStreak());
        initAffix(new AffixLateGame());
        initAffix(new AffixPunch());
        /*
        initAffix(new AffixRegeneration());
         */
        initAffix(new AffixSlowFall());
        initAffix(new AffixThunderdome());
        //initAffix(new AffixWane());
        //initAffix(new AffixWither());
    }

    public static void refresh() {
        for (ClassicSpleefAffix affix : affixMap.values()) {
            Document doc = affixCol.find(new Document("identifier", affix.getIdentifier())).first();
            if (doc != null) affix.load(doc);
        }
    }

    private static <T extends ClassicSpleefAffix> void initAffix(T affix) {
        Document doc = affixCol.find(new Document("identifier", affix.getIdentifier())).first();
        if (doc != null) affix.load(doc);
        affixMap.put(affix.getIdentifier(), affix);
        if (affix.isActive()) {
            activeAffixes.add(affix);
        }
    }

    public static <T extends ClassicSpleefAffix> T get(Class<T> clazz) {
        return (T) affixMap.get(clazz.getSimpleName());
    }

    public static void saveAffix(ClassicSpleefAffix affix) {
        affix.save(affixCol);
    }

    public static void updateAffix(ClassicSpleefAffix affix) {
        if (affix.isActive()) {
            activeAffixes.add(affix);
        } else {
            activeAffixes.remove(affix);
        }
    }

    public static void startBattle(ClassicSpleefBattle battle) {
        for (ClassicSpleefAffix affix : activeAffixes) {
            affix.startBattle(battle);
        }
    }

    public static void startRound(ClassicSpleefBattle battle) {
        for (ClassicSpleefAffix affix : activeAffixes) {
            affix.startRound(battle);
        }
    }

    public static void updateField(ClassicSpleefBattle battle) {
        for (ClassicSpleefAffix affix : activeAffixes) {
            affix.update(battle);
        }
    }

    public static void onBlockBreak(ClassicSpleefPlayer csp) {
        for (ClassicSpleefAffix affix : activeAffixes) {
            affix.onBlockBreak(csp);
        }
    }

    public static void onRightClick(ClassicSpleefPlayer csp) {
        if (csp.getBattle().isRoundStarted()) {
            for (ClassicSpleefAffix affix : activeAffixes) {
                affix.onRightClick(csp);
            }
        }
    }

}
