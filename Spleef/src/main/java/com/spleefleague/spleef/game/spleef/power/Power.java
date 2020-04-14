/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game.spleef.power;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.annotation.DBField;
import com.spleefleague.core.annotation.DBLoad;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.util.database.DBEntity;
import com.spleefleague.spleef.Spleef;
import com.spleefleague.spleef.game.spleef.power.effect.Effect;
import com.spleefleague.spleef.game.spleef.power.effect.EffectBuff;
import com.spleefleague.spleef.game.spleef.power.effect.EffectHeatBolts;
import com.spleefleague.spleef.game.spleef.power.effect.EffectRollerSpades;
import com.spleefleague.spleef.player.SpleefPlayer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author NickM13
 */
public class Power extends DBEntity {
    
    private static final Map<Integer, Power>[] powers = new TreeMap[4];
    
    private static final String[] slotName = {"Passive", "Offensive", "Defensive", "Utility"};
    private static final Material[] slotItem = {Material.WOODEN_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE};
    
    public static void init() {
        MongoCollection<Document> collection = Spleef.getInstance().getPluginDB().getCollection("Powers");
        for (int i = 0; i < powers.length; i++) {
            powers[i] = new TreeMap<>();
            powers[i].put(300, new Power());
        }
        collection.find().iterator().forEachRemaining(doc -> {
            Power power;
            Integer slot = doc.get("slot", Integer.class);
            if (slot == null) {
                System.out.println("Issue loading Power: " + doc);
            } else {
                switch (doc.get("slot", Integer.class)) {
                    case 0: power = new PowerPassive(); break;
                    case 1: power = new PowerOffensive(); break;
                    case 2: power = new PowerDefensive(); break;
                    case 3: power = new PowerUtility(); break;
                    default: power = null; break;
                }
                if (power != null) {
                    power.load(doc);
                    powers[power.getSlot()].put(power.getDamage(), power);
                } else {
                    System.out.println("Issue loading Power: " + doc);
                }
            }
        });
    }
    
    public static Power getDefaultPower(int slot) {
        return (Power) powers[slot].values().toArray()[0];
    }
    
    public static Power getPower(int slot, int id) {
        if (!powers[slot].containsKey(id)) {
            return getDefaultPower(slot);
        }
        return powers[slot].get(id);
    }
    
    private static InventoryMenuItem createActivePowerMenuItem(int slot) {
        return InventoryMenuAPI.createItem()
                .setName(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActivePower(slot).getDisplayName();
                }).setDescription(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActivePower(slot).getFullDescription();
                }).setDisplayItem(cp -> {
                    SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                    return sp.getActivePower(slot).getItem();
                }).setCloseOnAction(false);
    }
    
    public static InventoryMenuItem createMenu(int slot) {
        InventoryMenuItem menuItem = InventoryMenuAPI.createItem()
                .setName(slotName[slot])
                .setDescription("Set your " + slotName[slot])
                .setDisplayItem(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.getActivePower(slot).getItem();
                })
                .createLinkedContainer("Active Power: " + slotName[slot]);
        
        for (Power power : powers[slot].values()) {
            InventoryMenuItem smi = InventoryMenuAPI.createItem()
                    .setName(cp -> {
                        return power.getDisplayName();
                    })
                    .setDisplayItem(cp -> {
                        return power.getItem();
                    })
                    .setDescription(cp -> {
                        return power.getFullDescription();
                    })
                    .setAction(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        sp.setActivePower(slot, power.getDamage());
                    })
                    .setCloseOnAction(false);
            menuItem.getLinkedContainer().addMenuItem(smi);
        }
        
        menuItem.getLinkedContainer().addStaticItem(createActivePowerMenuItem(slot), 4, 5);
        
        return menuItem;
    }
    
    public static InventoryMenuItem createMenu() {
        InventoryMenuItem menu = InventoryMenuAPI.createItem()
                .setName("Power")
                .setDescription("Set your active power")
                .setDisplayItem(cp -> {
                        SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                        return sp.getActivePower(0).getItem();
                        })
                .createLinkedContainer("Active Power");
        menu.getLinkedContainer().addStaticItem(createActivePowerMenuItem(0), 2, 5);
        menu.getLinkedContainer().addStaticItem(createActivePowerMenuItem(1), 3, 5);
        menu.getLinkedContainer().addStaticItem(createActivePowerMenuItem(2), 4, 5);
        menu.getLinkedContainer().addStaticItem(createActivePowerMenuItem(3), 5, 5);
        
        for (int slot = 0; slot < powers.length; slot++) {
            for (Power power : powers[slot].values()) {
                InventoryMenuItem smi = InventoryMenuAPI.createItem()
                        .setName(cp -> power.getDisplayName())
                        .setDisplayItem(cp -> power.getItem())
                        .setDescription(cp -> power.getFullDescription())
                        .setAction(cp -> {
                            SpleefPlayer sp = Spleef.getInstance().getPlayers().get(cp.getPlayer());
                            sp.setActivePower(power.getSlot(), power.getDamage());
                        })
                        .setCloseOnAction(false);
                menu.getLinkedContainer().addMenuItem(smi);
            }
        }
        
        return menu;
    }
    
    @DBField
    private Integer slot;
    @DBField
    private Integer damage;
    @DBField
    private String displayName;
    @DBField
    private String description;
    @DBField
    private Double cooldown;
    private final List<Effect> effects = new ArrayList<>();
    
    public Power() {
        slot = 0;
        damage = 300;
        displayName = "No Power";
        description = "";
        cooldown = 0.;
    }
    public Power(Power o) {
        slot = o.getSlot();
        damage = o.getDamage();
        displayName = o.getDisplayName();
        description = o.getDescription();
        cooldown = o.getCooldown();
        for (Effect e : o.getEffects()) {
            try {
                effects.add(e.getClass().getConstructor(e.getClass()).newInstance(e));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException ex) {
                Logger.getLogger(Power.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @DBLoad(fieldname="effects")
    public void loadEffects(List<Document> docs) {
        for (Document doc : docs) {
            Effect.EffectType type = Effect.EffectType.valueOf(doc.get("type", String.class));
            Effect effect = null;
            switch (type) {
                case ROLLER_SPADES:
                    effect = new EffectRollerSpades();
                    break;
                case BUFF:
                    effect = new EffectBuff();
                    break;
                case HEAT_BOLTS:
                    effect = new EffectHeatBolts();
                    break;
                case ICE_PILLARS:
                    
                    break;
            }
            if (effect != null) {
                effect.load(doc);
                effects.add(effect);
            }
        }
    }
    public List<Effect> getEffects() {
        return effects;
    }

    public boolean isReady(SpleefPlayer sp) {
        return sp.getPlayer().getCooldown(slotItem[slot]) <= 0;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public int getDamage() {
        return damage;
    }
    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(slotItem[slot]);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            ((Damageable) itemMeta).setDamage(damage);
        }
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public Material getMaterial() {
        return slotItem[slot];
    }
    public String getDisplayName() {
        return displayName;
    }
    public String getDescription() {
        return description;
    }
    public String getFullDescription() {
        String fullDescription = description + "\n";
        fullDescription += ChatColor.AQUA + "Cooldown: " + Chat.DEFAULT + cooldown + "s";
        return fullDescription;
    }
    public double getCooldown() {
        return cooldown;
    }
    
    public void activate(SpleefPlayer sp) {
        for (Effect effect : effects) {
            effect.activate(sp);
        }
        sp.getPlayer().setCooldown(slotItem[slot], (int) (cooldown * 20));
    }
    public void reset(SpleefPlayer sp) {
        for (Effect effect : effects) {
            effect.reset(sp);
        }
        sp.getPlayer().setCooldown(slotItem[slot], 0);
    }
    public void updateEffects(SpleefPlayer sp) {
        for (Effect effect : effects) {
            effect.updateEffect(sp);
        }
    }
    public void onBlockBreak(SpleefPlayer sp) {
        for (Effect effect : effects) {
            effect.onBlockBreak(sp);
        }
    }
    public void onMove(SpleefPlayer sp) {
        for (Effect effect : effects) {
            effect.onMove(sp);
        }
    }
    
}
