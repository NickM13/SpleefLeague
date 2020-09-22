package com.spleefleague.spleef.game.battle.power.ability;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.spleef.game.battle.power.PowerSpleefPlayer;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * @author NickM13
 * @since 9/20/2020
 */
public class AbilityStats {

    public static AbilityStats create() {
        return new AbilityStats();
    }

    private Ability.Type type = null;
    private Class<? extends Ability> clazz = null;
    private String name = "";
    private String description = "";
    private ItemStack displayItem = null;
    private int customModelData = 0;
    private int charges = 0;
    private double cooldown = 0D;
    private double refresh = 0D;

    private AbilityStats() { }

    public AbilityStats setAbilityType(Ability.Type type) {
        this.type = type;
        return this;
    }

    public Ability.Type getType() {
        return type;
    }

    public AbilityStats setAbilityClass(Class<? extends Ability> clazz) {
        this.clazz = clazz;
        return this;
    }

    public Ability create(PowerSpleefPlayer psp) {
        try {
            Ability ability = clazz.getDeclaredConstructor().newInstance();
            ability.init(psp, this);
            ability.reset();
            return ability;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public Class<? extends Ability> getAbilityClass() {
        return clazz;
    }

    public AbilityStats setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public AbilityStats setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AbilityStats setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public AbilityStats setUsage(int charges, double cooldown, double refreshCooldown) {
        this.charges = charges;
        this.cooldown = cooldown;
        this.refresh = refreshCooldown;
        return this;
    }

    public AbilityStats setUsage(double cooldown) {
        setUsage(1, cooldown, 0.25D);
        return this;
    }

    public int getCharges() {
        return charges;
    }

    public double getCooldown() {
        return cooldown;
    }

    public double getRefresh() {
        return refresh;
    }

    /**
     * For now just for building the Description string to include things like %charges%
     *
     * @return self
     */
    public AbilityStats build() {
        displayItem = InventoryMenuUtils.createCustomItem(type.getMaterial(), customModelData);

        StringBuilder formatted = new StringBuilder(Chat.DESCRIPTION);
        StringBuilder convertText = new StringBuilder();
        boolean converting = false;
        for (char c : description.toCharArray()) {
            if (c == '%') {
                if (converting) {
                    String str = convertText.toString();
                    switch (str) {
                        case "charges": formatted.append(charges); break;
                        case "cooldown": formatted.append(cooldown); break;
                        case "refresh": formatted.append(refresh); break;
                        default:
                            if (str.startsWith("X")) {
                                formatted.append(str.substring(1));
                            } else {
                                try {
                                    Field field = clazz.getDeclaredField(str);
                                    if (Modifier.isStatic(field.getModifiers())) {
                                        field.setAccessible(true);
                                        if (field.getType() == int.class) {
                                            formatted.append(field.getInt(null));
                                        } else if (field.getType() == double.class) {
                                            formatted.append(field.getDouble(null));
                                        } else if (field.getType() == float.class) {
                                            formatted.append(field.getFloat(null));
                                        } else if (field.getType() == long.class) {
                                            formatted.append(field.getLong(null));
                                        } else if (field.getType() == short.class) {
                                            formatted.append(field.getShort(null));
                                        } else {
                                            formatted.append("null");
                                        }
                                    }
                                } catch (NoSuchFieldException | IllegalAccessException exception) {
                                    exception.printStackTrace();
                                }
                            }
                            break;
                    }
                    formatted.append(Chat.DESCRIPTION);
                } else {
                    convertText = new StringBuilder();
                    formatted.append(Chat.STAT);
                }
                converting = !converting;
            } else {
                if (converting) {
                    convertText.append(c);
                } else {
                    formatted.append(c);
                }
            }
        }

        description = formatted.toString();

        String cooldownStr = getCooldownString();
        if (!cooldownStr.isEmpty()) {
            description = description + "\n\n" + cooldownStr;
        }

        return this;
    }

    private String getCooldownString() {
        if (charges == 0) {
            return "";
        } else if (charges == 1) {
            return Chat.DESCRIPTION + "Cooldown: " + Chat.STAT + cooldown + Chat.DESCRIPTION + " seconds.";
        } else {
            return Chat.DESCRIPTION + "Cooldown: " + Chat.STAT + refresh + Chat.DESCRIPTION + " seconds (" + Chat.STAT + cooldown + Chat.DESCRIPTION + " seconds/charge)";
        }
    }

}
